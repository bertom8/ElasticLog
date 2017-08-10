package service;

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


public class LogRestImp implements LogRest {
    private static RestClient restClient = null;
    private final Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    private final String endPoint;
    private final String indexName;

    private final Logger logger = LoggerFactory.getLogger(LogRestImp.class);

    protected LogRestImp(@NotNull final String indexName, @NotNull final String typeName) {
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
            logger.error(e.getMessage());
        }
    }

    /**
     *
     * @return Index created successfully or not
     */
    @Override
    public boolean createIndex() {
        try {
            if (restClient.performRequest("PUT", "/" + indexName, new HashMap<>(),
                    EntityBuilder.create().setBinary(MapperUtility.getLogPropertiesForCreate().getBytes()).build(), header)
                    .getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     *
     * @param id id of the log record
     * @return Log entity with that id, if it is exists
     */
    @Override
    public Log getLog(final String id) {
        try {
            Log l = MapperUtility.readLogJsonAsObjectStream(restClient.performRequest("GET",
                    endPoint + id, header).getEntity().getContent());
            System.out.println(l);
            return l;
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     *
     * @return All Log entity in that index
     */
    @Override
    public List<Log> getLogs() {
        try {
            return MapperUtility.readLogJsonStream(restClient.performRequest("GET",
                    "/" + indexName + "/" + "_search?pretty=true&q=*:*", header).getEntity().getContent());
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param log Log entity for add
     * @return Log add was successfully or not
     */
    @Override
    public boolean addLog(final Log log) {
        try {
            return restClient.performRequest("POST", endPoint, new HashMap<String, String>(),
                    EntityBuilder.create().setBinary(MapperUtility.writeJsonStream(log)
                            .getBytes(StandardCharsets.UTF_8)).build(), header)
                    .getStatusLine().getStatusCode() == HttpStatus.SC_CREATED;
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     *
     * @param id id of Log what it has to remove
     * @return Document removed successfully or not
     */
    @Override
    public boolean removeLog(final String id) {
        try {
            return restClient.performRequest("DELETE", endPoint + id, header).getStatusLine()
                    .getStatusCode() == HttpStatus.SC_OK;
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     *
     * @return Index removed successfully or not
     */
    @Override
    public boolean removeIndex() {
        try {
            return restClient.performRequest("DELETE", "/" + indexName, header).getStatusLine()
                    .getStatusCode() == HttpStatus.SC_OK;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     *
     * @param filters
     * @return
     */
    @Override
    public List<Log> searchLog(String filters) {
        return null;
    }
}
