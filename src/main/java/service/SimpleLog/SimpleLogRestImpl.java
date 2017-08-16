package service.SimpleLog;

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

    private static RestClient restClient = null;
    private final Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    private final String endPoint;
    private final String indexName;

    private final Logger logger = LoggerFactory.getLogger(SimpleLogRestImpl.class);

    SimpleLogRestImpl(@NotNull final String indexName, @NotNull final String typeName) {
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
                    EntityBuilder.create().setBinary(SimpleLogUtility.getSimpleLogProperiesForCreate().getBytes()).build(),
                    header)
                    .getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
        } catch (final IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

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
    public boolean changeToLog(final String id, final Log log) {
        //TODO: implement this
        try {
            return restClient.performRequest("POST", endPoint + id + "/_update?pretty", new HashMap<>(),
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

    @Override
    public List<SimpleLog> searchLog(final String filters) {
        //TODO: implement this
        return null;
    }


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
