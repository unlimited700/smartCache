package com.flipkart.falcon.Provider;

import com.flipkart.falcon.commons.MetricUtils;
import com.flipkart.falcon.models.MetaValue;
import com.flipkart.falcon.models.ProbabilisticRefreshStatus;
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

    public ProbabilisticRefreshStatus shouldRefresh(MetaValue metaValue) {
        if (null == metaValue) return null;
        //todo: in future would like to store 99th percentile of delta
        metaValue.setDelta(metaValue.getDelta());
        long currentTime = Calendar.getInstance().getTimeInMillis();
        double random = Math.random() ;
        double logRandom = Math.log(0.0000000001 + random);
        long predictiveTime = currentTime - Double.valueOf(metaValue.getDelta() * beta * logRandom).longValue() + gama ;
        boolean shouldRefresh = (predictiveTime >= metaValue.getExpiryTime());
        boolean isGamaRefresh = (currentTime + gama >= metaValue.getExpiryTime()) ;

        if( shouldRefresh ){
            if( isGamaRefresh )
                MetricUtils.getMeter(ProbabilisticRefreshStrategyProvider.class,"gama-async-refresh").mark();
            else
                MetricUtils.getMeter(ProbabilisticRefreshStrategyProvider.class,"beta-async-refresh").mark();
        }

        ProbabilisticRefreshStatus refreshStatus = new ProbabilisticRefreshStatus(shouldRefresh,currentTime, random, logRandom, predictiveTime, isGamaRefresh,beta,gama) ;

        return refreshStatus;
    }

}
