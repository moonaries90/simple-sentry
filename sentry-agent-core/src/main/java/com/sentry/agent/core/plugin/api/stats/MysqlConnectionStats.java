package com.sentry.agent.core.plugin.api.stats;

import com.sentry.agent.core.plugin.api.PrimaryKey;

import java.util.concurrent.atomic.AtomicLong;

public class MysqlConnectionStats {

    private final AtomicLong createCount = new AtomicLong(), destroyCount = new AtomicLong(), currentCount = new AtomicLong();

    private final String connection;

    public MysqlConnectionStats(PrimaryKey key) {
        this.connection = key.get(0);
    }

    public String getConnection() {
        return connection;
    }

    public void onStart() {
        createCount.incrementAndGet();
        currentCount.incrementAndGet();
    }

    public void onDestroy() {
        destroyCount.incrementAndGet();
        currentCount.decrementAndGet();
    }

    public AtomicLong getCreateCount() {
        return createCount;
    }

    public AtomicLong getDestroyCount() {
        return destroyCount;
    }

    public AtomicLong getCurrentCount() {
        return currentCount;
    }
}
