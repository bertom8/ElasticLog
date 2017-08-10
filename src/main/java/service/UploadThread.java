package service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadThread {
    public static void createServer() {
        new UploadThread().startServer();
    }

    private void startServer() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = () -> {
            try {
                ServerSocket ss = new ServerSocket(8999);
                while (true) {
                    Socket clientSocket = ss.accept();
                    clientProcessingPool.submit(new ClientTask(clientSocket));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

}
