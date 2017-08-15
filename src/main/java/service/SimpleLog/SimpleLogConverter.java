package service.SimpleLog;

import model.LogItem;
import model.SimpleLogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SimpleLogConverter {
    private final SimpleLogRest logRest = SimpleLogFactory.createLogRest(true);
    private static final Logger logger = LoggerFactory.getLogger(SimpleLogConverter.class);

    public void convertLogs() throws IOException {
        if (!logRest.modifyIndexMappingforLog()) {
            return;
        }
        final StringBuilder scrollId = new StringBuilder();
        Iterator<SimpleLogItem> simpleLogIterator = logRest.getLogs(5000, scrollId);
        System.out.println(scrollId);
        final List<String> callstack = new ArrayList<>();
        final List<String> documentIdsForDelete = new LinkedList<>();
        final LogItem log = null;
        //int i = 0;
        while (simpleLogIterator.hasNext()) {
            final SimpleLogItem simpleLog = simpleLogIterator.next();
            //System.out.println(i++ + ". " + simpleLog.getLog());
            read(simpleLog, log, documentIdsForDelete, callstack);
        }
        List<SimpleLogItem> items;
        InputStream stream;
        while (!(items = SimpleLogUtility.readSimpleLogJsonStream(
                (stream = logRest.scrolling(20, scrollId.toString())), null)).isEmpty()) {
            simpleLogIterator = items.iterator();
            while (simpleLogIterator.hasNext()) {
                final SimpleLogItem simpleLog = simpleLogIterator.next();
                //System.out.println(i++ + ". " + simpleLog.getLog());
                read(simpleLog, log, documentIdsForDelete, callstack);
            }
            stream.close();
            for (final String aDocumentIdsForDelete : documentIdsForDelete) {
                logRest.removeLog(aDocumentIdsForDelete);
            }
        }
    }

    private void read(final SimpleLogItem simpleLog, final LogItem log, final List<String> documentIdsForDelete,
                      List<String> callstack) {
        //Stacktrace item
        if (simpleLog.getLog().getText()
                .matches("\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d\\ \\d\\d\\:\\d\\d\\:\\d\\d\\,\\d\\d\\d\\ .*")) {

            if (log != null) {
                log.getLog().setCallStack(callstack);
                callstack = new ArrayList<>();
                logRest.changeToLog(log.getId(), log.getLog());
            }
            final String[] splitedLine = simpleLog.getLog().getText().split(" ");
            //log.setServerId(server);
            try {
                assert log != null;
                log.getLog().setDate(SimpleLogUtility.dateformat.parse(splitedLine[0] + " " + splitedLine[1]));
            } catch (final ParseException e) {
                logger.error(e.getMessage(), e);
            }
            log.getLog().setType(splitedLine[2]);
            final StringBuilder result = new StringBuilder();
            for (int i = 3; i < splitedLine.length; i++) {
                result.append(" ").append(splitedLine[i]);
            }
            log.getLog().setResult(result.toString().trim());
            log.setId(simpleLog.getId());
        }

        //callstack item
        else {
            callstack.add(simpleLog.getLog().getText());
            documentIdsForDelete.add(simpleLog.getId());
        }

    }
}
