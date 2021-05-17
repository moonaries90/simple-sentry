package com.sentry.agent.core.config.url;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlIncludePatternEndwith extends AbstractUrlIncludePattern {

    private List<String> patterns;

    public UrlIncludePatternEndwith(String s, String t, String action) {
        super(s, t, action);
        if(s != null) {
            this.patterns = new ArrayList<>(Arrays.asList(s.split(",")));
        }
    }

    public boolean isValid() {
        return this.patterns != null && this.patterns.size() > 0;
    }

    public boolean matches(String url) {
        return isValid() && this.patterns.stream().anyMatch(url::endsWith);
    }
}
