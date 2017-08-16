package service.SimpleLog;

import model.Log;
import model.LogItem;
import model.SimpleLogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class SimpleLogConverter {
    private final SimpleLogRest logRest = SimpleLogFactory.createLogRest(true);
    private static final Logger logger = LoggerFactory.getLogger(SimpleLogConverter.class);

    void convertLogs() throws IOException {
        if (!logRest.modifyIndexMappingforLog()) {
            return;
        }
        final StringBuilder scrollId = new StringBuilder();
        Iterator<SimpleLogItem> simpleLogIterator = logRest.getLogs(5000, scrollId);
        System.out.println(scrollId);
        final List<String> callstack = new ArrayList<>();
        final List<String> documentIdsForDelete = new LinkedList<>();
        final LogItem log = null;
        int i = 0;
        while (simpleLogIterator.hasNext()) {
            final SimpleLogItem simpleLog = simpleLogIterator.next();
            System.out.println(i++ + ". " + simpleLog.getLog());
            if (read(simpleLog, log, documentIdsForDelete, callstack)) {
                callstack.clear();
            }
        }
//        for (final String aDocumentIdsForDelete : documentIdsForDelete) {
//            logRest.removeLog(aDocumentIdsForDelete);
//        }
    }

    private boolean read(final SimpleLogItem simpleLog, LogItem logItem, final List<String> documentIdsForDelete,
                         List<String> callstack) {
        boolean hasChanged = false;
        //Stacktrace item
        if (simpleLog.getLog().getText()
                .matches("\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d\\ \\d\\d\\:\\d\\d\\:\\d\\d\\,\\d\\d\\d\\ .*")) {

            if (logItem != null) {
                logItem.getLog().setCallStack(callstack);
                hasChanged = logRest.changeToLog(logItem.getId(), logItem.getLog());
            }
            Log log = new Log();
            logItem = new LogItem();
            logItem.setLog(log);
            final String[] splitedLine = simpleLog.getLog().getText().split(" ");
            //log.setServerId(server);
            try {
                logItem.getLog().setDate(SimpleLogUtility.dateformat.parse(splitedLine[0] + " " + splitedLine[1]));
            } catch (final ParseException e) {
                logger.error(e.getMessage(), e);
            }
            logItem.getLog().setType(splitedLine[2]);
            final StringBuilder result = new StringBuilder();

            for (int i = 3; i < splitedLine.length; i++) {
                result.append(" ").append(splitedLine[i]);
            }
            logItem.getLog().setResult(result.toString().trim());
            logItem.setId(simpleLog.getId());
        }
        //callstack item
        else {
            callstack.add(simpleLog.getLog().getText());
            documentIdsForDelete.add(simpleLog.getId());
        }
        return hasChanged;
    }
}
