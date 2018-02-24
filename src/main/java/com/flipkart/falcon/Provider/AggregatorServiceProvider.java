package com.flipkart.falcon.Provider;

import com.flipkart.falcon.models.MetaValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public class AggregatorServiceProvider<T> implements BackendServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AggregatorServiceProvider.class) ;

    public T execute() {
        //System.out.println("inside AggregatorServiceProvider execute...");
        LOG.info("inside AggregatorServiceProvider execute...");
        MetaValue metaValue = new MetaValue(900);
        metaValue.setDelta(-1);
        return (T)metaValue;
    }

    @Override
    public Object getFallback() {
        return null;
    }

    @Override
    public T queue() {
        MetaValue metaValue = new MetaValue(900);
        metaValue.setDelta(1);
        return (T)metaValue;
    }

    public Class getResponseType(){

        return MetaValue.class;
    }
}
