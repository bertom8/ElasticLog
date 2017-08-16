package service.simplelog;

import com.sun.istack.internal.NotNull;
import model.SimpleLog;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

        try (InputStream stream = new FileInputStream(new File(pathToFile))) {

            List<SimpleLog> logs = IOUtils.readLines(stream, "UTF8")
                    .stream()
                    .filter(line -> !line.isEmpty())
                    .map(this::parseLine)
                    .collect(Collectors.toList());

            imp.addLogs(logs);

        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private SimpleLog parseLine(String line) {
        SimpleLog log = new SimpleLog();
        log.setResult(line);
        log.setDate(new Date());

        return log;
    }
}
