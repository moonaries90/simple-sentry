package com.sentry.agent.core.util;

import com.sentry.agent.core.plugin.api.MeterSpiLoader;
import com.sentry.agent.core.config.ConfigManager;
import io.micrometer.core.instrument.Tag;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.util.Map;
import java.util.stream.Collectors;

public class PrometheusRegisterHolder {

    private static PrometheusMeterRegistry registry;

    public static void setRegistry(PrometheusMeterRegistry registry) {
        PrometheusRegisterHolder.registry = registry;
        Map<String, String> tags = ConfigManager.getAgentConfig().getTags();;
        if(tags != null && tags.size() > 0) {
            registry.config().commonTags(tags.entrySet().stream().map(i -> Tag.of(i.getKey(), i.getValue())).collect(Collectors.toList()));
        }

        MeterSpiLoader.bindTo(registry);
    }

    public static PrometheusMeterRegistry getRegistry() {
        return registry;
    }
}
