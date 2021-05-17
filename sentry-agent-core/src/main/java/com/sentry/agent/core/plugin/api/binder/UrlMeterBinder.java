package com.sentry.agent.core.plugin.api.binder;

import com.sentry.agent.core.plugin.url.UrlStatsAggregator;
import io.micrometer.core.instrument.MeterRegistry;

public class UrlMeterBinder extends SectionMeterBinder {

    @Override
    public void bindTo(MeterRegistry registry) {
        super.bindTo(registry);
        UrlStatsAggregator.setMeterBinder(this);
    }
}
