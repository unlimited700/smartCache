package com.flipkart.falcon.Provider;

import com.flipkart.falcon.client.MetaValue;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public class AggregatorServiceProvider<T> implements BackendServiceProvider {

    public T execute() {
        MetaValue metaValue = new MetaValue();
        metaValue.setDelta(0.3);
        return (T)metaValue;
    }

    public Class getResponseType(){

        return MetaValue.class;
    }

    public static AggregatorServiceProvider getInstance() {
        return new AggregatorServiceProvider() ;
    }
}
