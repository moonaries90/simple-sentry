package com.sentry.agent.core.plugin.api.aggreagtor;

import com.sentry.agent.core.plugin.api.binder.HttpClientMeterBinder;
import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.plugin.api.stats.HttpStats;
import com.sentry.agent.core.util.IntegerMap;

public class HttpAggregator extends AbstractPrimaryKeyValueAggregator<PrimaryKey, HttpStats> {

    private final IntegerMap counter = new IntegerMap(1000);

    private static final ThreadLocal<HttpStats> statsLocal = new ThreadLocal<>();

    private static final ThreadLocal<Long> timeLocal = new ThreadLocal<>();

    private static HttpClientMeterBinder meterBinder;

    @Override
    protected Class<HttpStats> getValueType() {
        return HttpStats.class;
    }

    public long onStart(String host, String uri, String method) {
        int r = counter.registerResource(String.format("%s-%s-%s", host, uri, method));
        if(r == -1) {
            return 0L;
        }
        HttpStats stats = super.getValue(new PrimaryKey(host, uri, method));
        if(stats != null) {
            long startTime = stats.onStart();
            statsLocal.set(stats);
            timeLocal.set(startTime);
            if(meterBinder != null) {
                meterBinder.addStats(stats);
            }
            return startTime;
        }
        return 0L;
    }

    public void onStatusCode(int code) {
        HttpStats stats = statsLocal.get();
        if(stats != null) {
            stats.onStatusCode(code);
        }
    }

    public void onThrowable(Throwable t) {
        HttpStats stats = statsLocal.get();
        if(stats != null) {
            stats.onThrowable(t);
        }
    }

    public void onResponseClose() {
        HttpStats stats = statsLocal.get();
        Long startTime = timeLocal.get();
        if(stats != null && startTime != null) {
            stats.onFinally(startTime);
        }
        statsLocal.remove();
        timeLocal.remove();
    }

    public static void setMeterBinder(HttpClientMeterBinder meterBinder) {
        HttpAggregator.meterBinder = meterBinder;
    }
}
