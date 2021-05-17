package com.sentry.agent.core.config.url;

public interface UrlExcludePattern {

    boolean match(String match);

    boolean isValid();
}
