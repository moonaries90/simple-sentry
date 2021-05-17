package com.sentry.agent.core.plugin.api;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.util.ServiceLoader;

public class MeterSpiLoader {

    public static void bindTo(MeterRegistry registry) {
        ServiceLoader<MeterBinder> binders = ServiceLoader.load(MeterBinder.class, Thread.currentThread().getContextClassLoader());
        for(MeterBinder binder : binders) {
            binder.bindTo(registry);
        }
    }
}
