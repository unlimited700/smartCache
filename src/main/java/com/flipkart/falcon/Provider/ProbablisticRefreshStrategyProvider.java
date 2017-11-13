package com.flipkart.falcon.Provider;

import java.util.Calendar;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public class ProbablisticRefreshStrategyProvider implements RefreshStrategyProvider {
    public Boolean shouldRefresh() {
        return Calendar.getInstance().getTimeInMillis()%2 == 0;
    }

    public static ProbablisticRefreshStrategyProvider getInstance() {
        return new ProbablisticRefreshStrategyProvider() ;
    }
}
