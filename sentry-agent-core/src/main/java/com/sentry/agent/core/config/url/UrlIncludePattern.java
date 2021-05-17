package com.sentry.agent.core.config.url;

import com.sentry.agent.core.config.Method;
import com.sentry.agent.core.config.SuccCodeValue;
import com.sentry.agent.core.meta.MatchResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class UrlIncludePattern {

    private final String pattern, target;

    private String act = "";

    private Set<String> actionValueSet = new HashSet<>();

    private final SuccCodeValue codeValue;

    private final Method method;

    public UrlIncludePattern(Method method, String pattern, String action, String target, String successCode) {
        this.method = method;
        this.pattern = pattern;
        this.target = target;
        successCode = successCode != null && successCode.length() > 0 ? successCode : "200,201,301,302";
        this.codeValue = new SuccCodeValue(successCode);
        action = action != null ? action.trim() : "";
        if (action.contains("=")) {
            String[] ss = action.split("=");
            if (ss.length == 2) {
                this.act = ss[0];
                String s2 = ss[1];
                if (s2 != null && s2.startsWith("(") && s2.endsWith(")") && s2.length() > 2) {
                    String[] op = s2.substring(1, s2.length() - 1).split(",");
                    if (op.length > 0) {
                        this.actionValueSet = new HashSet<>(Arrays.asList(op));
                    }
                }
            }
        }
    }

    public MatchResult matchAndTransform(String url) {
        if (!this.matches(url)) {
            return null;
        } else {
            String resultUrl;
            if (this.target == null || this.target.length() == 0) {
                resultUrl = url;
            } else {
                resultUrl = this.target;
            }
            MatchResult r = new MatchResult(resultUrl, this.act, this.actionValueSet);
            r.setUrlIncludePattern(this);
            return r;
        }
    }

    private boolean matches(String url) {
        switch (this.method) {
            case all: {
                return true;
            }
            case equal: {
                return this.pattern != null && Objects.equals(this.pattern, url);
            }
            case include: {
                if (this.pattern != null && !this.pattern.equals("")) {
                    String start, end;
                    String[] ss = this.pattern.split(",");
                    if (ss.length == 2) {
                        start = ss[0].trim();
                        end = ss[1].trim();
                        return url.endsWith(end) && url.startsWith(start);
                    }
                }
                return false;
            }
            case regex: {
                try {
                    return Pattern.compile(this.pattern).matcher(url).matches();
                } catch (Exception ignore) {
                    return false;
                }
            }
            case rest: {
                return url != null && url.contains(".");
            }
            case startwith: {
                return this.pattern != null && Arrays.stream(this.pattern.split(",")).anyMatch(url::startsWith);
            }
            case endwith: {
                return this.pattern != null && Arrays.stream(this.pattern.split(",")).anyMatch(url::endsWith);
            }
            default: {
                return false;
            }
        }
    }

    public boolean isSuccessCode(int code) {
        return null == this.codeValue || this.codeValue.isSuccCode(code);
    }
}
