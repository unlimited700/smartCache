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

        super(Setter.withGroupKey(
                HystrixCommandGroupKey.Factory.asKey(COMMAND + POOL))
                .andCommandKey(HystrixCommandKey.Factory.asKey(COMMAND + POOL))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withCircuitBreakerEnabled(false)
                                .withFallbackEnabled(true)
                                .withExecutionTimeoutInMilliseconds(2800)
                                .withFallbackIsolationSemaphoreMaxConcurrentRequests(Integer.MAX_VALUE)
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))
                .andThreadPoolKey(
                        HystrixThreadPoolKey.Factory.asKey(COMMAND  + POOL))
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withCoreSize(400)));
        this.supplier = supplier;
    }

    @Override
    protected Object run() throws Exception {
        return supplier.get();
    }
}
