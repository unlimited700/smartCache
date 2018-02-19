package com.flipkart.falcon.Provider;

import com.flipkart.falcon.models.MetaValue;
import com.flipkart.falcon.models.RefreshStatus;

/**
 * Created by pradeep.joshi on 09/10/17.
 */
public interface RefreshStrategyProvider {
    RefreshStatus shouldRefresh(MetaValue metaValue) ;
}
