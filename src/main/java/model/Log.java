package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Log implements Serializable {
    private Date date;
    private String type;
    private String result;
    private List<String> callStack;

    public Log() {}

    public Log(Date date, String type, String result, List<String> callStack) {
        this.date = date;
        this.type = type;
        this.result = result;
        this.callStack = callStack;
    }

    public Date getDate() { return date; }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Log log = (Log) o;

        if (!date.equals(log.date)) {
            return false;
        }
        if (!type.equals(log.type)) {
            return false;
        }
        if (!result.equals(log.result)) {
            return false;
        }
        return callStack != null ? callStack.equals(log.callStack) : log.callStack == null;
    }

    @Override
    public int hashCode() {
        int result1 = date.hashCode();
        result1 = 31 * result1 + type.hashCode();
        result1 = 31 * result1 + result.hashCode();
        result1 = 31 * result1 + (callStack != null ? callStack.hashCode() : 0);
        return result1;
    }

    @Override
    public String toString() {
        return date.toString()
                + " " + type + " " + result + "\n"
                + (callStack!=null ? callStack.toString():"");
    }
}
