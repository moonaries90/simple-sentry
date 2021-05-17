package com.sentry.agent.core.config.url;

public class UrlIncludePatternRest extends AbstractUrlIncludePattern {

    public UrlIncludePatternRest(String s, String t, String action) {
        super(s, t, action);
    }

    public boolean isValid() {
        return true;
    }

    public boolean matches(String url) {
        int i = url.indexOf(46);
        return i < 0;
    }
}
