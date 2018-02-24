package com.flipkart.falcon.Provider;

import com.flipkart.falcon.client.Value;

import java.util.Collection;
import java.util.Map;

/**
 *
 * Created by pradeep.joshi on 09/10/17.
 */
public interface CacheProvider<K,V> {
    Value<V> get(K key) ;
    void put(K key,Value<V> value,int ttl) ;
    void invalidate(K key) ;
    public Map<K, Value<V>> getBulk(Collection<K> keys);
}
