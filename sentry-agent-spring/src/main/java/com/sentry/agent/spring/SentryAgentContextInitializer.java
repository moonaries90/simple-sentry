package com.sentry.agent.spring;

import com.sentry.agent.core.SentryInitializer;
import com.sentry.agent.core.config.ConfigManager;
import com.sentry.agent.core.config.resolver.ConfigParser;
import com.sentry.agent.core.plugin.api.transformer.TransformerInitializer;
import com.sentry.agent.spring.resolver.YamlConfigParser;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class SentryAgentContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final ConfigParser yamlConfigParser = new YamlConfigParser();

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        String appName = configurableApplicationContext.getEnvironment().getProperty("spring.application.name");
        if(appName == null || appName.length() <= 0) {
            appName = configurableApplicationContext.getEnvironment().getProperty("project.name");
        }

        ConfigManager.setAppName(appName);
        String location = configurableApplicationContext.getEnvironment().getProperty("sentry.agent.location");
        if(location == null || location.length() <= 0) {
            location = "classpath:sentry-agent.yml";
        }
        TransformerInitializer.setLocation(location);
        TransformerInitializer.setConfigParser(yamlConfigParser);
        SentryInitializer.init();

        if(notSpringBoot()) {
            configurableApplicationContext.addBeanFactoryPostProcessor(new SentryAgentBeanDefinitionRegistryPostProcessor());
        }
    }

    private static boolean notSpringBoot() {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("org.springframework.boot.autoconfigure.SpringBootApplication");
            return false;
        } catch (Exception ignore) {
            return true;
        }
    }
}
