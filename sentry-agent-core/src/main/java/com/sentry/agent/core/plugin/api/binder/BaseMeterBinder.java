package com.sentry.agent.core.plugin.api.binder;

import com.sentry.agent.core.plugin.api.stats.BaseStats;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;

public abstract class BaseMeterBinder extends SentryMeterBinder {

    public void addStats(BaseStats stats) {
        if(registry != null) {
            FunctionCounter.builder(stats.getName(), stats, (stat) -> stat.getInvokeCount().get()).tags(stats.getTags()).tag("info", "invokeCount").register(registry);
            FunctionCounter.builder(stats.getName(), stats, (stat) -> stat.getErrorCount().get()).tags(stats.getTags()).tag("info", "errorCount").register(registry);
            FunctionCounter.builder(stats.getName(), stats, (stat) -> stat.getMillisTotal().get()).tags(stats.getTags()).tag("info", "millisTotal").register(registry);

            Gauge.builder(stats.getName(), stats, (stat) -> stat.getRunningCount().get()).tags(stats.getTags()).tag("info", "runningCount").register(registry);
            Gauge.builder(stats.getName(), stats, (stat) -> stat.getMaxTime().get()).tags(stats.getTags()).tag("info", "maxTime").register(registry);
            Gauge.builder(stats.getName(), stats, (stat) -> stat.getConcurrentMax().get()).tags(stats.getTags()).tag("info", "concurrentMax").register(registry);
        }
    }
}
