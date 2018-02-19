package com.flipkart.falcon.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.falcon.Provider.*;
import com.flipkart.falcon.models.MetaValue;
import com.flipkart.falcon.models.RefreshStatus;
import com.flipkart.falcon.schema.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public class CacheClientImpl<K extends CacheKey, V> implements CacheClient<K, V> {

    static ObjectMapper objectMapper = new ObjectMapper();
    CacheProvider<K, V> cacheProvider;
    BackendServiceProvider<V> backendServiceProvider;
    RefreshStrategyProvider refreshStrategyProvider;
    int ttl;
    int backendServiceProviderThreadPoolSize ;
    int dbProviderThreadPoolSize ;

    private static final Logger LOG = LoggerFactory.getLogger(CacheClientImpl.class) ;

    public V get(K key) {
        Value<V> cacheValueFromDB = cacheProvider.get(key);
        Value<V> value = new Value<V>();
        V backendServiceResponse;

        if (null == cacheValueFromDB) {
            long initTime = Calendar.getInstance().getTimeInMillis();
            //todo : discuss to have this execute part of client or application...
            backendServiceResponse = backendServiceProvider.execute();//what if response is null...
            long finishTime = Calendar.getInstance().getTimeInMillis();
            System.out.println("cache-hit=false, key=" + key.getString());;
            LOG.info("cache-hit=false, key=" + key.getString());;
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

            RefreshStatus refreshStatus = refreshStrategyProvider.shouldRefresh(value.getMetaValue()) ;

            if (null != value && null != refreshStatus ) {
                System.out.println("cache-hit=true, key=" + CBProvider.getCacheKey(key.getString()) + ", " + refreshStatus.toString() + ", expiryTime=" + value.getMetaValue().getExpiryTime() + ", delta=" + value.getMetaValue().getDelta());
                LOG.info("cache-hit=true, key=" + CBProvider.getCacheKey(key.getString()) + ", " + refreshStatus.toString() + ", expiryTime=" + value.getMetaValue().getExpiryTime() + ", delta=" + value.getMetaValue().getDelta());
                if( refreshStatus.isShouldRefresh() ){
                    key.setAsyncRefresh(true);
                    new FetchAndSet(() -> {
                        getFromBackendProcessMetaDataAndInsertInDB(key, value);
                        return null;
                    }).queue();
                }

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
            cacheProvider.put(key, value, ttl);
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
        cacheProvider.put(key, new Value(value, new MetaValue()), ttl);//sync or async...?
    }

    public void invalidate(K key) {
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
        int backendServiceProviderThreadPoolSize ;
        int dbProviderThreadPoolSize ;

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

        public CacheBuilder<K, V> backendServiceProviderThreadPoolSize(int backendServiceProviderThreadPoolSize){
            this.backendServiceProviderThreadPoolSize = backendServiceProviderThreadPoolSize ;
            return this ;
        }

        public CacheBuilder<K, V> dbProviderThreadPoolSize(int dbProviderThreadPoolSize){
            this.dbProviderThreadPoolSize = dbProviderThreadPoolSize ;
            return this ;
        }

        public CacheClientImpl<K, V> build() {

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return new CacheClientImpl(this);
        }
    }
}
