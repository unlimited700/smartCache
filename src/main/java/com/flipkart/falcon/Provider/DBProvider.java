package com.flipkart.falcon.Provider;

import com.flipkart.falcon.client.Value;

/**
 *
 * Created by pradeep.joshi on 09/10/17.
 */
public interface DBProvider<K,V> {
    Value<V> get(K key) ;
    void put(K key,Value<V> value,int ttl) ;
    void invalidate(K key) ;
}
