package service.log;

import model.Log;

import java.util.List;

public class LogRestFactory {
    public static LogRest createLogRest(String indexName, String typeName) {
        return new LogRestImpl(indexName, typeName);
    }

    public static LogRest createLogRest() {
        return new LogRest() {
            @Override
            public boolean createIndex() {
                return false;
            }

            @Override
            public Log getLog(String id) {
                return null;
            }

            @Override
            public List<Log> getLogs() {
                return null;
            }

            @Override
            public boolean addLog(Log log) {
                return false;
            }

            @Override
            public boolean addLogs(List<Log> logs) {
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
        };
    }
}
