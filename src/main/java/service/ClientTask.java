package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.Log.ServerLogUploadService;

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
            getServer(fromClient);
            final String path = "log-" + new Date().getTime();
            String line;
            try (PrintWriter toFile = new PrintWriter(new FileOutputStream(path))) {
                while ((line = fromClient.readLine()) != null) {
//                    System.out.println(line);
                    toFile.println(line);
                    toFile.flush();
                }
            } catch (final IOException e) {
                logger.error(e.toString());
            }
            new ServerLogUploadService().uploadLocalFile(path, server);
            new File(path).delete();
        } catch (final IOException e) {
            logger.error(e.toString(), e);
        }
    }

    private void getServer(final BufferedReader reader) throws IOException {
        server = reader.readLine();
        if (server != null) {
            if (!server.matches("(#log:server=)[\\w|\\d]+")) {
                return;
            }
            server = server.substring(server.lastIndexOf("=") + 1);
        }
    }
}
