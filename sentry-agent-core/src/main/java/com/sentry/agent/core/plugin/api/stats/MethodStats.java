package com.sentry.agent.core.plugin.api.stats;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.config.MetricsNames;
import io.micrometer.core.instrument.Tags;

public class MethodStats extends SectionStats {

    public MethodStats(PrimaryKey key) {
        super(MetricsNames.METHOD, Tags.of("class", key.get(0), "method", key.get(1)));
    }
}
