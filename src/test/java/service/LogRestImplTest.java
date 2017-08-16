package service;

import model.Log;
import org.junit.Test;
import service.log.LogRest;
import service.log.LogRestFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LogRestImplTest {
    private final LogRest logRest = LogRestFactory.createLogRest("serverlog", "log");

    @Test
    public void createIndex() throws Exception {
        assertTrue(logRest.createIndex());
    }

    @Test
    public void getLog() throws Exception {
        assertNotNull(logRest.getLog("AV3rMZbzlBJfl2Q7U1u7"));
    }

    @Test
    public void getLogs() throws Exception {
        final List<Log> list = logRest.getLogs();
        list.forEach(System.out::println);
    }

    @Test
    public void addLog() throws Exception {
        final Log log = new Log();
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        log.setDate(format.parse("2017-08-02 00:16:05,091"));
        log.setType("error");
        log.setResult("Fail");
        final List<String> stack = new ArrayList<>();
        stack.add("at no.ovitas.fkweb.web.util.DocumentStreamService.streamFile(DocumentStreamService.java:163)");
        stack.add("at no.ovitas.fkweb.web.util.DocumentStreamService.streamGeneral(DocumentStreamService.java:104)");
        log.setCallStack(stack);
        assertTrue(logRest.addLog(log));
    }

    @Test
    public void removeLog() throws Exception {
        assertTrue(logRest.removeLog("AV29SkKEjd91wETriiPC"));
    }

    @Test
    public void removeIndex() throws Exception {
        assertTrue(logRest.removeIndex());
    }

}