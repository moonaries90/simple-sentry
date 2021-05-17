package com.sentry.agent.core.config;

import com.sentry.agent.core.config.props.MethodPattern;
import com.sentry.agent.core.config.props.SentryAgentConfig;
import com.sentry.agent.core.config.url.UrlPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {

    private static String appName;

    private static SentryAgentConfig agentConfig;

    private static JavaMethodPattern javaMethodPattern;

    private static UrlPattern urlPattern;

    public static void setAppName(String appName) {
        ConfigManager.appName = appName;
    }

    public static String getAppName() {
        return appName != null ? appName : "unknown";
    }

    public static JavaMethodPattern getJavaMethodPattern() {
        return javaMethodPattern;
    }

    public static UrlPattern getUrlPattern() {
        return urlPattern;
    }

    public static void init(SentryAgentConfig config) {
        agentConfig = config;
        parseJavaMethodConfig();
        parseUrlConfig();
    }

    public static boolean isValidMethodModifier(String scope) {
        if (scope == null || scope.equals("")) {
            return true;
        } else {
            scope = scope.trim();
            String[] arr = scope.split(",");
            return Arrays.stream(arr).allMatch(i -> "public".equals(i) || "private".equals(i) || "protected".equals(i));
        }
    }

    private static void parseJavaMethodConfig() {
        if(agentConfig.getJavaMethods() != null) {
            List<JavaMethodPatternItem> list = new ArrayList<>();
            agentConfig.getJavaMethods().forEach(clazz -> {
                if(clazz.getPattern() != null && !clazz.getPattern().equals("")) {
                    JavaMethodPatternItem item = new JavaMethodPatternItem();
                    item.setClassNamePattern(clazz.getPattern());
                    String clazzMethodModifier = clazz.getMethodModifier();
                    if (isValidMethodModifier(clazzMethodModifier)) {
                        item.setMethodModifierText(clazzMethodModifier);
                        if(clazz.getIgnoredExceptions() != null && clazz.getIgnoredExceptions().length() > 0) {
                            item.setIgnoredExceptions(clazz.getIgnoredExceptions());
                        }
                        if(clazz.getMethods() != null) {
                            item.setMethodPatternConfigList(clazz.getMethods().stream().map(i ->
                                    new MethodPattern(i.getPattern(), i.getMethodModifier())).collect(Collectors.toList()));
                        }
                        if(clazz.getAnnotations() != null) {
                            item.setAnnotationMethodPatternConfigList(clazz.getAnnotations().stream().map(i ->
                                    new MethodPattern(i.getPattern(), i.getMethodModifier())).collect(Collectors.toList()));
                        }

                        if(item.compile()) {
                            list.add(item);
                        }
                    }
                }
            });
            javaMethodPattern = new JavaMethodPattern(list);
        }
    }

    public static void parseUrlConfig() {
        urlPattern = new UrlPattern(agentConfig.getUrl());
    }

    public static boolean isBlUrlActionNeedOther() {
        return false;
    }

    public static SentryAgentConfig getAgentConfig() {
        return agentConfig;
    }
}
