package com.sentry.agent.core.config.url;

public class UrlExcludePatternAll implements UrlExcludePattern {

    public UrlExcludePatternAll() {

    }

    public UrlExcludePatternAll(String ex) {

    }

    public boolean match(String url) {
        return true;
    }

    public boolean isValid() {
        return true;
    }
}
