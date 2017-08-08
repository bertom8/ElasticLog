package service;

import model.Log;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class LogRestImpTest {
    private LogRestImp logRest = new LogRestImp("serverlog-2017-08-03", "log");

    @Test
    public void createIndex() throws Exception {
        assertTrue(logRest.createIndex());
    }

    @Test
    public void getLog() throws Exception {
        assertNotNull(logRest.getLog("AV29RSkijd91wETriiO_"));
    }

    @Test
    public void getLogs() throws Exception {
        List<Log> list = logRest.getLogs();
        list.forEach(System.out::println);
    }

    @Test
    public void addLog() throws Exception {
        Log log = new Log();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        log.setDate(format.parse("2017-08-02 00:16:05,091"));
        log.setType("error");
        log.setResult("Fail");
        List<String> stack = new ArrayList<>();
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

    @Test
    public void searchLog() throws Exception {

    }

}