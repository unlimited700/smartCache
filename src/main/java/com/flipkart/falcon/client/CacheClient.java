package com.flipkart.falcon.client;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public interface CacheClient<K,V> {
    V get(K key) ;
    void put(K key,V value) ;
    void invalidate(K key) ;
}
