package model;

public class LogItem {
    private Log log;
    private String id;

    public Log getLog() {
        return log;
    }

    public void setLog(final Log log) {
        this.log = log;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LogItem logItem = (LogItem) o;

        if (!log.equals(logItem.log)) {
            return false;
        }
        return id.equals(logItem.id);
    }

    @Override
    public int hashCode() {
        int result = log.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return id + " : " + log.toString();
    }
}
