package com.sentry.agent.core.config.url;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlExcludePatternStartwith implements UrlExcludePattern {

    public List<String> patterns;

    public UrlExcludePatternStartwith(String s) {
        if(s != null) {
            this.patterns = new ArrayList<>(Arrays.asList(s.split(",")));
        }
    }

    public boolean match(String url) {
        return isValid() && this.patterns.stream().anyMatch(url::startsWith);
    }

    public boolean isValid() {
        return this.patterns != null && this.patterns.size() > 0;
    }
}
