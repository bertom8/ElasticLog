package service;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerLogUploadTest {

    @Test
    public void upload() throws Exception {
        ServerLogUploadService.upload(
                "C:\\Users\\bereczki\\Downloads\\logs\\wildfly\\standalone\\log\\server.log");
        assertTrue(true);
    }

}