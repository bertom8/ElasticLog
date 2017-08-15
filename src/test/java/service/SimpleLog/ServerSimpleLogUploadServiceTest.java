package service.SimpleLog;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ServerSimpleLogUploadServiceTest {
    @Test
    public void uploadLocalFile() throws Exception {
        new ServerSimpleLogUploadService().uploadLocalFile(
                "C:\\Users\\bereczki\\Downloads\\logs\\wildfly\\standalone\\log\\server.log.2017-08-02",
                "testServer");
        new SimpleLogConverter().convertLogs();
        assertTrue(true);
    }

    @Test
    public void convertLogs() throws Exception {
        new SimpleLogConverter().convertLogs();
        assertTrue(true);
    }

}