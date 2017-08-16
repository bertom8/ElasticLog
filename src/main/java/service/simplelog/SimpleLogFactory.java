package service.simplelog;

import model.Log;
import model.SimpleLog;
import model.SimpleLogItem;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class SimpleLogFactory {
    public static SimpleLogRest createLogRest(String indexName, String typeName) {
        return new SimpleLogRestImpl(indexName, typeName);
    }

    public static SimpleLogRest createLogRest() {
        return new SimpleLogRest() {
            @Override
            public SimpleLog getLog(String id) {
                return null;
            }

            @Override
            public Iterator<SimpleLogItem> getLogs(int documentPerScroll, StringBuilder generatedScrollId) {
                return null;
            }

            @Override
            public boolean createIndex() {
                return false;
            }

            @Override
            public boolean addLog(SimpleLog log) {
                return false;
            }

            @Override
            public boolean addLogs(List<SimpleLog> logs) {
                return false;
            }

            @Override
            public boolean changeToLog(String id, Log log) {
                return false;
            }

            @Override
            public boolean modifyIndexMappingforLog() {
                return false;
            }

            @Override
            public boolean removeLog(String id) {
                return false;
            }

            @Override
            public boolean removeIndex() {
                return false;
            }

            @Override
            public InputStream scrolling(int scroll, String scrollId) {
                return null;
            }
        };
    }
}
