package com.flipkart.falcon.Provider;

import com.flipkart.falcon.client.Value;

import java.util.Collection;
import java.util.Map;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public class RedisProvider<K,V> implements CacheProvider<K,V> {
    public Value<V> get(K key) {
        return null;
    }

    public void put(K key, Value<V> value,int ttl) {

    }

    public void invalidate(K key) {

    }

    @Override
    public Map<K, Value<V>> getBulk(Collection<K> keys) {
        return null;
    }
}
