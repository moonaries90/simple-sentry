package com.sentry.agent.core.config.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlIncludePatternRegex extends AbstractUrlIncludePattern {

    private Pattern pattern;

    public UrlIncludePatternRegex(String s, String t, String action) {
        super(s, t, action);
    }

    public boolean isValid() {
        if (this.source == null || this.source.equals("")) {
            return false;
        } else {
            try {
                this.pattern = Pattern.compile(this.source);
                return true;
            } catch (Exception ignore) {
                return false;
            }
        }
    }

    public boolean matches(String url) {
        Matcher m = this.pattern.matcher(url);
        return m.matches();
    }
}
