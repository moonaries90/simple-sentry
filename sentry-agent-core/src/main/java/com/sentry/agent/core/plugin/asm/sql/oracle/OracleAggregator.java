package com.sentry.agent.core.plugin.asm.sql.oracle;

import com.sentry.agent.core.plugin.api.binder.BaseMeterBinder;
import com.sentry.agent.core.plugin.api.binder.SqlMeterBinder;
import com.sentry.agent.core.plugin.api.stats.SqlStats;
import com.sentry.agent.core.plugin.asm.AsmAggregator;

public class OracleAggregator extends AsmAggregator<SqlStats> {

    private static SqlMeterBinder meterBinder;

    public static void setMeterBinder(SqlMeterBinder sqlMeterBinder) {
        meterBinder = sqlMeterBinder;
    }

    @Override
    protected Class<SqlStats> getValueType() {
        return SqlStats.class;
    }

    @Override
    protected BaseMeterBinder getMeterBinder() {
        return meterBinder;
    }
}
