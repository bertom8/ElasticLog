package service;

import model.Log;

import java.util.List;

public interface LogRest {

    boolean createIndex();

    Log getLog(String id);

    List<Log> getLogs();

    boolean addLog(Log log);

    boolean removeLog(String id);

    boolean removeIndex();

    List<Log> searchLog(String filters);
}
