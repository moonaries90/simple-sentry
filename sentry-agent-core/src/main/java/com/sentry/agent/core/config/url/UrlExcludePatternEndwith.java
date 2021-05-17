package com.sentry.agent.core.config.url;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlExcludePatternEndwith implements UrlExcludePattern {

    private List<String> patterns;

    public UrlExcludePatternEndwith(String s) {
        if(s != null) {
            this.patterns = new ArrayList<>(Arrays.asList(s.split(",")));
        }
    }

    public boolean match(String url) {
        return isValid() && patterns.stream().anyMatch(url::endsWith);
    }

    public boolean isValid() {
        return this.patterns != null && this.patterns.size() > 0;
    }
}
