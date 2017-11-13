package com.flipkart.falcon.schema;

/**
 * Created by pradeep.joshi on 10/11/17.
 */
public class StringCacheKeyImpl implements CacheKey<String> {

    private final String data;

    public StringCacheKeyImpl(String data) {
        this.data = data;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public String getString() {
        return data;
    }
}
