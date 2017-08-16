package service.simplelog;

import model.Log;
import model.LogItem;
import model.SimpleLogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

class SimpleLogConverter {
    private final SimpleLogRest logRest;
    private static final Logger logger = LoggerFactory.getLogger(SimpleLogConverter.class);

    SimpleLogConverter(String indexName, String typeName) {
        logRest = SimpleLogFactory.createLogRest(indexName, typeName);
    }

    void convertLogs() throws IOException {
        if (!logRest.modifyIndexMappingforLog()) {
            return;
        }
        final StringBuilder scrollId = new StringBuilder();
        Iterator<SimpleLogItem> simpleLogIterator = logRest.getLogs(5000, scrollId);
        final List<String> callstack = new ArrayList<>();
        final List<String> documentIdsForDelete = new LinkedList<>();
        LogItem log = null;
        logger.info("Merge...");
        while (simpleLogIterator.hasNext()) {
            final SimpleLogItem simpleLog = simpleLogIterator.next();
            LogItem newlog = merge(simpleLog, log, documentIdsForDelete, callstack);
            if (log != null && !newlog.equals(log)) {
                callstack.clear();
            }
            log = newlog;
        }
        logger.info("Delete...");
        for (final String aDocumentIdsForDelete : documentIdsForDelete) {
            logRest.removeLog(aDocumentIdsForDelete);
        }
    }

    private LogItem merge(final SimpleLogItem simpleLog, LogItem previouslogItem,
                          final List<String> documentIdsForDelete, List<String> callstack) {
        if (simpleLog.getLog().getResult()
                .matches("\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d\\ \\d\\d\\:\\d\\d\\:\\d\\d\\,\\d\\d\\d\\ .*")) {

            if (previouslogItem != null) {
                previouslogItem.getLog().setCallStack(callstack);
                logRest.changeToLog(previouslogItem.getId(), previouslogItem.getLog());
            }
            Log log = new Log();
            LogItem newlogItem = new LogItem();
            newlogItem.setLog(log);
            final String[] splitedLine = simpleLog.getLog().getResult().split(" ");
            try {
                newlogItem.getLog().setDate(new Date(
                        SimpleLogUtility.dateformat
                                .parse(splitedLine[0] + " " + splitedLine[1]).getTime() - 7200000));
            } catch (final ParseException e) {
                logger.error(e.getMessage(), e);
            }
            newlogItem.getLog().setType(splitedLine[2]);
            final StringBuilder result = new StringBuilder();

            for (int i = 3; i < splitedLine.length; i++) {
                result.append(" ").append(splitedLine[i]);
            }
            newlogItem.getLog().setResult(result.toString().trim());
            newlogItem.setId(simpleLog.getId());
            return newlogItem;
        } else {
            callstack.add(simpleLog.getLog().getResult().trim() + "\n");
            documentIdsForDelete.add(simpleLog.getId());
            return previouslogItem;
        }
    }
}
