package com.sentry.agent.core.config.url;

public class UrlExcludePatternInclude implements UrlExcludePattern {

    private String start = null;

    private String end = null;

    public UrlExcludePatternInclude(String patten) {
        if (patten != null && !patten.equals("")) {
            String[] ss = patten.split(",");
            if (ss.length == 2) {
                this.start = ss[0].trim();
                this.end = ss[1].trim();
            }
        }
    }

    public boolean match(String url) {
        return url.endsWith(this.end) && url.startsWith(this.start);
    }

    public boolean isValid() {
        return this.start != null && this.end != null;
    }
}
