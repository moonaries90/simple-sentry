package com.sentry.agent.core.plugin.asm.redis;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.plugin.api.binder.BaseMeterBinder;
import com.sentry.agent.core.plugin.api.binder.RedisMeterBinder;
import com.sentry.agent.core.plugin.api.stats.RedisStats;
import com.sentry.agent.core.plugin.asm.AsmAggregator;

public class RedisAggregator extends AsmAggregator<RedisStats> {

    private static RedisMeterBinder meterBinder;

    public static void setMeterBinder(RedisMeterBinder meterBinder) {
        RedisAggregator.meterBinder = meterBinder;
    }

    @Override
    protected Class<RedisStats> getValueType() {
        return RedisStats.class;
    }

    @Override
    protected BaseMeterBinder getMeterBinder() {
        return meterBinder;
    }

    public void onFinallyHint(PrimaryKey key, boolean hint) {
        super.onFinally(key);
        if(hint && key != null) {
            RedisStats stats = super.getValue(key, false);
            if(stats != null) {
                stats.increaseHint();
            }
        }
    }
}
