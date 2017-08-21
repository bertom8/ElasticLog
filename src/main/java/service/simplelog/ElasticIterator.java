package service.simplelog;

import model.SimpleLogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class ElasticIterator implements Iterator {
    private static final Logger logger = LoggerFactory.getLogger(ElasticIterator.class);

    private final SimpleLogRest logRest;
    private final String scrollId;
    private List<SimpleLogItem> list;
    private int current = 0;

    ElasticIterator(SimpleLogRest logRest, final List<SimpleLogItem> list, String scrollId) {
        this.logRest = logRest;
        this.list = list;
        this.scrollId = scrollId;
    }

    @Override
    public boolean hasNext() {
        if (current < list.size()) {
            return true;
        } else {
            current = 0;
            return getNextScroll();
        }
    }

    @Override
    public SimpleLogItem next() {
        if (!hasNext()) {
            return null;
        }
        return list.get(current++);
    }

    private boolean getNextScroll() {
        try (InputStream stream = logRest.scrolling(20, scrollId)) {
            list = SimpleLogUtility.readSimpleLogJsonStream(stream);
            return !list.isEmpty();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }
}
