package com.sentry.agent.core.plugin.asm.method;

import com.sentry.agent.core.plugin.api.binder.BaseMeterBinder;
import com.sentry.agent.core.plugin.api.binder.MethodMeterBinder;
import com.sentry.agent.core.plugin.api.stats.MethodStats;
import com.sentry.agent.core.plugin.asm.AsmAggregator;

public class MethodAggregator extends AsmAggregator<MethodStats> {

    private static MethodMeterBinder methodMeterBinder;

    public static void setMeterBinder(MethodMeterBinder meterBinder) {
        methodMeterBinder = meterBinder;
    }

    @Override
    protected Class<MethodStats> getValueType() {
        return MethodStats.class;
    }

    @Override
    protected BaseMeterBinder getMeterBinder() {
        return methodMeterBinder;
    }
}
