package com.flipkart.falcon.commons;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;

import java.util.concurrent.TimeUnit;

/**
 * Copied by pradeep.joshi on 18/08/17.
 */
public class MetricUtils {
    private static final String BASE = "timed";
    private static final String HYPHEN = "-";

    public static void stopTimers(TimerContext... timerContexts) {
        if (timerContexts != null && timerContexts.length > 0) {
            for (TimerContext timerContext : timerContexts) {
                if (timerContext != null) {
                    timerContext.stop();
                }
            }
        }
    }

    public static Timer getTimer(Class clazz, String... strings) {
        String concat = BASE;
        for (String s : strings) {
            concat = concat + HYPHEN + s;
        }
        return Metrics.newTimer(clazz, concat);
    }

    public static Meter getMeter(Class clazz, String... strings) {
        String concat = null;
        for (String s : strings) {
            concat = concat == null ? s : concat + HYPHEN + s;
        }
        return getMeter(clazz, concat);
    }


    public static Timer getTimer(Class clazz, String name) {
        return Metrics.newTimer(clazz, name);
    }

    public static Meter getMeter(Class clazz, String name, String eventType, TimeUnit unit) {
        return Metrics.newMeter(clazz, name, eventType, unit);
    }

    public static Meter getMeter(Class clazz, String name) {
        return Metrics.newMeter(clazz, name, name, TimeUnit.SECONDS);
    }

    public static Histogram getHistogram(Class clazz, String name) {
        return Metrics.newHistogram(clazz, name);
    }

}
