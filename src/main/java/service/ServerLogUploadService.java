package service;

import com.sun.istack.internal.NotNull;
import model.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ServerLogUploadService {
    private static LogRest imp = LogRestFactory.createLogRest(true);
    private static Logger logger = LoggerFactory.getLogger(ServerLogUploadService.class);

    /**
     * @param pathToFile Path to the log file with escaped backslashes
     */
    public static void uploadLocalFile(@NotNull String pathToFile) {
        if ("".equals(pathToFile)) {
            throw new IllegalArgumentException("Path is empty!");
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(pathToFile)), StandardCharsets.UTF_8));
            read(reader);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void uploadRemoteFile(@NotNull String host, @NotNull int port, @NotNull String pathToFile) {
        if (host.isEmpty()) {
            logger.error("Host is empty");
            throw new IllegalArgumentException("Host is empty!");
        }
        HttpsURLConnection urlConnection = null;
        String localPathToFile = null;
        try {
            URL url;
            if (pathToFile.isEmpty()) {
                url = new URL(host + port);
            } else {
                url = new URL(host + port + File.pathSeparator + pathToFile);
            }
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "text/plain");
            localPathToFile = writeToFile(urlConnection.getInputStream());
            urlConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        assert localPathToFile != null;
        uploadLocalFile(localPathToFile);
        new File(localPathToFile).deleteOnExit();
    }

    private static String writeToFile(InputStream inputStream) throws FileNotFoundException, UnsupportedEncodingException {
        String fileName = System.getProperty("user.dir")
                + new BigInteger(60, new SecureRandom()).toString(32);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter writer = new PrintWriter(fileName, StandardCharsets.UTF_8.displayName());
        reader.lines().forEach(writer::println);
        writer.close();
        return fileName;
    }

    /**
     * @param reader
     * @throws IOException
     */
    private static void read(BufferedReader reader) throws IOException {
        String line;
        Log log = null;
        List<String> callStack = null;
        while ((line = reader.readLine()) != null) {
            // Log
            if (line.matches("\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d\\ \\d\\d\\:\\d\\d\\:\\d\\d\\,\\d\\d\\d\\ .*")) {
                if (log != null) {
                    log.setCallStack(callStack);
                    imp.addLog(log);
                    System.out.println(log); // remove this
                }
                log = new Log();
                callStack = new ArrayList<>();
                String[] splitedLine = line.split(" ");
                try {
                    log.setDate(MapperUtility.dateformat.parse(splitedLine[0] + " " + splitedLine[1]));
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                }
                log.setType(splitedLine[2]);
                StringBuilder result = new StringBuilder();
                for (int i = 3; i < splitedLine.length; i++) {
                    result.append(" ").append(splitedLine[i]);
                }
                log.setResult(result.toString());
            } else { // callStack item
                if (!line.isEmpty()) {
                    assert callStack != null;
                    callStack.add(line + "\n");
                }
            }
        }
    }
}
