package com.flipkart.falcon.schema;

import com.flipkart.falcon.Provider.CBProvider;
import com.flipkart.falcon.client.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pradeep.joshi on 10/11/17.
 */
public class POJOCacheKeyImpl implements CacheKey<Map> {

    private final HashMap data;

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

    public static void main(String[] args) {
        CBProvider<StringCacheKeyImpl, String> cbProvider = new CBProvider<>();
        Value<String> value = new Value<String>() ;
        value.setResponse("random cache value");
    //    System.out.println(value.getResponse());
        cbProvider.put(new StringCacheKeyImpl("random key"), value,900000);
    }
}
