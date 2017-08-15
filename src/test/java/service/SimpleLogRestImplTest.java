package service;

import org.junit.Test;
import service.SimpleLog.SimpleLogFactory;
import service.SimpleLog.SimpleLogRest;

import static org.junit.Assert.assertTrue;

public class SimpleLogRestImplTest {
    private final SimpleLogRest logRest = SimpleLogFactory.createLogRest(true);

    @Test
    public void getLog() throws Exception {
    }

    @Test
    public void getLogs() throws Exception {
    }

    @Test
    public void createIndex() throws Exception {
        assertTrue(logRest.createIndex());
    }

    @Test
    public void addLog() throws Exception {
    }

    @Test
    public void removeLog() throws Exception {
    }

    @Test
    public void removeIndex() throws Exception {
        assertTrue(logRest.removeIndex());
    }

    @Test
    public void searchLog() throws Exception {
    }

}