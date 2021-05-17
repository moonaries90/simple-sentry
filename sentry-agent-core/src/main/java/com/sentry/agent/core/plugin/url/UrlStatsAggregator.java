package com.sentry.agent.core.plugin.url;

import com.sentry.agent.core.plugin.api.binder.UrlMeterBinder;
import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.plugin.api.aggreagtor.MultiPrimaryKeyAggregator;
import com.sentry.agent.core.plugin.api.stats.UrlStats;
import com.sentry.agent.core.config.url.UrlIncludePattern;

public class UrlStatsAggregator extends MultiPrimaryKeyAggregator<UrlStats> {

    private static UrlMeterBinder meterBinder;

    public UrlStats onStart(String url, String method, UrlIncludePattern pattern) {
        PrimaryKey pk = new PrimaryKey(url, method);
        UrlStats stats = this.getValue(pk);
        if (null != stats) {
            stats.setPattern(pattern);
            if(meterBinder != null) {
                meterBinder.addStats(stats);
            }
        }
        return stats;
    }

    @Override
    protected Class<UrlStats> getValueType() {
        return UrlStats.class;
    }

    @Override
    protected int primaryKeyLength() {
        return 2;
    }

    public static void setMeterBinder(UrlMeterBinder meterBinder) {
        UrlStatsAggregator.meterBinder = meterBinder;
    }
}
