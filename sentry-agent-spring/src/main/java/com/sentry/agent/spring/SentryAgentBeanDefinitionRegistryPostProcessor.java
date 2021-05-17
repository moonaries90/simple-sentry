package com.sentry.agent.spring;

import com.sentry.agent.core.util.PrometheusRegisterHolder;
import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

public class SentryAgentBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private final AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        // 添加 PrometheusMeterRegistry 的三个依赖
        PrometheusConfig prometheusConfig = key -> null;
        configurableListableBeanFactory.registerSingleton("prometheusConfig", prometheusConfig);

        CollectorRegistry registry = new CollectorRegistry();
        configurableListableBeanFactory.registerSingleton("collectorRegistry", registry);

        Clock clock = Clock.SYSTEM;
        PrometheusMeterRegistry meterRegistry = new PrometheusMeterRegistry(prometheusConfig, registry, clock);
        configurableListableBeanFactory.registerSingleton("prometheusMeterRegistry", meterRegistry);

        // 将初始化好的 prometheusMeterRegistry 设置到依赖项中
        PrometheusRegisterHolder.setRegistry(meterRegistry);
        SentryAgentWebApplicationInitializer.getSentryFilter().setRegistry(meterRegistry);
    }

    private void doRegister(Class<?> clazz, BeanDefinitionRegistry registry) {
        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(clazz).getBeanDefinition();
        BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanNameGenerator.generateBeanName(beanDefinition, registry));
        BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, registry);
    }
}
