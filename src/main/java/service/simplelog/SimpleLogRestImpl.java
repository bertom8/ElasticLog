package service.simplelog;

import com.sun.istack.internal.NotNull;
import model.Log;
import model.SimpleLog;
import model.SimpleLogItem;
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class SimpleLogRestImpl implements SimpleLogRest {

    private static final Logger logger = LoggerFactory.getLogger(SimpleLogRestImpl.class);
    private RestClient restClient = null;
    private final Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    private final String endPoint;
    private final String indexName;


    SimpleLogRestImpl(@NotNull final String indexName, @NotNull final String typeName) {
        this.indexName = indexName;
        this.endPoint = "/" + indexName + "/" + typeName + "/";
        initRestClient();
    }

    private void initRestClient() {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
            System.setProperties(properties);
            restClient = RestClient.builder(
                    new HttpHost(System.getProperty("host"), Integer.valueOf(System.getProperty("port")),
                            System.getProperty("scheme"))).build();
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param id Id of the wanted Log
     * @return Founded Log or null
     */
    @Override
    public SimpleLog getLog(final String id) {
        try (InputStream inputStream = restClient.performRequest("GET",
                endPoint + id, header).getEntity().getContent()) {
            return SimpleLogUtility.readSimpleLogJsonAsObjectStream(inputStream).getLog();
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param documentPerScroll How many document there be in one scroll page. 0 < documentPerScroll < 10000
     * @param generatedScrollId Id of Scroll
     * @return Iterator to get all logs
     */
    @Override
    public Iterator<SimpleLogItem> getLogs(final int documentPerScroll, final StringBuilder generatedScrollId) {
        if (documentPerScroll > 10000 || documentPerScroll < 1) {
            throw new IllegalArgumentException("Wrong size!");
        }
        try (InputStream stream = restClient.performRequest("GET",
                endPoint + "_search?scroll=20m", new HashMap<>(),
                EntityBuilder.create().setBinary(SimpleLogUtility.getSearchJSON(documentPerScroll).getBytes()).build(),
                header).getEntity().getContent()) {

            List<SimpleLogItem> list = SimpleLogUtility.readSimpleLogJsonStream(stream, generatedScrollId);
            return new ElasticIterator(this, list, generatedScrollId.toString());
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean createIndex() {
        try {
            if (restClient.performRequest("PUT", "/" + indexName, new HashMap<>(),
                    EntityBuilder.create().setBinary(SimpleLogUtility.getSimpleLogProperiesForCreate().getBytes())
                            .build(),
                    header)
                    .getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * @param log SimpleLog entity for add to ElasticSearch
     * @return Was this successfull
     */
    @Override
    public boolean addLog(final SimpleLog log) {
        try {
            return restClient.performRequest("POST", endPoint, new HashMap<>(),
                    EntityBuilder.create().setBinary(SimpleLogUtility.writeJsonStream(log).getBytes()).build(), header)
                    .getStatusLine().getStatusCode() == HttpStatus.SC_CREATED;
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean addLogs(List<SimpleLog> logs) {
        try {
            return restClient.performRequest("POST", endPoint + "_bulk", new HashMap<>(),
                    EntityBuilder.create().setBinary(SimpleLogUtility.writeJsonObjectList(logs).getBytes()).build(),
                    header)
                    .getStatusLine().getStatusCode() == HttpStatus.SC_CREATED;
        } catch (final IOException e) {
            logger.error(e.toString(), e);
        }
        return false;
    }

    /**
     * @param id  id of log for update
     * @param log Log entity for update
     * @return Was this successfull
     */
    @Override
    public boolean changeToLog(final String id, final Log log) {
        try {
            return restClient.performRequest("POST", endPoint + id + "/_update?pretty",
                    new HashMap<>(),
                    EntityBuilder.create().setBinary(SimpleLogUtility.writeJsonStreamForUpdate(log).getBytes()).build(),
                    header)
                    .getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean modifyIndexMappingforLog() {
        try {
            return restClient.performRequest("POST",
                    "/" + indexName + "/_mapping" + endPoint.substring(indexName.length() + 1),
                    new HashMap<>(),
                    EntityBuilder.create().setBinary(SimpleLogUtility.getModifyIndexForLog().getBytes()).build(),
                    header).getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * @param id Id of document
     * @return Was this successfull
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
     * @param scroll   Lifetime of scroll in minute
     * @param scrollId Id of scroll
     * @return InputStream that is contains response json
     */
    @Override
    public InputStream scrolling(final int scroll, final String scrollId) {
        try {
            return restClient.performRequest("POST", "/_search/scroll", new HashMap<>(),
                    EntityBuilder.create().setText(SimpleLogUtility.getScrollJSON(scroll, scrollId)).build(), header)
                    .getEntity().getContent();
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
