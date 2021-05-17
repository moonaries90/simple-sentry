package com.sentry.agent.core.config.url;

public class UrlIncludePatternInclude extends AbstractUrlIncludePattern {

    private String start = null;
    private String end = null;

    public UrlIncludePatternInclude(String s, String t, String action) {
        super(s, t, action);
        if (s != null && !s.equals("")) {
            String[] ss = s.split(",");
            if (ss.length == 2) {
                this.start = ss[0].trim();
                this.end = ss[1].trim();
            }
        }
    }

    public boolean isValid() {
        return this.start != null && this.end != null;
    }

    public boolean matches(String url) {
        return url.endsWith(this.end) && url.startsWith(this.start);
    }
}
