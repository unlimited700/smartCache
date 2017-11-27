package com.flipkart.falcon.Provider;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public interface BackendServiceProvider<V> {
    V execute() ;
    V queue() ;
    Class getResponseType() ;
}
