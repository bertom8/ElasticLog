package model;

import java.util.Date;
import java.util.List;

public class LogBuilder {
    private static Log log = new Log();

    public LogBuilder setDate(Date date) {
        log.setDate(date);
        return this;
    }

    public LogBuilder setCallStack(List<String> list) {
        log.setCallStack(list);
        return this;
    }

    public LogBuilder setResult(String result) {
        log.setResult(result);
        return this;
    }

    public LogBuilder setType(String type) {
        log.setType(type);
        return this;
    }

    public Log build() {
        return log;
    }
}
