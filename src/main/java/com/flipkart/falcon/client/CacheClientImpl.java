package com.flipkart.falcon.client;

import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.falcon.Provider.BackendServiceProvider;
import com.flipkart.falcon.Provider.CBProvider;
import com.flipkart.falcon.Provider.DBProvider;
import com.flipkart.falcon.Provider.RefreshStrategyProvider;
import com.flipkart.falcon.schema.CacheKey;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public class CacheClientImpl<K, V> implements CacheClient<K, V> {

    static ObjectMapper objectMapper = new ObjectMapper();
    DBProvider<K,V> dbProvider;
    BackendServiceProvider<V> backendServiceProvider;
    RefreshStrategyProvider refreshStrategyProvider;
    long ttl ;

    public V get(K key) {
        V backendServiceResponse = null;
        Value<V> cacheValue =  dbProvider.get(key);

        if (null == cacheValue) {
            backendServiceResponse = backendServiceProvider.execute();
            cacheValue = getCacheValue(backendServiceResponse);
            dbProvider.put(key, cacheValue,ttl);//sync or async...?
        } else {

            if (refreshStrategyProvider.shouldRefresh()) {// do async...
                backendServiceResponse = backendServiceProvider.execute();
                cacheValue = getCacheValue(backendServiceResponse);

                dbProvider.put(key, cacheValue,ttl);
            }
        }
        backendServiceResponse = (V) objectMapper.convertValue(cacheValue.getResponse(),backendServiceProvider.getResponseType()) ;
        return backendServiceResponse;
    }

    public Value<V> getCacheValue(V backendServiceResponse) {
        Value cacheValue = new Value<V>();
        MetaValue metaValue = new MetaValue();
        metaValue.setDelta(1.3);
        cacheValue.setResponse(backendServiceResponse);
        cacheValue.setMetaValue(metaValue);
        return cacheValue;
    }

    public V convertToValue(Object jsonObject, Class type) {

        V value = null;
        try {
            value = (V) objectMapper.convertValue(jsonObject, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public void put(K key, V value) {
        dbProvider.put(key, new Value(value, new MetaValue()),ttl);//sync or async...?
    }

    public void invalidate(K key) {
    }

    private CacheClientImpl(CacheBuilder cacheBuilder) {
        this.dbProvider = cacheBuilder.dbProvider;
        this.backendServiceProvider = cacheBuilder.backendServiceProvider;
        this.refreshStrategyProvider = cacheBuilder.refreshStrategyProvider;
        this.ttl = cacheBuilder.ttl ;
    }

    public static class CacheBuilder<K extends CacheKey, V> {
        DBProvider dbProvider = new CBProvider<K, V>();
        BackendServiceProvider backendServiceProvider;
        RefreshStrategyProvider refreshStrategyProvider;
        long ttl = 900000 ;

        public CacheBuilder() {
        }

        public CacheBuilder<K, V> dbProvider(DBProvider dbProvider) {
            this.dbProvider = dbProvider;
            return this;
        }

        public CacheBuilder<K, V> backendServiceProvider(BackendServiceProvider backendServiceProvider) {
            this.backendServiceProvider = backendServiceProvider;
            return this;
        }

        public CacheBuilder<K, V> refreshStrategyProvider(RefreshStrategyProvider refreshStrategyProvider) {
            this.refreshStrategyProvider = refreshStrategyProvider;
            return this;
        }

        public CacheBuilder<K, V> setTtl(long ttl) {
            this.ttl = ttl;
            return  this ;
        }

        public CacheClientImpl<K, V> build() {
            return new CacheClientImpl(this);
        }
    }
}
