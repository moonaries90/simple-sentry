package com.sentry.agent.core.plugin.api.binder;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class CommonInfoAggregator implements MeterBinder {

    public static CommonInfoAggregator instance = new CommonInfoAggregator();

    private static final ConcurrentMap<Info, AtomicLong> infoMap = new ConcurrentHashMap<>();

    private static MeterRegistry registry;

    public void addInfo(String name, String infoStr) {
        Info info = new Info(name + "_info", infoStr);
        AtomicLong count = infoMap.computeIfAbsent(info, (v) -> new AtomicLong(0));
        if(count.incrementAndGet() <= 1 && registry != null) {
            FunctionCounter.builder(info.metricsName, null, (o) -> count.get()).tag("info", info.info).register(registry);
        }
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        CommonInfoAggregator.registry = registry;
        if(infoMap.size() > 0) {
            infoMap.forEach((info, v) -> FunctionCounter.builder(info.metricsName, v, AtomicLong::get).tag("info", info.info).register(registry));
        }
    }

    static class Info {

        private final String metricsName;

        private final String info;

        Info(String metricsName, String info) {
            this.metricsName = metricsName;
             this.info = info;
        }
    }
}
