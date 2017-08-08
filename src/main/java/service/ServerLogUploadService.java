package service;

import com.sun.istack.internal.NotNull;
import model.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ServerLogUploadService {
    private static LogRestImp imp = new LogRestImp("serverlog-2017-08-03", "log");
    private static Logger logger = LoggerFactory.getLogger(ServerLogUploadService.class);

    /**
     *
     * @param pathToFile Path to the log file with escaped backslashes
     */
    public static void upload(@NotNull String pathToFile) {
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

    /**
     *
     * @param reader
     * @throws IOException
     */
    private static void read(BufferedReader reader) throws IOException {
        String line;
        Log log = null;
        List<String> callStack = null;
        while ( (line = reader.readLine()) != null ) {
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
