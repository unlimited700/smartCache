package com.flipkart.falcon.schema;

/**
 * Created by pradeep.joshi on 10/11/17.
 */
public class StringCacheKeyImpl implements CacheKey<String> {

    private final String data;
    private boolean cacheHit = false ;
    private boolean asyncRefresh = false ;

    public StringCacheKeyImpl(String data) {
        this.data = data;
    }

    public boolean isCacheHit() {
        return cacheHit;
    }

    @Override
    public void setCacheHit(boolean status) {
        this.cacheHit = status;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public String getString() {
        return data;
    }

    @Override
    public boolean isAsyncRefresh() { return asyncRefresh ;}

    @Override
    public void setAsyncRefresh(boolean status) { this.asyncRefresh = status ;}
}
