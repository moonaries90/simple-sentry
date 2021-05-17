package com.sentry.agent.core.prometheus;

import com.sentry.agent.core.util.PrometheusRegisterHolder;
import io.prometheus.client.exporter.common.TextFormat;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class PrometheusFilter implements Filter {

    private static final String PrometheusPath = "/actuator/prometheus";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUri = request.getRequestURI();
        if(requestUri.startsWith(PrometheusPath)) {
            String expose = this.expose();
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(expose);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private String expose() {
        try {
            Writer writer = new StringWriter();
            TextFormat.write004(writer, PrometheusRegisterHolder.getRegistry().getPrometheusRegistry().metricFamilySamples());
            return writer.toString();
        } catch (IOException var2) {
            throw new RuntimeException("Writing metrics failed", var2);
        }
    }

    @Override
    public void destroy() {

    }
}
