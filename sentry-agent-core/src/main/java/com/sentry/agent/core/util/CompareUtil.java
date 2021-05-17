package com.sentry.agent.core.util;

import io.prometheus.client.Gauge;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CompareUtil {

    public static void setMinValue(AtomicInteger current, int value) {
        int min;
        do {
            min = current.get();
        } while(value < min && !current.compareAndSet(min, value));
    }

    public static void setMinValue(AtomicLong current, long value) {
        long min;
        do {
            min = current.get();
        } while(value < min && !current.compareAndSet(min, value));
    }

    public static boolean setMaxValue(AtomicLong current, long value) {
        while(true) {
            long max = current.get();
            if (value > max) {
                if (!current.compareAndSet(max, value)) {
                    continue;
                }
                return true;
            }
            return false;
        }
    }

    public static boolean setMaxValue(AtomicInteger current, int value) {
        while(true) {
            int max = current.get();
            if (value > max) {
                if (!current.compareAndSet(max, value)) {
                    continue;
                }
                return true;
            }
            return false;
        }
    }

    public static void setMaxValue(Gauge.Child child, double value) {
        double max = child.get();
        if(value > max) {
            child.set(value);
        }
    }
}

