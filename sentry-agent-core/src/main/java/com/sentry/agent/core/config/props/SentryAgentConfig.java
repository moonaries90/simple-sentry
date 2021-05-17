package com.sentry.agent.core.config.props;

import java.util.List;
import java.util.Map;

public class SentryAgentConfig {

    private Map<String, String> tags;

    private List<JavaMethodPatternConfig> javaMethods;

    private UrlConfig url;

    private EnabledConfig enabled = new EnabledConfig();

    public List<JavaMethodPatternConfig> getJavaMethods() {
        return javaMethods;
    }

    public void setJavaMethods(List<JavaMethodPatternConfig> javaMethods) {
        this.javaMethods = javaMethods;
    }

    public UrlConfig getUrl() {
        return url;
    }

    public void setUrl(UrlConfig url) {
        this.url = url;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public EnabledConfig getEnabled() {
        return enabled;
    }

    public void setEnabled(EnabledConfig enabled) {
        this.enabled = enabled;
    }
}
