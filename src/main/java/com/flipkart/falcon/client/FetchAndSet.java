package com.flipkart.falcon.client;


import com.netflix.hystrix.*;

import java.util.function.Supplier;

public class FetchAndSet extends HystrixCommand<Object> {

    /* command name */
    private static final String COMMAND = "CacheClient";
    /* pool to identify the thread pool */
    private static final String POOL = "Pool";
    /* supplier during run */
    private Supplier<Object> supplier;

    protected FetchAndSet(Supplier<Object> supplier) {
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(COMMAND ))
                .andCommandKey(HystrixCommandKey.Factory.asKey(COMMAND ))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withCircuitBreakerEnabled(true)
                                .withFallbackEnabled(false)
                                .withExecutionTimeoutInMilliseconds(140000)
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))
                .andThreadPoolKey(
                        HystrixThreadPoolKey.Factory.asKey(COMMAND ))
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withCoreSize(100)));
        this.supplier = supplier;
    }

    @Override
    protected Object run() throws Exception {
        return supplier.get();
    }
}
