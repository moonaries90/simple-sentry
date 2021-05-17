package com.sentry.agent.core.plugin.api.binder;

import com.sentry.agent.core.plugin.api.aggreagtor.HttpAggregator;
import com.sentry.agent.core.plugin.asm.http.HttpClientAggregator;
import io.micrometer.core.instrument.MeterRegistry;

public class HttpClientMeterBinder extends SectionMeterBinder {

    @Override
    public void bindTo(MeterRegistry registry) {
        super.bindTo(registry);
        HttpAggregator.setMeterBinder(this);
        HttpClientAggregator.setMeterBinder(this);
    }
}
