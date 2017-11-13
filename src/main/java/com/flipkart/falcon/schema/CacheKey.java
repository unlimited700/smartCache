package com.flipkart.falcon.schema;

/**
 * Created by pradeep.joshi on 10/11/17.
 */
public interface CacheKey<K> {

    public K getData();

    public String getString();

}
