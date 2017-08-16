package service.simplelog;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ServerSimpleLogUploadServiceTest {
    @Test
    public void uploadLocalFile() throws Exception {
        new ServerSimpleLogUploadService("serverlog", "log").uploadLocalFile(
                "C:\\Users\\bereczki\\Downloads\\logs\\wildfly\\standalone\\log\\server.log.2017-08-02");
        assertTrue(true);
    }

    @Test
    public void convertLogs() throws Exception {
        new SimpleLogConverter("serverlog", "log").convertLogs();
        assertTrue(true);
    }
}