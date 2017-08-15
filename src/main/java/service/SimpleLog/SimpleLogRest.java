package service.SimpleLog;

import model.Log;
import model.SimpleLog;
import model.SimpleLogItem;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public interface SimpleLogRest {
    SimpleLog getLog(String id);

    Iterator<SimpleLogItem> getLogs(int documentPerScroll, StringBuilder generatedScrollId);

    boolean createIndex();

    boolean addLog(SimpleLog log);

    boolean changeToLog(String id, Log log);

    boolean modifyIndexMappingforLog();

    boolean removeLog(String id);

    boolean removeIndex();

    List<SimpleLog> searchLog(String filters);

    InputStream scrolling(int scroll, String scrollId);
}
