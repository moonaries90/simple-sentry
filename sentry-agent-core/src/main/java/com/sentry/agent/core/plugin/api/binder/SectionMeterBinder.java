package com.sentry.agent.core.plugin.api.binder;

import com.sentry.agent.core.plugin.api.stats.BaseStats;
import com.sentry.agent.core.plugin.api.stats.SectionStats;
import io.micrometer.core.instrument.FunctionCounter;

public abstract class SectionMeterBinder extends BaseMeterBinder {

    @Override
    public void addStats(BaseStats stats) {
        if(registry != null) {
            super.addStats(stats);
            if(stats instanceof SectionStats) {
                SectionStats sectionStats = (SectionStats) stats;
                FunctionCounter.builder(stats.getName(), sectionStats, (stat) -> stat.getMs0_10().get()).tags(stats.getTags()).tag("info", "ms0_10").register(registry);
                FunctionCounter.builder(stats.getName(), sectionStats, (stat) -> stat.getMs10_100().get()).tags(stats.getTags()).tag("info", "ms10_100").register(registry);
                FunctionCounter.builder(stats.getName(), sectionStats, (stat) -> stat.getMs100_1000().get()).tags(stats.getTags()).tag("info", "ms100_1000").register(registry);
                FunctionCounter.builder(stats.getName(), sectionStats, (stat) -> stat.getS1_10().get()).tags(stats.getTags()).tag("info", "s1_10").register(registry);
                FunctionCounter.builder(stats.getName(), sectionStats, (stat) -> stat.getS10_n().get()).tags(stats.getTags()).tag("info", "s10_n").register(registry);
            }
        }
    }
}
