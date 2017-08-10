package service;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ServerLogUploadTest {

    @Test
    public void upload() throws Exception {
        ServerLogUploadService.uploadLocalFile(
                "C:\\Users\\bereczki\\Downloads\\logs\\wildfly\\standalone\\log\\server.log.2017-08-02");
        assertTrue(true);
    }

}