package service;

import model.Log;

import java.util.List;

public class LogRestFactory {
    public static LogRest createLogRest(boolean rest) {
        if (rest) {
            return new LogRestImp("serverlog", "log");
        } else {
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
                public boolean removeLog(String id) {
                    return false;
                }

                @Override
                public boolean removeIndex() {
                    return false;
                }

                @Override
                public List<Log> searchLog(String filters) {
                    return null;
                }
            };
        }
    }
}
