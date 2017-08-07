package service;

import com.sun.istack.internal.NotNull;
import model.Log;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class LogRestImp implements LogRest {
    private static RestClient restClient = null;
    private final Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    private final String endPoint;
    private final String indexName;

    private final Logger logger = LoggerFactory.getLogger(LogRestImp.class);

    public LogRestImp(@NotNull final String indexName, @NotNull final String typeName) {
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

    @Override
    public boolean createIndex() {
        try {
            if (restClient.performRequest("PUT", "/" + indexName, header)
                    .getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    @Override
    public Log getLog(final long id) {
        try {
            return MapperUtility.readJsonAsObjectStream(restClient.performRequest("GET",
                    endPoint + id, header).getEntity().getContent());
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Log> getLogs() {
        try {
            return MapperUtility.readJsonStream(restClient.performRequest("PUT", "/" + indexName, header)
                    .getEntity().getContent());
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean addLog(final Log log) {
        try {
            restClient.performRequest("PUT", endPoint + log.getId(), header);
            // TODO:
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean removeLog(final long id) {
        try {
            return restClient.performRequest("DELETE", endPoint + id, header).getStatusLine()
                    .getStatusCode() == HttpStatus.SC_OK;
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
