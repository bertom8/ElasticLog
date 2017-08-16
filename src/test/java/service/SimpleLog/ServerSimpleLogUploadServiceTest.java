package service.SimpleLog;

import model.Log;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class ServerSimpleLogUploadServiceTest {
    @Test
    public void uploadLocalFile() throws Exception {
        new ServerSimpleLogUploadService().uploadLocalFile(
                "C:\\Users\\bereczki\\Downloads\\logs\\wildfly\\standalone\\log\\server.log.2017-08-02",
                "testServer");
        //new SimpleLogConverter().convertLogs();
        assertTrue(true);
    }

    @Test
    public void convertLogs() throws Exception {
        new SimpleLogConverter().convertLogs();
        assertTrue(true);
    }

    @Test
    public void updateTest() throws Exception {
        Log log = new Log();
        log.setDate(new Date());
        log.setResult("asd");
        log.setType("warn");
        log.setCallStack(null);
        System.out.println(SimpleLogUtility.writeJsonStreamForUpdate(log));
    }

}