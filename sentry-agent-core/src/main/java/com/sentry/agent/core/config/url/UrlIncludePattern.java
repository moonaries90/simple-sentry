package com.sentry.agent.core.config.url;

import com.sentry.agent.core.meta.MatchResult;

public interface UrlIncludePattern {

    MatchResult matchAndTransform(String url);

    boolean isValid();

    void sethHttpSuccCodePattern(String pattern);

    boolean isSuccCode(int code);
}
