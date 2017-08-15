package service.Log;

import model.Log;

import java.util.List;

public class LogRestFactory {
    public static LogRest createLogRest(final boolean rest) {
        if (rest) {
            return new LogRestImpl("serverlog", "log");
        } else {
            return new LogRest() {
                @Override
                public boolean createIndex() {
                    return false;
                }

                @Override
                public Log getLog(final String id) {
                    return null;
                }

                @Override
                public List<Log> getLogs() {
                    return null;
                }

                @Override
                public boolean addLog(final Log log) {
                    return false;
                }

                @Override
                public boolean addLogs(final List<Log> logs) {
                    return false;
                }

                @Override
                public boolean removeLog(final String id) {
                    return false;
                }

                @Override
                public boolean removeIndex() {
                    return false;
                }

                @Override
                public List<Log> searchLog(final String filters) {
                    return null;
                }
            };
        }
    }
}
