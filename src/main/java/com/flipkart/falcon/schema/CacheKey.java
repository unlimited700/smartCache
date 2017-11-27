package com.flipkart.falcon.schema;

/**
 * Created by pradeep.joshi on 10/11/17.
 */
public interface CacheKey<K> {

    public K getData();
    public boolean isCacheHit() ;
    public boolean isAsyncRefresh() ;
    public void setAsyncRefresh(boolean status) ;
    public void setCacheHit(boolean status) ;
    public String getString();

}
