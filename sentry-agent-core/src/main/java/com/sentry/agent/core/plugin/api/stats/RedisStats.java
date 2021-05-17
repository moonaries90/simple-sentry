package com.sentry.agent.core.plugin.api.stats;

import com.sentry.agent.core.config.MetricsNames;
import com.sentry.agent.core.plugin.api.PrimaryKey;
import io.micrometer.core.instrument.Tags;

import java.util.concurrent.atomic.AtomicLong;

public class RedisStats extends SectionStats {

    private final AtomicLong hint = new AtomicLong(0);

    public RedisStats(PrimaryKey key) {
        super(MetricsNames.REDIS, Tags.of("host", key.get(0), "port", key.get(1), "method", key.get(2)));
    }

    public AtomicLong getHint() {
        return this.hint;
    }

    public void increaseHint() {
        this.hint.incrementAndGet();
    }
}
