package service.SimpleLog;

import com.sun.istack.internal.NotNull;
import model.SimpleLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ServerSimpleLogUploadService {
    private static final Logger logger = LoggerFactory.getLogger(ServerSimpleLogUploadService.class);

    private final SimpleLogRest imp = SimpleLogFactory.createLogRest(true);

    public void uploadLocalFile(@NotNull final String pathToFile, @NotNull final String serverName) {
        if ("".equals(pathToFile)) {
            logger.error("Path is empty");
            throw new IllegalArgumentException("Path is empty!");
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(pathToFile)), StandardCharsets.UTF_8));
            read(reader, serverName);
            System.out.println("Upload finished!");
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (final IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void read(final BufferedReader reader, final String serverName) throws IOException {
        String line;
        //final List<SimpleLog> logList = new ArrayList<>();
        SimpleLog log = null;
        // final List<String> callStack = null;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            System.out.println(i + ":  " + line);
            i++;
            // Log
            log = new SimpleLog();
            if (!line.isEmpty()) {
                log.setText(line);
                log.setDate(new Date());
                imp.addLog(log);
            }
        }
    }
}
