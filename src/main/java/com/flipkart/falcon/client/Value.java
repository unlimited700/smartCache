package com.flipkart.falcon.client;

/**
 * Created by pradeep.joshi on 03/11/17.
 */
public class Value<V> {
    V response ;
    MetaValue metaValue ;

    public Value() {
    }

    public Value(V response, MetaValue metaValue) {
        this.response = response;
        this.metaValue = metaValue;
    }

    public V getResponse() {
        return response;
    }

    public void setResponse(V response) {
        this.response = response;
    }

    public MetaValue getMetaValue() {
        return metaValue;
    }

    public void setMetaValue(MetaValue metaValue) {
        this.metaValue = metaValue;
    }

}
