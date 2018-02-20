package com.flipkart.falcon.Provider;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.falcon.client.Value;
import com.flipkart.falcon.models.CBConfig;
import com.flipkart.falcon.schema.CacheKey;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by pradeep.joshi on 13/02/18.
 */
public class CBProvider <K extends CacheKey, V > implements CacheProvider<K, V> {

    private static final Logger LOG = LoggerFactory.getLogger(CBProvider.class) ;
    static ObjectMapper objectMapper = new ObjectMapper();
    private static final int DEFAULT_TIMEOUT = 1000;
    private String bucketName;
    private CouchbaseClient cacheClient;

    private List<URI> getClusterConfiguration(Set<String> hosts) {
        List<URI> cbCluster = new ArrayList<URI>();
        for (String cbHost : hosts) {
            try {
                cbCluster.add(new URI(cbHost));
            } catch (URISyntaxException e) {
                LOG.error("Couchbase host configuration has an issue : {}", e.getMessage());
            }
        }
        LOG.info("Initializing CouchBase connection, using : {}", cbCluster);
        return cbCluster;

    }

    public CBProvider(CBConfig cbConfig, int opTimeout)  throws IOException{

        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
        CouchbaseConnectionFactoryBuilder builder = new CouchbaseConnectionFactoryBuilder();
        builder.setOpTimeout(opTimeout);
        CouchbaseConnectionFactory connectionFactory = builder.buildCouchbaseConnection(getClusterConfiguration(cbConfig.getHosts()), cbConfig.getBucket(), cbConfig.getPassword());

        LOG.info("Couchbase bucket config host:" + cbConfig.getHosts() + "bucket name: " + cbConfig.getBucket() + " password: " + cbConfig.getPassword());
        this.cacheClient = new CouchbaseClient(connectionFactory);
        this.bucketName = cbConfig.getBucket();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Value<V> get(K key) {
        Object response = null ;
        try {

            if( null == key ) return null ;
            response = cacheClient.get(getCacheKey(key.getString()));

        } catch (Exception ex) {
            LOG.error("Exception while fetching data from bucket for key : " + key + " Exception : " + ex.getMessage());
            ex.printStackTrace();
        }

        if (null == response) return null;

        Value<V> value = convertFromJsonObjectToValue(new String((byte [])response, StandardCharsets.UTF_8));
        return value;
    }

    public void put(K key, Value<V> value, int ttl) {
        try {

            cacheClient.set(getCacheKey(key.getString()),ttl,objectMapper.writeValueAsString(value).getBytes(StandardCharsets.UTF_8)) ;

        } catch (Exception ex) {
            LOG.error("Exception while updating data from bucket for key : " + key + " Exception : " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    public static String getCacheKey(String uri){
        if(uri==null){
            return null;
        }
        return DigestUtils.md5Hex(uri);
    }

    public Value<V> convertFromJsonObjectToValue(String object) {

        Value<V> value = null;
        try {
            value = objectMapper.readValue(object, new TypeReference<Value<V>>() {
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
