package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadThread {
    private static final Logger logger = LoggerFactory.getLogger(UploadThread.class);
    private static UploadThread thread;

    private static boolean isStopped = true;

    public static void createServer() {
        thread = new UploadThread();
        isStopped = false;
        thread.startServer();
    }

    private UploadThread() {
    }

    private void startServer() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        final Runnable serverTask = () -> {
            try {
                final ServerSocket ss = new ServerSocket(getPort());
                while (!isStopped) {
                    final Socket clientSocket = ss.accept();
                    clientProcessingPool.submit(new ClientTask(clientSocket));
                }
            } catch (final IOException e) {
                logger.error(e.toString());
            }
        };
        final Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    private int getPort() {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
            return Integer.valueOf(properties.getProperty("portToServerSocket"));
        } catch (final IOException e) {
            logger.error(e.toString());
        }
        return 0;
    }

    public static void stopServer() {
        if (thread != null) {
            isStopped = true;
        }
    }

    public static boolean isIsStopped() {
        return isStopped;
    }
}
