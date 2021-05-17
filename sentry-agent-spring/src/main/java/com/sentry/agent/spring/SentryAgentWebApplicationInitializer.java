package com.sentry.agent.spring;

import com.sentry.agent.core.web.SentryFilter;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class SentryAgentWebApplicationInitializer implements WebApplicationInitializer {

    private static final SentryFilter sentryFilter = new SentryFilter();

    public static SentryFilter getSentryFilter() {
        return sentryFilter;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addFilter("prometheus", sentryFilter).addMappingForUrlPatterns(null, false, "/*");
    }
}
