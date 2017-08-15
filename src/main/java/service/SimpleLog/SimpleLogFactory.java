package service.SimpleLog;

import model.Log;
import model.SimpleLog;
import model.SimpleLogItem;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class SimpleLogFactory {
    public static SimpleLogRest createLogRest(final boolean rest) {
        if (rest) {
            return new SimpleLogRestImpl("serverlog", "log");
        } else {
            return new SimpleLogRest() {
                @Override
                public SimpleLog getLog(final String id) {
                    return null;
                }

                @Override
                public Iterator<SimpleLogItem> getLogs(final int documentPerScroll, final StringBuilder generatedScrollId) {
                    return null;
                }

                @Override
                public boolean createIndex() {
                    return false;
                }

                @Override
                public boolean addLog(final SimpleLog log) {
                    return false;
                }

                @Override
                public boolean changeToLog(final String id, final Log log) {
                    return false;
                }

                @Override
                public boolean modifyIndexMappingforLog() {
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
                public List<SimpleLog> searchLog(final String filters) {
                    return null;
                }

                @Override
                public InputStream scrolling(final int scroll, final String scrollId) {
                    return null;
                }
            };
        }
    }
}
