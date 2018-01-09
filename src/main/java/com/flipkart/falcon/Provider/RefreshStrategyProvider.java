package com.flipkart.falcon.Provider;

import com.flipkart.falcon.client.MetaValue;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public interface RefreshStrategyProvider {
    boolean shouldRefresh(MetaValue metaValue) ;
}
