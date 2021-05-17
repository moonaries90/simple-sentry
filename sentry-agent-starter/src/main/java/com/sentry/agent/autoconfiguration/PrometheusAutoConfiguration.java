package com.sentry.agent.autoconfiguration;

import com.sentry.agent.core.util.PrometheusRegisterHolder;
import com.sentry.agent.spring.SentryAgentWebApplicationInitializer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.export.prometheus.PrometheusMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.Filter;
import java.util.ArrayList;

@AutoConfigureAfter(PrometheusMetricsExportAutoConfiguration.class)
@Configuration
public class PrometheusAutoConfiguration implements InitializingBean {

    @Autowired
    private PrometheusMeterRegistry registry;

    @Override
    public void afterPropertiesSet() {
        SentryAgentWebApplicationInitializer.getSentryFilter().setRegistry(registry);
        PrometheusRegisterHolder.setRegistry(registry);
    }

    @Bean
    public FilterRegistrationBean<Filter> prometheusFilter() {
        FilterRegistrationBean<Filter> r = new FilterRegistrationBean<>();
        r.setName("prometheusFilter");
        r.setFilter(SentryAgentWebApplicationInitializer.getSentryFilter());
        r.setUrlPatterns(new ArrayList<String>(){{add("/*");}});
        r.setOrder(Ordered.HIGHEST_PRECEDENCE);
        r.setMatchAfter(false);
        return r;
    }
}
