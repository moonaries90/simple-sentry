package com.sentry.agent.core.web;

import com.sentry.agent.core.plugin.url.UrlStatsCollector;
import com.sentry.agent.core.plugin.api.stats.UrlStats;
import com.sentry.agent.core.config.ConfigManager;
import com.sentry.agent.core.meta.MatchResult;
import com.sentry.agent.core.util.PrometheusRegisterHolder;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SentryFilter implements Filter {

    private static final String PROMETHEUS_URL = "/actuator/prometheus";

    private static final PrometheusMeterRegistry defaultRegistry = new PrometheusMeterRegistry(key -> null);

    private PrometheusMeterRegistry registry = defaultRegistry;

    public void setRegistry(PrometheusMeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        PrometheusRegisterHolder.setRegistry(registry);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUri = request.getRequestURI();
        if (requestUri.startsWith(PROMETHEUS_URL)) {
            try {
                response.setHeader("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
                TextFormat.write004(response.getWriter(), this.registry.getPrometheusRegistry().metricFamilySamples());
            } catch (IOException ex) {
                throw new RuntimeException("Writing metrics failed", ex);
            }
        } else {
            UrlStats stats = null;
            boolean thrown = false;
            long nanoTime = System.nanoTime();
            if(ConfigManager.getAgentConfig().getEnabled().isUrl()) {
                try {
                    MatchResult matchResult = UrlStatsCollector.onMatch(requestUri);
                    if (matchResult != null) {
                        String target;
                        if (matchResult.hasAction()) {
                            target = matchResult.transform(request.getParameterMap());
                        } else {
                            target = matchResult.targetUrl;
                        }
                        stats = UrlStatsCollector.onStart(target, request.getMethod(), matchResult.getUrlIncludePattern());
                        stats.onStart();
                    }
                    filterChain.doFilter(servletRequest, servletResponse);
                } catch (Throwable t) {
                    if(stats != null) {
                        stats.onThrowable(t);
                        thrown = true;
                    }
                } finally {
                    if(stats != null) {
                        if(!thrown) {
                            stats.ifNeedIncrementErrorCount(response.getStatus());
                        }
                        stats.onFinally(nanoTime, response.getStatus());
                    }
                }
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
