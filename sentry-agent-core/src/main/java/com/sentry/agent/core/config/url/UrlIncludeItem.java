package com.sentry.agent.core.config.url;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UrlIncludeItem {

    private String source;
    private String target;
    private String action;
    private Set<String> actionSet = null;
    private String act;
    private final EqualInnerIncludePattern innerPattern;

    public UrlIncludeItem(String source, String target, String action, String successCode) {
        this.source = source;
        this.target = target;
        this.action = action;
        innerPattern = new EqualInnerIncludePattern();
        innerPattern.sethHttpSuccCodePattern(successCode);
        this.act = action;
        if (action != null && !action.equals("") && action.contains("=")) {
            String[] ss = action.split("=");
            if (ss.length == 2) {
                this.act = ss[0];
                String s2 = ss[1];
                if (s2 != null && s2.startsWith("(") && s2.endsWith(")") && s2.length() > 2) {
                    String[] op = s2.substring(1, s2.length() - 1).split(",");
                    if (op.length > 0) {
                        this.actionSet = new HashSet<>();
                        Collections.addAll(this.actionSet, op);
                    }
                }
            }
        }
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Set<String> getActionSet() {
        return this.actionSet;
    }

    public String getAct() {
        return this.act;
    }

    public EqualInnerIncludePattern getInnerPattern() {
        return this.innerPattern;
    }
}
