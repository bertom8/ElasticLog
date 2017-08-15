package service.Log;

import com.sun.istack.internal.NotNull;
import model.Log;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


public class LogRestImpl implements LogRest {
    private static RestClient restClient = null;
    private final Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    private final String endPoint;
    private final String indexName;

    private final Logger logger = LoggerFactory.getLogger(LogRestImpl.class);

    LogRestImpl(@NotNull final String indexName, @NotNull final String typeName) {
        this.indexName = indexName;
        this.endPoint = "/" + indexName + "/" + typeName + "/";
        getProperties();
    }

    private void getProperties() {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
            restClient = RestClient.builder(
                    new HttpHost(properties.getProperty("host"), Integer.valueOf(properties.getProperty("port")),
                            properties.getProperty("sheme"))).build();
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @return Index created successfully or not
     */
    @Override
    public boolean createIndex() {
        try {
            if (restClient.performRequest("PUT", "/" + indexName, new HashMap<>(),
                    EntityBuilder.create().setBinary(LogUtility.getLogPropertiesForCreate().getBytes()).build(), header)
                    .getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * @param id id of the log record
     * @return Log entity with that id, if it is exists
     */
    @Override
    public Log getLog(final String id) {
        try {
            final Log l = LogUtility.readLogJsonAsObjectStream(restClient.performRequest("GET",
                    endPoint + id, header).getEntity().getContent());
            System.out.println(l);
            return l;
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @return All Log entity in that index
     */
    @Override
    public List<Log> getLogs() {
        try {
            return LogUtility.readLogJsonStream(restClient.performRequest("GET",
                    "/" + indexName + "/" + "_search?pretty=true&q=*:*", header).getEntity().getContent());
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param log Log entity for add
     * @return Log add was successfully or not
     */
    @Override
    public boolean addLog(final Log log) {
        try {
            return restClient.performRequest("POST", endPoint, new HashMap<String, String>(),
                    EntityBuilder.create().setBinary(LogUtility.writeJsonStream(log)
                            .getBytes(StandardCharsets.UTF_8)).build(), header)
                    .getStatusLine().getStatusCode() == HttpStatus.SC_CREATED;
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean addLogs(final List<Log> logs) {
        System.out.println(LogUtility.writeJsonObjectList(logs));
        try {
            return restClient.performRequest("POST", endPoint + "_bulk", new HashMap<>(),
                    EntityBuilder.create().setSerializable(LogUtility.writeJsonObjectList(logs)).build(), header)
                    .getStatusLine().getStatusCode() == HttpStatus.SC_CREATED;
        } catch (final IOException e) {
            logger.error(e.toString(), e);
        }
        return false;
    }

    /**
     * @param id id of Log what it has to remove
     * @return Document removed successfully or not
     */
    @Override
    public boolean removeLog(final String id) {
        try {
            return restClient.performRequest("DELETE", endPoint + id, header).getStatusLine()
                    .getStatusCode() == HttpStatus.SC_OK;
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * @return Index removed successfully or not
     */
    @Override
    public boolean removeIndex() {
        try {
            return restClient.performRequest("DELETE", "/" + indexName, header).getStatusLine()
                    .getStatusCode() == HttpStatus.SC_OK;
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * @param filters filters to search
     * @return Founded logs
     */
    @Override
    public List<Log> searchLog(final String filters) {
        return null;
    }
}
