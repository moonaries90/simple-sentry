package com.sentry.agent.core.plugin.api.stats;

import io.micrometer.core.instrument.Tag;

import java.util.concurrent.atomic.AtomicLong;

public class SectionStats extends BaseStats {

    protected final AtomicLong ms0_10 = new AtomicLong(),
            ms10_100 = new AtomicLong(),
            ms100_1000 = new AtomicLong(),
            s1_10 = new AtomicLong(),
            s10_n = new AtomicLong();

    public SectionStats(String name, Iterable<Tag> tags) {
        super(name, tags);
    }

    public long onFinally(long start) {
        long used = super.onFinally(start);
        if (used < 10L) {
            ms0_10.incrementAndGet();
        } else if (used < 100L) {
            ms10_100.incrementAndGet();
        } else if (used < 1000L) {
            ms100_1000.incrementAndGet();
        } else if (used < 10000L) {
            s1_10.incrementAndGet();
        } else {
            s10_n.incrementAndGet();
        }
        return used;
    }

    public AtomicLong getMs0_10() {
        return ms0_10;
    }

    public AtomicLong getMs10_100() {
        return ms10_100;
    }

    public AtomicLong getMs100_1000() {
        return ms100_1000;
    }

    public AtomicLong getS1_10() {
        return s1_10;
    }

    public AtomicLong getS10_n() {
        return s10_n;
    }
}
