package com.flipkart.falcon.schema;

/**
 * Created by pradeep.joshi on 20/02/18.
 */
public interface CacheResponse<V> {

    String getString() ;
    V getResponse(String response) ;
}
