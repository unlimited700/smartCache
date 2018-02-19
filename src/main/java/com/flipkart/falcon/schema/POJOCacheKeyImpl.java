package com.flipkart.falcon.schema;

import com.flipkart.falcon.Provider.CBProvider;
import com.flipkart.falcon.Provider.CacheProvider;
import com.flipkart.falcon.client.Value;
import com.flipkart.falcon.models.CBConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pradeep.joshi on 10/11/17.
 */
public class POJOCacheKeyImpl implements CacheKey<Map> {

    private final HashMap data;
    private boolean cacheHit = false ;
    private boolean asyncRefresh = false ;

    public POJOCacheKeyImpl(HashMap<String, String> data) {
        this.data = data;
    }

    @Override
    public HashMap<String, String> getData() {
        return data;
    }

    @Override
    public String getString() {
        return data.toString();
    }


    public boolean isCacheHit() {
        return cacheHit;
    }

    @Override
    public void setCacheHit(boolean cacheHit) {
        this.cacheHit = cacheHit;
    }

    @Override
    public boolean isAsyncRefresh() { return asyncRefresh ;}

    @Override
    public void setAsyncRefresh(boolean status) { this.asyncRefresh = status ;}


    public static void main(String[] args) {

        try {
            CBConfig cbConfig = new CBConfig() ;
            CacheProvider cbProvider = null;
            cbProvider = new CBProvider(cbConfig,10000);
            Value<String> value = new Value<String>() ;
            value.setResponse("random cache value");
            cbProvider.put(new StringCacheKeyImpl("random key"), value,900000);
        } catch (IOException e) {
            e.printStackTrace();
        }

    //    System.out.println(value.getResponse());

    }
}
