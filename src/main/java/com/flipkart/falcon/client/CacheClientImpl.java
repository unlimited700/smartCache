package com.flipkart.falcon.client;

import com.couchbase.client.deps.com.fasterxml.jackson.databind.DeserializationFeature;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.falcon.Provider.*;
import com.flipkart.falcon.schema.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public class CacheClientImpl<K extends CacheKey, V> implements CacheClient<K, V> {

    static ObjectMapper objectMapper = new ObjectMapper();
    DBProvider<K, V> dbProvider;
    BackendServiceProvider<V> backendServiceProvider;
    RefreshStrategyProvider refreshStrategyProvider;
    int ttl;

    private static final Logger LOG = LoggerFactory.getLogger(CacheClientImpl.class) ;

    public V get(K key) {
        Value<V> cacheValueFromDB = dbProvider.get(key);
        Value<V> value = new Value<V>();
        V backendServiceResponse;

        LOG.info("get for key:" + key.getString());
        if (null == cacheValueFromDB) {
            long initTime = Calendar.getInstance().getTimeInMillis();
            //todo : discuss to have this execute part of client or application...
            backendServiceResponse = backendServiceProvider.execute();
            long finishTime = Calendar.getInstance().getTimeInMillis();
            System.out.println("Cache miss...");
            LOG.info("Cache miss...");
            new FetchAndSet(() -> {
                value.setResponse(backendServiceResponse);
                MetaValue metaValue = new MetaValue();
                value.setMetaValue(metaValue);

                processMetaDataAndInsertInDB(key, value, finishTime - initTime);
                return null;
            }).queue();

        } else {
            key.setCacheHit(true);
            value.setMetaValue(cacheValueFromDB.getMetaValue());
            backendServiceResponse = (V) objectMapper.convertValue(cacheValueFromDB.getResponse(), backendServiceProvider.getResponseType());
            System.out.println("Cache hit...");
            LOG.info("Cache hit...");

            if (null != value && refreshStrategyProvider.shouldRefresh(value.getMetaValue())) {
                key.setAsyncRefresh(true);
                new FetchAndSet(() -> {
                    getFromBackendProcessMetaDataAndInsertInDB(key, value);
                    return null;
                }).queue();
            }

        }

        return backendServiceResponse;
    }

    public void processMetaData(MetaValue metaValue, long reComputationTime) {//process MetaData and format value db compatible
        //System.out.println("processing metadata...");
        LOG.info("processing metadata...");

        if (null != metaValue) {
            metaValue.setDelta(reComputationTime);//todo : compute 99th percentile of reComputation time...
            metaValue.setExpiryTime(Calendar.getInstance().getTimeInMillis() + ttl*1000);
        }
        //System.out.println("processed metadata...");
        LOG.info("processed metadata...");
    }

    private void processMetaDataAndInsertInDB(K key, Value<V> value, long reComputationTime) {
        //System.out.println("inside processMetaDataAndInsertInDB");
        LOG.info("inside processMetaDataAndInsertInDB");

        if (null != value) {
            processMetaData(value.getMetaValue(), reComputationTime);
            //System.out.println("current recomputation time : " + reComputationTime);
            LOG.info("current recomputation time : " + reComputationTime + ", ttl : " + ttl);
            dbProvider.put(key, value, ttl);
        } else {
            //System.out.println("Exception!!! received value is null");
            LOG.info("Exception!!! received value is null");
        }

    }

    private void getFromBackendProcessMetaDataAndInsertInDB(K key, Value<V> value) {
        //System.out.println("inside getFromBackendProcessMetaDataAndInsertInDB");
        LOG.info("inside getFromBackendProcessMetaDataAndInsertInDB");
        //todo : acquire lock on key and  decide what other simultaneous requests for same key will do...
        long initTime = Calendar.getInstance().getTimeInMillis();
        V backendServiceResponse = backendServiceProvider.execute();
        long finishTime = Calendar.getInstance().getTimeInMillis();
        value.setResponse(backendServiceResponse);
        processMetaDataAndInsertInDB(key, value, finishTime - initTime);


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
        dbProvider.put(key, new Value(value, new MetaValue()), ttl);//sync or async...?
    }

    public void invalidate(K key) {
    }

    private CacheClientImpl(CacheBuilder cacheBuilder) {
        this.dbProvider = cacheBuilder.dbProvider;
        this.backendServiceProvider = cacheBuilder.backendServiceProvider;
        this.refreshStrategyProvider = cacheBuilder.refreshStrategyProvider;
        this.ttl = cacheBuilder.ttl;
    }

    public static class CacheBuilder<K extends CacheKey, V> {
        DBProvider dbProvider  = CBProvider.getInstance("default");
        BackendServiceProvider backendServiceProvider;
        RefreshStrategyProvider refreshStrategyProvider = new ProbabilisticRefreshStrategyProvider(1,60000);
        int ttl = 900 ;

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

        public CacheBuilder<K, V> ttl(int ttl) {
            this.ttl = ttl;
            return this;
        }

        public CacheClientImpl<K, V> build() {

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return new CacheClientImpl(this);
        }
    }
}
