package service;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UploadThreadTest {

    @Test
    public void run() throws Exception {
        UploadThread.createServer();
        final Socket socket = new Socket("localhost", 8999);
        final PrintWriter dataOutputStream = new PrintWriter(socket.getOutputStream());
        final BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\bereczki\\Downloads\\logs\\wildfly\\standalone\\log\\server.log.2017-08-02"));
        final DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        reader.lines().forEach(line -> {
            dataOutputStream.println(line);
            dataOutputStream.flush();
        });
        dataOutputStream.close();
        socket.close();
        Thread.sleep(1000);

//        while (!dataInputStream.readBoolean()) {}
//        socket.close();

        /*Socket socket2 = new Socket("localhost", 8999);
        System.out.println(socket2.isConnected());
        DataOutputStream dataOutputStream1 = new DataOutputStream(socket2.getOutputStream());
        dataOutputStream1.writeUTF("ujszoveg");
        dataOutputStream1.flush();
        dataOutputStream1.close();
        socket2.close();*/
    }

    @Test
    public void startServer() throws Exception {
        UploadThread.createServer();
        while (!UploadThread.isIsStopped()) {
        }
    }

}