package com.flipkart.falcon.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CBConfig {

    private Set<String> hosts;
    private String bucket;
    private String password;
    private String pool;

    public CBConfig(Set<String> hosts, String bucketName, String password, String pool) {
        this.bucket = bucketName;
        this.password = password;
        this.pool = pool;
        this.hosts = makeRandomHosts(hosts);
    }

    public CBConfig() {
    }

    public Set<String> getHosts() {
        return hosts;
    }

    public void setHosts(Set<String> hosts) {
        this.hosts = makeRandomHosts(hosts);
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }

    private Set<String> makeRandomHosts(Set<String> hosts) {
        Set<String> randomHosts = new HashSet<String>();
        try {
            int[] randomHostIndices = new Random().ints(0, hosts.size()).distinct().limit(Math.min(3, hosts.size())).toArray();
            List<String> hostsAsList = new ArrayList<>(hosts);
            for (int i : randomHostIndices) {
                randomHosts.add(hostsAsList.get(i));
            }
        } catch(Exception e) {
            for (String host : hosts) {
                randomHosts.add(host);
                if (randomHosts.size() >= 3)
                    break;
            }
        }
        return randomHosts;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
