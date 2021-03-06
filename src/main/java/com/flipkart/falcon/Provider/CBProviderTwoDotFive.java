/*
package com.flipkart.falcon.Provider;

import com.couchbase.client.deps.com.fasterxml.jackson.core.type.TypeReference;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.DeserializationFeature;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.transcoder.JsonTranscoder;
import com.couchbase.client.java.util.DigestUtils;
import com.flipkart.falcon.client.Value;
import com.flipkart.falcon.schema.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

*/
/*
Created by pradeep.joshi on 09/10/17.
*//*



public class CBProviderTwoDotFive<K extends CacheKey, V> implements CacheProvider<K, V> {

    static ObjectMapper objectMapper = new ObjectMapper();
    static ConcurrentHashMap<String,CacheProvider> store = new ConcurrentHashMap<String,CacheProvider>();
    static JsonTranscoder transcoder = new JsonTranscoder();
    private CouchbaseCluster cluster;
    private Bucket bucket;

    private static final Logger LOG = LoggerFactory.getLogger(CBProviderTwoDotFive.class) ;

    public void setConfigurations(String bucketName) {
        // Create a cluster reference
        cluster = CouchbaseCluster.create("10.33.217.224", "10.32.85.207", "10.32.189.201", "10.33.237.4");

        cluster.authenticate("Administrator", "sherlock");

        // Connect to the bucket and open it
        this.bucket = cluster.openBucket(bucketName);

        */
/*//*
/ Create a JSON document and store it with the ID "helloworld"
        JsonObject content = JsonObject.create().put("hello", "world!");
        content.put("flip","kart") ;
        JsonDocument inserted = bucket.upsert(JsonDocument.create("helloworld", content));
        content.put("hello","mumbai") ;
        inserted = bucket.upsert(JsonDocument.create("helloworld", content));

        // Read the document and print the "hello" field
        JsonDocument found = bucket.get("helloworld");

        System.out.println("Couchbase is the best database in the " + found.content().getString("hello"));
        //System.out.println("best place to work is flip " + found.content().getString("flip"));

        Map<String,String> value = new HashMap<String, String>();
        value.put("xyz","abc") ;
        JsonObject jsonObject = JsonObject.from(value) ;

        Map<String,Object> store = new CouchbaseMap<Object>("mapDocId1",bucket) ;
        store.put("key1",jsonObject) ;

        Object object = found.content() ;
        found = bucket.get("mapDocId");
        System.out.println("values for mapDocId : " + found.content().toString() + " object : " + object.toString());
        System.out.println("values for mapDocId.key1 : " + found.content().get("key1"));
        System.out.println("values for mapDocId.key1.xyz : " + found.content().getObject("key1").get("xyz"));



        // Close all buckets and disconnect
        // cluster.disconnect();*//*

    }


    private CBProviderTwoDotFive(String bucketName){
        setConfigurations(bucketName);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static CacheProvider getInstance(String bucketName){
        CacheProvider cbProvider = null ;
        if( null != store && store.containsKey(bucketName) )
            return store.get(bucketName) ;
        else{
            cbProvider = new CBProviderTwoDotFive(bucketName) ;
            store.put(bucketName,cbProvider) ;
        }


        return cbProvider ;
    }

    public Value<V> get(K key) {
        JsonDocument valueDocument = null;
        try {
            System.out.println(bucket.get(getCacheKey(key.getString())));
            if( null == key ) return null ;
            valueDocument = bucket.get(getCacheKey(key.getString()));

        } catch (Exception ex) {
            //System.out.println("Exception while fetching data from bucket for key : " + key + " Exception : " + ex.getMessage());
            LOG.error("Exception while fetching data from bucket for key : " + key + " Exception : " + ex.getMessage());
            ex.printStackTrace();
        }

        if (null == valueDocument) return null;

        Value<V> value = convertFromJsonObjectToValue(valueDocument.content());
        return value;
    }

    public void put(K key, Value<V> value, int ttl) {
        try {
            //System.out.println("inserting... ");
            LOG.info("inserting... ");
            JsonDocument inserted = bucket.upsert(JsonDocument.create(getCacheKey(key.getString()), ttl,convertToJsonObject(value)));
            //System.out.println("Successfully inserted "+inserted.id());
            LOG.info("Successfully inserted "+inserted.id());
        } catch (Exception ex) {
            //System.out.println("Exception while updating data from bucket for key : " + key + " Exception : " + ex.getMessage());
            LOG.error("Exception while updating data from bucket for key : " + key + " Exception : " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    public static String getCacheKey(String uri){
        if(uri==null){
            return null;
        }
        return DigestUtils.digestSha1Hex(uri) ;
    }

    public JsonObject convertToJsonObject(Value<V> object) {

        JsonObject response = JsonObject.create();
        try {

            response = transcoder.stringToJsonObject(objectMapper.writeValueAsString(object));

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Exception while converting to jsonObject by transcoder...");
        }
        return response;

    }

    public Value<V> convertFromJsonObjectToValue(JsonObject jsonObject) {

        Value<V> value = null;
        try {

            value = objectMapper.readValue(jsonObject.toString(), new TypeReference<Value<V>>() {
            });
        } catch (IOException e) {
            LOG.error("Exception while converting to Value by objectMapper...");
            e.printStackTrace();
        }
        return value;
    }


    public void invalidate(K key) {

    }
}
*/
