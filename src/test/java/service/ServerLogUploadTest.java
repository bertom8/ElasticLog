package service;

import org.junit.Test;
import service.Log.ServerLogUploadService;

import static org.junit.Assert.assertTrue;

public class ServerLogUploadTest {

    @Test
    public void upload() throws Exception {
        new ServerLogUploadService().uploadLocalFile(
                "C:\\Users\\bereczki\\Downloads\\logs\\wildfly\\standalone\\log\\server.log.2017-08-02",
                "testServer");
        assertTrue(true);
    }

}