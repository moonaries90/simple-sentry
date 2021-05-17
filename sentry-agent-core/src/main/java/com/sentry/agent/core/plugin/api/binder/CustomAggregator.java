package com.sentry.agent.core.plugin.api.binder;

import com.sentry.agent.core.meta.AtomicDouble;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CustomAggregator implements MeterBinder {

    private static final ConcurrentMap<String, AtomicDouble> customMap = new ConcurrentHashMap<>();

    private static final String CUSTOM_SENTRY = "sentry_custom";

    private static MeterRegistry registry;

    @Override
    public void bindTo(MeterRegistry registry) {
        CustomAggregator.registry = registry;
    }

    public static double addAndGet(String info, double value) {
        AtomicDouble d = new AtomicDouble();
        AtomicDouble _d = customMap.computeIfAbsent(info, (o) -> d);
        if(d.equals(_d)) {
            FunctionCounter.builder(CUSTOM_SENTRY, d, AtomicDouble::get).tag("info", info).register(registry);
        }
        return _d.addAndGet(value);
    }

    public static double getAndSet(String info, double value) {
        AtomicDouble d = new AtomicDouble();
        AtomicDouble _d = customMap.computeIfAbsent(info, (o) -> d);
        if(d.equals(_d)) {
            Gauge.builder(CUSTOM_SENTRY, d::get).tag("info", info).register(registry);
        }
        return _d.getAndSet(value);
    }
}
