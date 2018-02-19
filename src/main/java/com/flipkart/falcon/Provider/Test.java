package com.flipkart.falcon.Provider;

import com.flipkart.falcon.client.CacheClientImpl;
import com.flipkart.falcon.models.CBConfig;
import com.flipkart.falcon.models.MetaValue;
import com.flipkart.falcon.schema.StringCacheKeyImpl;

import java.io.IOException;

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
        CBConfig cbConfig = new CBConfig() ;
        try {
            CacheClientImpl<StringCacheKeyImpl, MetaValue> build = null;
            build = new CacheClientImpl.CacheBuilder<StringCacheKeyImpl, MetaValue>().
                    backendServiceProvider(new AggregatorServiceProvider()).
                    refreshStrategyProvider(new ProbabilisticRefreshStrategyProvider(1,60000)).dbProvider(new CBProvider(cbConfig,10000)).
                    build();



        StringCacheKeyImpl key3 = new StringCacheKeyImpl("key233") ;

            Object resp = build.get(key3);

            try {
                Thread.sleep(5000);
                build = new CacheClientImpl.CacheBuilder<StringCacheKeyImpl, MetaValue>().
                        backendServiceProvider(new AggregatorServiceProvider()).
                        refreshStrategyProvider(new ProbabilisticRefreshStrategyProvider(1,60000)).dbProvider(new CBProvider(cbConfig,10000)).
                        ttl(900).build();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public class Test4{

    }

    public static class Test5{

    }
}
