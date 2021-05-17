package com.sentry.agent.core.config.url;

import com.sentry.agent.core.config.SuccCodeValue;
import com.sentry.agent.core.meta.MatchResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractUrlIncludePattern implements UrlIncludePattern {

    private final String target;

    protected String source;

    private Set<String> actionValueSet;

    private String act;

    private int traceThresholdTimeMiss = 0;

    private static final String defaultSuccCodeValue = "200,301,302";

    private static final SuccCodeValue defaultCodeValue = new SuccCodeValue(defaultSuccCodeValue);

    private String httpSuccCodePattern;

    private SuccCodeValue codeValue;

    public AbstractUrlIncludePattern(String source, String target, String action) {
        this.httpSuccCodePattern = defaultSuccCodeValue;
        this.codeValue = defaultCodeValue;
        this.source = source;
        this.target = target;
        if (action != null) {
            action = action.trim();
        }

        this.act = action;
        if (action != null && action.contains("=")) {
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

    public void sethHttpSuccCodePattern(String value) {
        if (value != null && value.length() > 0) {
            this.httpSuccCodePattern = this.httpSuccCodePattern + "," + value;
            this.codeValue = new SuccCodeValue(this.httpSuccCodePattern);
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

    public abstract boolean matches(String str);

    public void setTraceThresholdTimeMiss(int timeInMiss) {
        this.traceThresholdTimeMiss = timeInMiss;
    }

    public int getTraceThresholdTimeMiss() {
        return this.traceThresholdTimeMiss;
    }

    public boolean isSuccCode(int code) {
        return null == this.codeValue || this.codeValue.isSuccCode(code);
    }
}
