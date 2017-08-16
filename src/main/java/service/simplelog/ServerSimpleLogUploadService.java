package service.simplelog;

import com.sun.istack.internal.NotNull;
import model.SimpleLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ServerSimpleLogUploadService {
    private static final Logger logger = LoggerFactory.getLogger(ServerSimpleLogUploadService.class);

    private final SimpleLogRest imp;

    public ServerSimpleLogUploadService(String indexName, String typeName) {
        imp = SimpleLogFactory.createLogRest(indexName, typeName);
    }

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
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void read(final BufferedReader reader) throws IOException {
        String line;
        SimpleLog log = null;
        while ((line = reader.readLine()) != null) {
            log = new SimpleLog();
            if (!line.isEmpty()) {
                log.setResult(line);
                log.setDate(new Date());
                imp.addLog(log);
            }
        }
    }
}
