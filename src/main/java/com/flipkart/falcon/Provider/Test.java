package com.flipkart.falcon.Provider;

import com.couchbase.client.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.falcon.schema.StringCacheKeyImpl;
import com.flipkart.falcon.client.CacheClientImpl;
import com.flipkart.falcon.client.MetaValue;

/**
 * Created by pradeep.joshi on 31/10/17.
 */
public class Test {
    public static class Test2 {
        public class Test3 {

        }
    }

    public static void main(String[] args)  {
        MetaValue metaValue  ;

        CacheClientImpl<StringCacheKeyImpl, MetaValue> build = new CacheClientImpl.CacheBuilder<StringCacheKeyImpl, MetaValue>().
                backendServiceProvider(new AggregatorServiceProvider()).
                refreshStrategyProvider(new ProbabilisticRefreshStrategyProvider(1,60000)).dbProvider(CBProvider.getInstance("falcon")).
                build();

        StringCacheKeyImpl key3 = new StringCacheKeyImpl("key133") ;

        Object resp = build.get(key3);
        try {
            System.out.println("reComputation time : " + new ObjectMapper().writeValueAsString(resp.toString()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(5000);
            build = new CacheClientImpl.CacheBuilder<StringCacheKeyImpl, MetaValue>().
                    backendServiceProvider(new AggregatorServiceProvider()).
                    refreshStrategyProvider(new ProbabilisticRefreshStrategyProvider(1,60000)).dbProvider(CBProvider.getInstance("falcon")).
                    ttl(900).build();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
