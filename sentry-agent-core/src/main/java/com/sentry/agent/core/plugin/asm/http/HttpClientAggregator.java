package com.sentry.agent.core.plugin.asm.http;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.plugin.api.binder.BaseMeterBinder;
import com.sentry.agent.core.plugin.api.binder.HttpClientMeterBinder;
import com.sentry.agent.core.plugin.api.stats.HttpStats;
import com.sentry.agent.core.plugin.asm.AsmAggregator;

public class HttpClientAggregator extends AsmAggregator<HttpStats> {

    private static HttpClientMeterBinder httpClientMeterBinder;

    public static void setMeterBinder(HttpClientMeterBinder meterBinder) {
        httpClientMeterBinder = meterBinder;
    }

    public void onStatusCode(int code, PrimaryKey key) {
        if(key != null) {
            HttpStats stats = super.getValue(key, false);
            if(stats != null) {
                stats.onStatusCode(code);
            }
        }
    }

    @Override
    protected Class<HttpStats> getValueType() {
        return HttpStats.class;
    }

    @Override
    protected BaseMeterBinder getMeterBinder() {
        return httpClientMeterBinder;
    }
}
