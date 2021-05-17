package com.sentry.agent.core.plugin.api.stats;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.config.MetricsNames;
import com.sentry.agent.core.config.url.UrlIncludePattern;
import com.sentry.agent.core.util.PrometheusRegisterHolder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;

public class UrlStats extends SectionStats {

    private UrlIncludePattern pattern;

    public UrlStats(PrimaryKey key) {
        super(MetricsNames.URL, Tags.of("uri", key.get(0), "method", key.get(1)));
    }

    public long onFinally(long start, int code) {
        if(PrometheusRegisterHolder.getRegistry() != null) {
            Counter.builder(MetricsNames.URL + "_code")
                    .tags(super.getTags())
                    .tag("code", String.valueOf(code)).register(PrometheusRegisterHolder.getRegistry()).increment();
        }
        return super.onFinally(start);
    }

    public void ifNeedIncrementErrorCount(int code) {
        if (null != this.pattern && !this.pattern.isSuccCode(code)) {
            super.errorCount.incrementAndGet();
        }
    }

    public void setPattern(UrlIncludePattern pattern) {
        this.pattern = pattern;
    }
}
