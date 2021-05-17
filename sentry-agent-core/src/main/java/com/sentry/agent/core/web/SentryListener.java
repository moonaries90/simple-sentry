package com.sentry.agent.core.web;

import com.sentry.agent.core.SentryInitializer;
import com.sentry.agent.core.plugin.api.transformer.TransformerInitializer;
import com.sentry.agent.core.config.ConfigManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SentryListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        String location = event.getServletContext().getInitParameter("sentry.agent.location");
        TransformerInitializer.setLocation(location == null || location.length() <= 0 ? "classpath:sentry-agent.xml" : location);
        String appName = System.getProperty("project.name");
        if(appName == null) {
            appName = event.getServletContext().getInitParameter("project.name");
        }
        ConfigManager.setAppName(appName);
        SentryInitializer.appendToClassLoader(SentryInitializer.class);
        SentryInitializer.init();
        TransformerInitializer.reTransformIfNeeded();
        event.getServletContext().addFilter("prometheus", new SentryFilter()).addMappingForUrlPatterns(null, false, "/*");
    }
}
