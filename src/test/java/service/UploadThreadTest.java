package service;

import org.junit.Test;

import java.io.DataOutputStream;
import java.net.Socket;

public class UploadThreadTest {
    @Test
    public void run() throws Exception {
        UploadThread.createServer();
        Socket socket = new Socket("localhost", 8999);

        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataOutputStream.writeUTF("2017-07-31 00:06:12,713 WARN  [nlh-graviditet-amming] (default task-109) NLH:" +
                " hydroksokobalamin (vitamin b12) requested but not received for chapter amming");
        dataOutputStream.flush();
        dataOutputStream.close();
        socket.close();


        Socket socket2 = new Socket("localhost", 8999);
        System.out.println(socket2.isConnected());
        DataOutputStream dataOutputStream1 = new DataOutputStream(socket2.getOutputStream());
        dataOutputStream1.writeUTF("ujszoveg");
        dataOutputStream1.flush();
        dataOutputStream1.close();
        socket2.close();
    }

}