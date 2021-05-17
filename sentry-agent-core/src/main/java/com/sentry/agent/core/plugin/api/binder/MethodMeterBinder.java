package com.sentry.agent.core.plugin.api.binder;

import com.sentry.agent.core.plugin.asm.method.MethodAggregator;
import io.micrometer.core.instrument.MeterRegistry;

public class MethodMeterBinder extends SectionMeterBinder {

    @Override
    public void bindTo(MeterRegistry registry) {
        super.bindTo(registry);
        MethodAggregator.setMeterBinder(this);
    }
}
