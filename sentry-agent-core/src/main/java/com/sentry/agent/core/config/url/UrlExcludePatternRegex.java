package com.sentry.agent.core.config.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlExcludePatternRegex implements UrlExcludePattern {

    private String pattern;

    private Pattern patt;

    public UrlExcludePatternRegex(String s) {
        this.pattern = s;
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String toString() {
        return this.pattern;
    }

    public boolean match(String url) {
        Matcher matcher = this.patt.matcher(url);
        return matcher.matches();
    }

    public boolean isValid() {
        try {
            this.patt = Pattern.compile(this.pattern);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }
}
