package com.sentry.agent.core.plugin.api.binder;

import com.sentry.agent.core.plugin.api.stats.BaseStats;
import com.sentry.agent.core.plugin.api.stats.RedisStats;
import com.sentry.agent.core.plugin.asm.redis.RedisAggregator;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;

public class RedisMeterBinder extends SectionMeterBinder {

    @Override
    public void bindTo(MeterRegistry registry) {
        super.bindTo(registry);
        RedisAggregator.setMeterBinder(this);
    }

    @Override
    public void addStats(BaseStats stats) {
        super.addStats(stats);
        if(registry != null) {
            if(stats instanceof RedisStats) {
                RedisStats redisStats = (RedisStats) stats;
                FunctionCounter.builder(stats.getName(), redisStats, (stat) -> stat.getHint().get()).tags(stats.getTags()).tag("info", "hint").register(registry);
            }
        }
    }
}
