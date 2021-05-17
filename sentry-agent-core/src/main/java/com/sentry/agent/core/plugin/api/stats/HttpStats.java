package com.sentry.agent.core.plugin.api.stats;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.config.MetricsNames;
import com.sentry.agent.core.util.PrometheusRegisterHolder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;

public class HttpStats extends SectionStats {

    public HttpStats(PrimaryKey key) {
        super(MetricsNames.HTTP, Tags.of("host", key.get(0), "uri", key.get(1), "method", key.get(2)));
    }

    public void onStatusCode(int code) {
        if(PrometheusRegisterHolder.getRegistry() != null) {
            Counter.builder(MetricsNames.HTTP + "_code")
                    .tags(super.getTags())
                    .tag("code", String.valueOf(code)).register(PrometheusRegisterHolder.getRegistry()).increment();
        }
    }
}
