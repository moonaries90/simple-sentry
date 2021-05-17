package com.sentry.agent.core.plugin.api.binder;

import com.sentry.agent.core.plugin.api.stats.MysqlConnectionStats;
import com.sentry.agent.core.config.MetricsNames;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

public class SqlConnectionMeterBinder extends SentryMeterBinder {

    @Override
    public void bindTo(MeterRegistry registry) {
        super.bindTo(registry);
    }

    public void addStats(MysqlConnectionStats stats) {
        if(registry != null) {
            FunctionCounter.builder(MetricsNames.SQL + "_connection", stats, (stat) -> stat.getCreateCount().get()).tag("connection", stats.getConnection()).tag("info", "createCount").register(registry);
            FunctionCounter.builder(MetricsNames.SQL + "_connection", stats, (stat) -> stat.getDestroyCount().get()).tag("connection", stats.getConnection()).tag("info", "destroyCount").register(registry);
            Gauge.builder(MetricsNames.SQL + "_connection", stats, (stat) -> stat.getCurrentCount().get()).tag("connection", stats.getConnection()).tag("info", "currentCount").register(registry);
        }
    }
}
