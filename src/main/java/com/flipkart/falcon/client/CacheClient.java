package com.flipkart.falcon.client;

import java.util.Collection;
import java.util.Map;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public interface CacheClient<K,V> {
    public V get(K key) ;
    public void put(K key,V value) ;
    public void invalidate(K key) ;
    public Map<K, V> getBulk(Collection<K> keys);
}
