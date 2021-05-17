package com.sentry.agent.core.plugin.asm.sql.mysql;

import com.sentry.agent.core.plugin.api.binder.BaseMeterBinder;
import com.sentry.agent.core.plugin.api.binder.SqlMeterBinder;
import com.sentry.agent.core.plugin.api.stats.MysqlStats;
import com.sentry.agent.core.plugin.asm.AsmAggregator;

public class MysqlAggregator extends AsmAggregator<MysqlStats> {

    private static SqlMeterBinder meterBinder;

    public static void setMeterBinder(SqlMeterBinder sqlMeterBinder) {
        meterBinder = sqlMeterBinder;
    }

    @Override
    protected Class<MysqlStats> getValueType() {
        return MysqlStats.class;
    }

    @Override
    protected BaseMeterBinder getMeterBinder() {
        return meterBinder;
    }
}
