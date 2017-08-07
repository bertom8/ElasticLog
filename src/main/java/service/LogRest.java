package service;

import model.Log;

import java.util.List;

public interface LogRest {

    boolean createIndex();

    Log getLog(long id);

    List<Log> getLogs();

    boolean addLog(Log log);

    boolean removeLog(long id);
}
