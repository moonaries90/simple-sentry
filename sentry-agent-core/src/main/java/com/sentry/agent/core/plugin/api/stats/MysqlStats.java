package com.sentry.agent.core.plugin.api.stats;

import com.sentry.agent.core.plugin.api.PrimaryKey;

import java.util.concurrent.atomic.AtomicLong;

public class MysqlStats extends SqlStats {

    private final AtomicLong readRowCount = new AtomicLong(), updateRouCount = new AtomicLong();

    public MysqlStats(PrimaryKey key) {
        super(key);
    }

    public void onFinally(long startTime, int readRowCount, int updateRowCount) {
        super.onFinally(startTime);
        if(readRowCount > 0) {
            this.readRowCount.accumulateAndGet(readRowCount, Long::sum);
        }
        if(updateRowCount > 0) {
            this.updateRouCount.accumulateAndGet(updateRowCount, Long::sum);
        }
    }

    public AtomicLong getReadRowCount() {
        return readRowCount;
    }

    public AtomicLong getUpdateRouCount() {
        return updateRouCount;
    }
}
