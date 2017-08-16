package service.log;

import model.Log;

import java.util.List;

public interface LogRest {

    boolean createIndex();

    Log getLog(String id);

    List<Log> getLogs();

    boolean addLog(Log log);

    boolean addLogs(List<Log> logs);

    boolean removeLog(String id);

    boolean removeIndex();
}
