package model;

import java.io.Serializable;
import java.util.Date;

public class SimpleLog implements Serializable {
    private String result;
    private Date date;

    public String getResult() {
        return result;
    }

    public void setResult(final String text) {
        this.result = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SimpleLog simpleLog = (SimpleLog) o;

        return result.equals(simpleLog.result) && date.equals(simpleLog.date);
    }

    @Override
    public int hashCode() {
        int res = result.hashCode();
        res = 31 * res + date.hashCode();
        return res;
    }

    @Override
    public String toString() {
        return date.toString() + " : " + result;
    }
}
