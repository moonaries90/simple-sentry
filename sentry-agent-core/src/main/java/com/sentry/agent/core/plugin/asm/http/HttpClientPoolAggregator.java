package com.sentry.agent.core.plugin.asm.http;

import com.sentry.agent.core.config.MetricsNames;
import com.sentry.agent.core.util.PrometheusRegisterHolder;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpClientPoolAggregator {

    private static final AtomicInteger seq = new AtomicInteger(0);

    private static final Map<Object, Integer> poolMap = new ConcurrentHashMap<>();

    static {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PrometheusMeterRegistry registry = PrometheusRegisterHolder.getRegistry();
                if(poolMap.size() > 0 && registry != null) {
                    poolMap.forEach((object, poolId) -> {
                        try {
                            Method getTotalStatsMethod = object.getClass().getMethod("getTotalStats");
                            Object poolStats = getTotalStatsMethod.invoke(object);
                            if (poolStats != null) {
                                int leased = (int) poolStats.getClass().getMethod("getLeased").invoke(poolStats);
                                int available = (int) poolStats.getClass().getMethod("getAvailable").invoke(poolStats);
                                int max = (int) poolStats.getClass().getMethod("getMax").invoke(poolStats);
                                int pending = (int) poolStats.getClass().getMethod("getPending").invoke(poolStats);

                                Gauge.builder(MetricsNames.HTTP + "_stat", () -> available).tag("poolId", String.valueOf(poolId)).tag("info", "available").register(registry);
                                Gauge.builder(MetricsNames.HTTP + "_stat", () -> max).tag("poolId", String.valueOf(poolId)).tag("info", "max").register(registry);
                                Gauge.builder(MetricsNames.HTTP + "_stat", () -> leased).tag("poolId", String.valueOf(poolId)).tag("info", "leased").register(registry);
                                Gauge.builder(MetricsNames.HTTP + "_stat", () -> pending).tag("poolId", String.valueOf(poolId)).tag("info", "pending").register(registry);
                            }
                        } catch (Throwable ignore) {}
                        try {
                            Method getRoutesMethod = object.getClass().getMethod("getRoutes");
                            Collection<Object> routes = (Collection<Object>) getRoutesMethod.invoke(object);
                            if (routes != null && routes.size() > 0) {
                                Object o = routes.iterator().next();
                                Method getStatMethod = object.getClass().getMethod("getStats", o.getClass());
                                for (Object route : routes) {
                                    Object routeStat = getStatMethod.invoke(object, route);
                                    if(routeStat != null) {
                                        int leased = (int) routeStat.getClass().getMethod("getLeased").invoke(routeStat);
                                        int available = (int) routeStat.getClass().getMethod("getAvailable").invoke(routeStat);
                                        int max = (int) routeStat.getClass().getMethod("getMax").invoke(routeStat);
                                        int pending = (int) routeStat.getClass().getMethod("getPending").invoke(routeStat);

                                        Gauge.builder(MetricsNames.HTTP + "_routeStat", () -> available).tag("route", route.toString()).tag("poolId", String.valueOf(poolId)).tag("info", "available").register(registry);
                                        Gauge.builder(MetricsNames.HTTP + "_routeStat", () -> max).tag("route", route.toString()).tag("poolId", String.valueOf(poolId)).tag("info", "max").register(registry);
                                        Gauge.builder(MetricsNames.HTTP + "_routeStat", () -> leased).tag("route", route.toString()).tag("poolId", String.valueOf(poolId)).tag("info", "leased").register(registry);
                                        Gauge.builder(MetricsNames.HTTP + "_routeStat", () -> pending).tag("route", route.toString()).tag("poolId", String.valueOf(poolId)).tag("info", "pending").register(registry);

                                    }
                                }
                            }
                        } catch (Throwable ignore) {}
                    });
                }
            }
        }, 1000, 30000);
    }

    public static void register(Object o) {
        if(poolMap.size() >= 10 || poolMap.containsKey(o)) {
            return;
        }
        if(poolMap.putIfAbsent(o, seq.incrementAndGet()) != null) {
            seq.decrementAndGet();
        }
    }
}
