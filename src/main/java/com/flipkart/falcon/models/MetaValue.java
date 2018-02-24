package com.flipkart.falcon.models;

import java.util.Calendar;

/**
 * Created by pradeep.joshi on 03/11/17.
 */
public class MetaValue {
    long delta = 0 ;
    long expiryTime ;

    public MetaValue(int ttl){
        this.expiryTime = Calendar.getInstance().getTimeInMillis() + ttl*1000 ;
    }

    public MetaValue(){}

    public MetaValue(long delta){
        this.delta = delta;
    }

    public long getDelta() {
        return delta;
    }

    public void setDelta(long delta) {
        this.delta = delta;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }
}
