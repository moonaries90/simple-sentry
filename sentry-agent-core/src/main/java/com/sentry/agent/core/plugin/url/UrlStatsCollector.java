package com.sentry.agent.core.plugin.url;

import com.sentry.agent.core.plugin.api.stats.UrlStats;
import com.sentry.agent.core.config.ConfigManager;
import com.sentry.agent.core.config.url.UrlIncludePattern;
import com.sentry.agent.core.config.url.UrlPattern;
import com.sentry.agent.core.meta.MatchResult;
import com.sentry.agent.core.util.IntegerMap;

public class UrlStatsCollector {

    private static final UrlPattern pattern = ConfigManager.getUrlPattern();

    public static MatchResult onMatch(String uri) {
        return pattern == null ? null : pattern.matchedUrl(uri);
    }

    private static final UrlStatsAggregator urlStatsAggregator = new UrlStatsAggregator();

    private static final IntegerMap totalUrlMap = new IntegerMap(1000);

    public static UrlStats onStart(String uri, String method, UrlIncludePattern pattern) {
        if(uri == null) {
            return null;
        }
        int value = totalUrlMap.registerResource(uri);
        if(value < 0) {
            return null;
        }
        return urlStatsAggregator.onStart(uri, method, pattern);
    }
}
