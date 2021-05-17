package com.sentry.agent.core.plugin.api.binder;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

public abstract class SentryMeterBinder implements MeterBinder {

    protected MeterRegistry registry;

    @Override
    public void bindTo(MeterRegistry registry) {
        this.registry = registry;
    }
}
