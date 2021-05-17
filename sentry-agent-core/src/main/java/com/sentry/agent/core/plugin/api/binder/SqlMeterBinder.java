package com.sentry.agent.core.plugin.api.binder;

import com.sentry.agent.core.plugin.api.stats.BaseStats;
import com.sentry.agent.core.plugin.asm.sql.mysql.MysqlAggregator;
import com.sentry.agent.core.plugin.asm.sql.oracle.OracleAggregator;
import com.sentry.agent.core.plugin.api.stats.MysqlStats;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;

public class SqlMeterBinder extends SectionMeterBinder {

    @Override
    public void bindTo(MeterRegistry registry) {
        super.bindTo(registry);
        OracleAggregator.setMeterBinder(this);
        MysqlAggregator.setMeterBinder(this);
    }

    @Override
    public void addStats(BaseStats stats) {
        super.addStats(stats);
        if(stats instanceof MysqlStats) {
            MysqlStats mysqlStats = (MysqlStats) stats;
            FunctionCounter.builder(stats.getName(), mysqlStats, (stat) -> stat.getReadRowCount().get()).tags(stats.getTags()).tag("info", "readRowCount").register(registry);
            FunctionCounter.builder(stats.getName(), mysqlStats, (stat) -> stat.getUpdateRouCount().get()).tags(stats.getTags()).tag("info", "updateRowCount").register(registry);
        }
    }
}
