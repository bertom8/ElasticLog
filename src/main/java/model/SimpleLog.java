package model;

import java.io.Serializable;
import java.util.Date;

public class SimpleLog implements Serializable {
    private String text;
    private Date date;

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
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

        if (!text.equals(simpleLog.text)) {
            return false;
        }
        return date.equals(simpleLog.date);
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return date.toString() + " : " + text;
    }
}
