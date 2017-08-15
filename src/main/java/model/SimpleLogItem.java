package model;

import com.sun.istack.internal.NotNull;

public class SimpleLogItem {
    @NotNull
    private SimpleLog log;
    private String id;

    public SimpleLog getLog() {
        return log;
    }

    public void setLog(final SimpleLog log) {
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

        final SimpleLogItem that = (SimpleLogItem) o;

        if (!log.equals(that.log)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        int result = log.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
