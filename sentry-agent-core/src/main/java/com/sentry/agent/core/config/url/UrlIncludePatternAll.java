package com.sentry.agent.core.config.url;

public class UrlIncludePatternAll extends AbstractUrlIncludePattern {

    public UrlIncludePatternAll(String s, String t, String action) {
        super(s, t, action);
    }

    public boolean isValid() {
        return true;
    }

    public boolean matches(String url) {
        return true;
    }
}
