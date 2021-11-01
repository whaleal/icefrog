package com.whaleal.icefrog.json.test.bean;

import java.util.List;

public class JsonRootBean {

    private int statusCode;
    private String message;
    private int skip;
    private int limit;
    private int total;
    private List<Data> data;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode( int statusCode ) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip( int skip ) {
        this.skip = skip;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit( int limit ) {
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal( int total ) {
        this.total = total;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData( List<Data> data ) {
        this.data = data;
    }
}
