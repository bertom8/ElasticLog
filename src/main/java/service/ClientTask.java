package service;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;

public class ClientTask implements Runnable {
    private final Socket clientSocket;

    ClientTask(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            System.out.println("asd");
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String path = new BigInteger(60, new SecureRandom()).toString(32);
            final OutputStream toFile = new FileOutputStream(path);
            System.out.println("987");
            System.out.println("qwe");
            int bytesRead;
            String line;
            try {
                while ((line = fromClient.readLine()) != null) {
                    System.out.println(line);
                    //toFile.write(line.getBytes(StandardCharsets.UTF_8));
                    //toFile.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    toFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
