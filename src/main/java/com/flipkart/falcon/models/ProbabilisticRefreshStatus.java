package com.flipkart.falcon.models;

/**
 * Created by pradeep.joshi on 12/01/18.
 */
public class ProbabilisticRefreshStatus extends RefreshStatus {

    double random;
    double logRandom;
    long predictiveTime;
    boolean isGamaRefresh;
    private double beta;
    private long gama;

    public ProbabilisticRefreshStatus(boolean shouldRefresh, long currentTime, double random, double logRandom, long predictiveTime, boolean isGamaRefresh, double beta, long gama) {
        this.shouldRefresh = shouldRefresh;
        this.currentTime = currentTime;
        this.random = random;
        this.logRandom = logRandom;
        this.predictiveTime = predictiveTime;
        this.isGamaRefresh = isGamaRefresh;
        this.beta = beta;
        this.gama = gama;
    }

    public boolean isShouldRefresh() {
        return shouldRefresh;
    }

    public void setShouldRefresh(boolean shouldRefresh) {
        this.shouldRefresh = shouldRefresh;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public double getRandom() {
        return random;
    }

    public void setRandom(double random) {
        this.random = random;
    }

    public double getLogRandom() {
        return logRandom;
    }

    public void setLogRandom(double logRandom) {
        this.logRandom = logRandom;
    }

    public long getPredictiveTime() {
        return predictiveTime;
    }

    public void setPredictiveTime(long predictiveTime) {
        this.predictiveTime = predictiveTime;
    }

    public boolean isGamaRefresh() {
        return isGamaRefresh;
    }

    public void setGamaRefresh(boolean gamaRefresh) {
        isGamaRefresh = gamaRefresh;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public long getGama() {
        return gama;
    }

    public void setGama(long gama) {
        this.gama = gama;
    }

    @Override
    public String toString() {
        return  "shouldRefresh=" + shouldRefresh +
                ", currentTime=" + currentTime +
                ", random=" + random +
                ", logRandom=" + logRandom +
                ", predictiveTime=" + predictiveTime +
                ", isGamaRefresh=" + isGamaRefresh +
                ", beta=" + beta +
                ", gama=" + gama ;
    }
}
