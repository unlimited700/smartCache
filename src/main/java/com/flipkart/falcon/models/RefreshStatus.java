package com.flipkart.falcon.models;

/**
 * Created by pradeep.joshi on 12/01/18.
 */
public class RefreshStatus {

    boolean shouldRefresh ;//status of refresh
    long currentTime ;

    public boolean isShouldRefresh() {
        return shouldRefresh;
    }

    @Override
    public String toString() {
        return "RefreshStatus{" +
                "shouldRefresh=" + shouldRefresh +
                ", currentTime=" + currentTime +
                '}';
    }
}
