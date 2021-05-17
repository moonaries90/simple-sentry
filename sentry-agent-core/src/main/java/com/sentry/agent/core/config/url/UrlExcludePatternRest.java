package com.sentry.agent.core.config.url;

public class UrlExcludePatternRest implements UrlExcludePattern {

    public UrlExcludePatternRest() {

    }

    public UrlExcludePatternRest(String ex) {

    }

    public boolean match(String url) {
        int i = url.indexOf('.');
        return i < 0;
    }

    public boolean isValid() {
        return true;
    }
}
