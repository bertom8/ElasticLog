package service.log;

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
    private final LogRest imp;
    private static final Logger logger = LoggerFactory.getLogger(ServerLogUploadService.class);

    public ServerLogUploadService(String indexName, String typeName) {
        imp = LogRestFactory.createLogRest(indexName, typeName);
    }

    /**
     * @param pathToFile Path to the log file with escaped backslashes
     */
    public void uploadLocalFile(@NotNull final String pathToFile) {
        if ("".equals(pathToFile)) {
            logger.error("Path is empty");
            throw new IllegalArgumentException("Path is empty!");
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(pathToFile)), StandardCharsets.UTF_8));
            read(reader);
            System.out.println("Upload finished!");
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (final IOException e) {
                logger.error(e.toString(), e);
            }
        }
    }

    public void uploadRemoteFile(@NotNull final String host, @NotNull final int port,
                                 @NotNull final String pathToFile) {
        if (host.isEmpty()) {
            logger.error("Host is empty");
            throw new IllegalArgumentException("Host is empty!");
        }
        HttpsURLConnection urlConnection = null;
        String localPathToFile = null;
        try {
            final URL url;
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
            urlConnection.getInputStream().close();
            urlConnection.connect();
        } catch (final IOException e) {
            logger.error(e.toString(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        assert localPathToFile != null;
        uploadLocalFile(localPathToFile);
        new File(localPathToFile).deleteOnExit();
    }

    private static String writeToFile(final InputStream inputStream) throws IOException {
        final String fileName = System.getProperty("user.dir")
                + new BigInteger(60, new SecureRandom()).toString(32);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final PrintWriter writer = new PrintWriter(fileName, StandardCharsets.UTF_8.displayName());
        reader.lines().forEach(writer::println);
        reader.close();
        writer.close();
        return fileName;
    }

    /**
     * @param reader BufferedReader to read File
     * @throws IOException It can throw it while reading
     */
    private void read(final BufferedReader reader) throws IOException {
        String line;
        // For bulk upload:
        // final List<Log> logList = new ArrayList<>();
        Log log = null;
        List<String> callStack = null;
        while ((line = reader.readLine()) != null) {
            // Log
            if (line.matches("\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d\\ \\d\\d\\:\\d\\d\\:\\d\\d\\,\\d\\d\\d\\ .*")) {
                if (log != null) {
                    log.setCallStack(callStack);
                    // For bulk upload, next line should be removed:
                    // logList.add(log);
                    imp.addLog(log);
                }
                log = new Log();
                callStack = new ArrayList<>();
                final String[] splitedLine = line.split(" ");
                try {
                    log.setDate(LogUtility.dateformat.parse(splitedLine[0] + " " + splitedLine[1]));
                } catch (final ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                log.setType(splitedLine[2]);
                final StringBuilder result = new StringBuilder();
                for (int i = 3; i < splitedLine.length; i++) {
                    result.append(" ").append(splitedLine[i]);
                }
                log.setResult(result.toString().trim());
            } else { // callStack item
                if (!line.isEmpty()) {
                    assert callStack != null;
                    callStack.add(line.trim() + "\n");
                }
            }
        }
        // For bulk upload:
        // imp.addLogs(logList);
    }
}
