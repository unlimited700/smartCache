package com.flipkart.falcon.Provider;

import com.flipkart.falcon.client.MetaValue;
import com.flipkart.falcon.commons.MetricUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public class ProbabilisticRefreshStrategyProvider implements RefreshStrategyProvider {

    private double beta = 1.0 ;
    private long gama = 60000 ;
    private static final Logger LOG = LoggerFactory.getLogger(ProbabilisticRefreshStrategyProvider.class) ;

    public ProbabilisticRefreshStrategyProvider(double beta,long gama) {
        this.beta = beta;
        this.gama = gama;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public void setGama(long gama) {
        this.gama = gama;
    }

    public boolean shouldRefresh(MetaValue metaValue) {
        if (null == metaValue) return false;
        //todo: in future would like to store 99th percentile of delta
        metaValue.setDelta(metaValue.getDelta());
        long currentTime = Calendar.getInstance().getTimeInMillis();
        double random = Math.random() ;
        double logRandom = Math.log(0.0000000001 + random);
        long predictiveTime = currentTime - Double.valueOf(metaValue.getDelta() * beta * logRandom).longValue() + gama ;
        boolean status = (predictiveTime >= metaValue.getExpiryTime());

        if( status ){
            if( currentTime + gama >= metaValue.getExpiryTime() )
                MetricUtils.getMeter(ProbabilisticRefreshStrategyProvider.class,"gama-async-refresh").mark();
            else
                MetricUtils.getMeter(ProbabilisticRefreshStrategyProvider.class,"beta-async-refresh").mark();
        }

        System.out.println("status: " + status + ", curTime: " + currentTime + ", predictiveTime: " + predictiveTime + ", expiryTime: " + metaValue.getExpiryTime() + ", delta:" + metaValue.getDelta() + ", logRandom:" + logRandom + ", random:" + random + ", beta:"+beta + ", gama:"+gama);
        LOG.info("status: " + status + ", curTime: " + currentTime + ", predictiveTime: " + predictiveTime + ", expiryTime: " + metaValue.getExpiryTime() + ", delta:" + metaValue.getDelta() + ", logRandom:" + logRandom + ", random:" + random + ", beta:"+beta + ", gama:"+gama);

        return status;
    }

}
