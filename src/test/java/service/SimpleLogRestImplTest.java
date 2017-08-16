package service;

import model.SimpleLog;
import org.junit.Test;
import service.simplelog.SimpleLogFactory;
import service.simplelog.SimpleLogRest;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class SimpleLogRestImplTest {
    private final SimpleLogRest logRest = SimpleLogFactory.createLogRest("serverlog", "log");

    @Test
    public void createIndex() throws Exception {
        assertTrue(logRest.createIndex());
    }

    @Test
    public void addLog() throws Exception {
        SimpleLog log = new SimpleLog();
        log.setDate(new Date());
        log.setResult("Test1");
        assertTrue(logRest.addLog(log));
    }

    @Test
    public void removeIndex() throws Exception {
        assertTrue(logRest.removeIndex());
    }

}