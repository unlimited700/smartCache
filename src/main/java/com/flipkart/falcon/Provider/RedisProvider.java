package com.flipkart.falcon.Provider;

import com.flipkart.falcon.client.Value;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public class RedisProvider<K,V> implements DBProvider<K,V> {
    public Value<V> get(K key) {
        return null;
    }

    public void put(K key, Value<V> value,long ttl) {

    }

    public void invalidate(K key) {

    }
}
