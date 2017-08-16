package service.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.log.ServerLogUploadService;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientTask implements Runnable {
    private final Socket clientSocket;
    private String server;
    private static final Logger logger = LoggerFactory.getLogger(ClientTask.class);

    ClientTask(final Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            if (clientSocket == null) {
                logger.error("Socket was null");
                throw new IllegalArgumentException("Socket was null");
            }
            final BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            final String path = "log-" + new Date().getTime();
            String line;
            try (PrintWriter toFile = new PrintWriter(new FileOutputStream(path))) {
                while ((line = fromClient.readLine()) != null) {
                    toFile.println(line);
                    toFile.flush();
                }
            } catch (final IOException e) {
                logger.error(e.toString());
            }
            new ServerLogUploadService("serverlog", "log").uploadLocalFile(path);
            new File(path).delete();
        } catch (final IOException e) {
            logger.error(e.toString(), e);
        }
    }
}
