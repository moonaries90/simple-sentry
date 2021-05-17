package com.sentry.agent.core.config.url;

import com.sentry.agent.core.config.Method;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class UrlExcludePattern {

    private final Method method;

    private final String pattern;

    public UrlExcludePattern(Method method, String pattern) {
        this.method = method;
        this.pattern = pattern;
    }

    public boolean match(String url) {
        switch (method) {
            case all: {
                return false;
            }
            case equal: {
                return Objects.equals(url, this.pattern);
            }
            case include: {
                String start, end;
                if (this.pattern != null && url != null && !this.pattern.equals("")) {
                    String[] ss = this.pattern.split(",");
                    if (ss.length == 2) {
                        start = ss[0].trim();
                        end = ss[1].trim();
                        return url.endsWith(end) && url.startsWith(start);
                    }
                }
                return false;
            }
            case rest: {
                return url != null && !url.contains(".");
            }
            case regex: {
                try {
                    return Pattern.compile(this.pattern).matcher(url).matches();
                } catch (Exception ignore) {
                    return false;
                }
            }
            case endwith: {
                return url != null && Arrays.stream(this.pattern.split(",")).anyMatch(url::endsWith);
            }
            case startwith: {
                return url != null && Arrays.stream(this.pattern.split(",")).anyMatch(url::startsWith);
            }
            default:
                return false;
        }
    }
}
