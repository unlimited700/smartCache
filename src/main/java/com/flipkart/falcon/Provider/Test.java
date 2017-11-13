package com.flipkart.falcon.Provider;

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
        MetaValue metaValue = new MetaValue();
        metaValue.setDelta(0.3);
        CacheClientImpl<StringCacheKeyImpl, MetaValue> build = new CacheClientImpl.CacheBuilder<StringCacheKeyImpl, MetaValue>().
                backendServiceProvider(AggregatorServiceProvider.getInstance()).
                refreshStrategyProvider(ProbablisticRefreshStrategyProvider.getInstance()).
                build();

        StringCacheKeyImpl key3 = new StringCacheKeyImpl("key3") ;

        MetaValue resp = build.get(key3);
        System.out.println("reComputation time : " + resp.getDelta());
    }
}
