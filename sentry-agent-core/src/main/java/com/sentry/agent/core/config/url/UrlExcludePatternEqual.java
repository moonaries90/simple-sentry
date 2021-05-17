package com.sentry.agent.core.config.url;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UrlExcludePatternEqual implements UrlExcludePattern {

    public Set<String> patternSet = new HashSet<>();

    public UrlExcludePatternEqual(List<String> list) {
        if (list != null) {
            this.patternSet.addAll(list);
        }
    }

    public boolean match(String url) {
        return this.patternSet.contains(url);
    }

    public boolean isValid() {
        return true;
    }
}
