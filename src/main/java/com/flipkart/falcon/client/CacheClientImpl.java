package com.flipkart.falcon.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.falcon.Provider.*;
import com.flipkart.falcon.commons.MetricUtils;
import com.flipkart.falcon.models.MetaValue;
import com.flipkart.falcon.models.RefreshStatus;
import com.flipkart.falcon.schema.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public class CacheClientImpl<K extends CacheKey, V> implements CacheClient<K, V> {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private CacheProvider<K, V> cacheProvider;
    private BackendServiceProvider<V> backendServiceProvider;
    private RefreshStrategyProvider refreshStrategyProvider;
    private int ttl = 900;
    private int backendThreadPoolSize = 150;
    private int backendTimeout = 140000 ;
    private int cacheThreadPoolSize = 150;
    private int cacheTimeout = 500000;
    private int defaultTimeout = 500000;
    private int defaultThreadPoolSize = 1 ;

    private static final Logger LOG = LoggerFactory.getLogger(CacheClientImpl.class) ;

    public V get(K key) {
        Value<V> cacheValueFromDB = getFromCache(key) ;
        return processCacheResponse(key,cacheValueFromDB) ;
    }

    private V processCacheResponse(K key,Value<V> cacheValueFromDB) {
        Value<V> value = new Value<V>();
        V backendServiceResponse;

        if (null == cacheValueFromDB) {
            long initTime = Calendar.getInstance().getTimeInMillis();
            //todo : discuss to have this execute part of client or application...
            backendServiceResponse = getFromBackend();//what if response is null...
            long finishTime = Calendar.getInstance().getTimeInMillis();
            System.out.println("cache-hit=false, key=" + CBProvider.getCacheKey(key.getString()));
            LOG.info("cache-hit=false, key=" + CBProvider.getCacheKey(key.getString()));
            new FetchAndSet(() -> {
                value.setResponse(backendServiceResponse);
                MetaValue metaValue = new MetaValue(ttl);
                value.setMetaValue(metaValue);

                processMetaDataAndInsertInDB(key, value, finishTime - initTime);
                return null;
            },cacheTimeout,cacheThreadPoolSize).queue();
            MetricUtils.getMeter(CacheClientImpl.class,"prob-cache-miss").mark();
        } else {
            key.setCacheHit(true);
            value.setMetaValue(cacheValueFromDB.getMetaValue());
            backendServiceResponse = (V) objectMapper.convertValue(cacheValueFromDB.getResponse(), backendServiceProvider.getResponseType());

            RefreshStatus refreshStatus = refreshStrategyProvider.shouldRefresh(value.getMetaValue()) ;

            if (null != value && null != refreshStatus ) {
                MetricUtils.getMeter(CacheClientImpl.class,"prob-refresh").mark();
                System.out.println("cache-hit=true, key=" + CBProvider.getCacheKey(key.getString()) + ", " + refreshStatus.toString() + ", expiryTime=" + value.getMetaValue().getExpiryTime() + ", delta=" + value.getMetaValue().getDelta());
                LOG.info("cache-hit=true, key=" + CBProvider.getCacheKey(key.getString()) + ", " + refreshStatus.toString() + ", expiryTime=" + value.getMetaValue().getExpiryTime() + ", delta=" + value.getMetaValue().getDelta());
                if( refreshStatus.isShouldRefresh() ){
                    key.setAsyncRefresh(true);
                    new FetchAndSet(() -> {
                        getFromBackendProcessMetaDataAndInsertInDB(key, value);
                        return null;
                    },defaultTimeout,defaultThreadPoolSize).queue();
                }

            }
            MetricUtils.getMeter(CacheClientImpl.class,"prob-cache-hit").mark();
        }
        return backendServiceResponse;
    }

    private Value<V> getFromCache(K key){
        return (Value<V>)new FetchAndSet(() -> { return cacheProvider.get(key); },cacheTimeout,cacheThreadPoolSize).execute();
    }

    private void putOnCache(K key, Value<V> value){
        new FetchAndSet(() -> { cacheProvider.put(key,value,ttl); return null ; },cacheTimeout,cacheThreadPoolSize).execute();
    }

    private void putOnCacheAsync(K key, Value<V> value){
        new FetchAndSet(() -> { cacheProvider.put(key,value,ttl); return null ; },cacheTimeout,cacheThreadPoolSize).queue();
    }

    private V getFromBackend(){
        try{
            return (V)new FetchAndSet(() -> { return backendServiceProvider.execute(); },backendTimeout,backendThreadPoolSize).execute();
        }catch (Exception ex){
            LOG.error("Exception while fetching from backend...");
            return (V)backendServiceProvider.getFallback();
        }


    }

    public void processMetaData(MetaValue metaValue, long reComputationTime) {//process MetaData and format value db compatible
        LOG.info("processing metadata...");

        if (null != metaValue) {
            metaValue.setDelta(reComputationTime);//todo : compute 99th percentile of reComputation time...
            metaValue.setExpiryTime(Calendar.getInstance().getTimeInMillis() + ttl*1000);
        }

        LOG.info("processed metadata...");
    }

    private void processMetaDataAndInsertInDB(K key, Value<V> value, long reComputationTime) {
        LOG.info("inside processMetaDataAndInsertInDB");

        if (null != value) {
            processMetaData(value.getMetaValue(), reComputationTime);

            LOG.info("current recomputation time : " + reComputationTime + ", ttl : " + ttl);
            //cacheProvider.put(key, value, ttl);
            putOnCacheAsync(key,value) ;
        } else {
            LOG.info("Exception!!! received value is null");
        }

    }

    private void getFromBackendProcessMetaDataAndInsertInDB(K key, Value<V> value) {
        LOG.info("inside getFromBackendProcessMetaDataAndInsertInDB");
        //todo : acquire lock on key and  decide what other simultaneous requests for same key will do...
        long initTime = Calendar.getInstance().getTimeInMillis();
        V backendServiceResponse = getFromBackend();
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
        //cacheProvider.put(key, new Value(value, new MetaValue()), ttl);//sync or async...?
        putOnCacheAsync(key,new Value(value, new MetaValue(ttl))) ;
    }

    public void invalidate(K key) {
    }

    @Override
    public Map<K, V> getBulk(Collection<K> keys) {
        Map<K,Value<V>> bulkResponse = cacheProvider.getBulk(keys) ;
        Map<K,V> response = new HashMap<K,V>() ;

        for(Map.Entry<K,Value<V>> entry : bulkResponse.entrySet()){
            response.put(entry.getKey(),processCacheResponse(entry.getKey(),entry.getValue())) ;
        }
        return response;
    }

    private CacheClientImpl(CacheBuilder cacheBuilder) {
        this.cacheProvider = cacheBuilder.cacheProvider;
        this.backendServiceProvider = cacheBuilder.backendServiceProvider;
        this.refreshStrategyProvider = cacheBuilder.refreshStrategyProvider;
        this.ttl = cacheBuilder.ttl;
    }

    public static class CacheBuilder<K extends CacheKey, V> {
        CacheProvider cacheProvider ;
        BackendServiceProvider backendServiceProvider;
        RefreshStrategyProvider refreshStrategyProvider = new ProbabilisticRefreshStrategyProvider(1,60000);
        int ttl = 900 ;
        private int backendThreadPoolSize = 150;
        private int backendTimeout = 140000 ;
        private int cacheThreadPoolSize = 150;
        private int cacheTimeout = 500000;
        private int defaultTimeout = 500000;
        private int defaultThreadPoolSize = 1 ;

        public CacheBuilder() {
        }

        public CacheBuilder<K, V> dbProvider(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
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

        public CacheBuilder<K, V> backendThreadPoolSize(int backendThreadPoolSize){
            this.backendThreadPoolSize = backendThreadPoolSize ;
            return this ;
        }

        public CacheBuilder<K, V> cacheThreadPoolSize(int cacheThreadPoolSize){
            this.cacheThreadPoolSize = cacheThreadPoolSize ;
            return this ;
        }

        public CacheBuilder<K, V> defaultThreadPoolSize(int defaultThreadPoolSize){
            this.defaultThreadPoolSize = defaultThreadPoolSize ;
            return this ;
        }

        public CacheBuilder<K, V> backendTimeout(int backendTimeout){
            this.backendTimeout = backendTimeout ;
            return this ;
        }

        public CacheBuilder<K, V> cacheTimeout(int cacheTimeout){
            this.cacheTimeout = cacheTimeout ;
            return this ;
        }

        public CacheBuilder<K, V> defaultTimeout(int defaultTimeout){
            this.defaultTimeout = defaultTimeout ;
            return this ;
        }

        public CacheClientImpl<K, V> build() {

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return new CacheClientImpl(this);
        }


    }
}
