package model;

import java.util.Date;
import java.util.List;

public class Log {
    private long id;
    private Date date;
    private String type;
    private String result;
    private List<String> callStack;

    public Log() {}

    public Log(long id, Date date, String type, String result, List<String> callStack) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.result = result;
        this.callStack = callStack;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<String> getCallStack() {
        return callStack;
    }

    public void setCallStack(List<String> callStack) {
        this.callStack = callStack;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Log log = (Log) o;

        if (id != log.id) return false;
        if (date != null ? !date.equals(log.date) : log.date != null) return false;
        if (type != null ? !type.equals(log.type) : log.type != null) return false;
        if (result != null ? !result.equals(log.result) : log.result != null) return false;
        return callStack != null ? callStack.equals(log.callStack) : log.callStack == null;
    }

    @Override
    public int hashCode() {
        int result1 = (int) (id ^ (id >>> 32));
        result1 = 31 * result1 + (date != null ? date.hashCode() : 0);
        result1 = 31 * result1 + (type != null ? type.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        result1 = 31 * result1 + (callStack != null ? callStack.hashCode() : 0);
        return result1;
    }
}
