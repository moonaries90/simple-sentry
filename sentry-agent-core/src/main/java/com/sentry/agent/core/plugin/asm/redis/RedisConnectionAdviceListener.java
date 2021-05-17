package com.sentry.agent.core.plugin.asm.redis;

import com.sentry.agent.core.config.MetricsNames;
import com.sentry.agent.core.plugin.asm.api.AdviceListener;
import com.sentry.agent.core.util.PrometheusRegisterHolder;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RedisConnectionAdviceListener implements AdviceListener {

    private static final Map<Integer, ConnectionInfo> connectionCounter = new ConcurrentHashMap<>();

    @Override
    public void afterReturning(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) throws Throwable {
        Field hostField = target.getClass().getField("host");
        Field portField = target.getClass().getField("port");
        hostField.setAccessible(true);
        portField.setAccessible(true);

        String host = (String) hostField.get(target);
        int port = (int) portField.get(target);
        int cnId = RedisAdviceListener.hostPortMap.registerResource(host + ":" + port);
        if(cnId >= 0) {
            ConnectionInfo info = connectionCounter.getOrDefault(cnId, new ConnectionInfo());
            if("connect".equals(methodName)) {
                info.currentCount.incrementAndGet();
                info.connectCount.incrementAndGet();
            } else if("disconnect".equals(methodName)) {
                info.disconnectCount.incrementAndGet();
                if(info.currentCount.decrementAndGet() < 0) {
                    info.currentCount.incrementAndGet();
                }
            }
            connectionCounter.putIfAbsent(cnId, info);
            if(PrometheusRegisterHolder.getRegistry() != null) {
                Gauge.builder(MetricsNames.REDIS + "_connection", info.currentCount::get).tag("host", host).tag("port", String.valueOf(port)).tag("info", "current").strongReference(true).register(PrometheusRegisterHolder.getRegistry());
                FunctionCounter.builder(MetricsNames.REDIS + "_connection", (info), (i) -> i.connectCount.get()).tag("host", host).tag("port", String.valueOf(port)).tag("info", "connect").register(PrometheusRegisterHolder.getRegistry());
                FunctionCounter.builder(MetricsNames.REDIS + "_connection", (info), (i) -> i.disconnectCount.get()).tag("host", host).tag("port", String.valueOf(port)).tag("info", "disconnect").register(PrometheusRegisterHolder.getRegistry());
            }
        }
    }

    static class ConnectionInfo {
        private final AtomicLong connectCount = new AtomicLong(0);

        private final AtomicLong disconnectCount = new AtomicLong(0);

        private final AtomicLong currentCount = new AtomicLong(0);
    }
}
