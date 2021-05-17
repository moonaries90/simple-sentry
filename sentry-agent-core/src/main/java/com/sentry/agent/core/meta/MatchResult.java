package com.sentry.agent.core.meta;

import com.sentry.agent.core.config.ConfigManager;
import com.sentry.agent.core.config.url.UrlIncludePattern;

import java.util.Map;
import java.util.Set;

public class MatchResult {

    public String targetUrl;

    public String action;

    public Set<String> actionValueSet;

    private UrlIncludePattern urlIncludePattern;

    public MatchResult(String targetUrl, String action, Set<String> actionSet) {
        this.targetUrl = targetUrl;
        this.action = action;
        this.actionValueSet = actionSet;
    }

    public boolean hasAction() {
        return this.action != null && this.action.length() > 0;
    }

    public String transform(Map<String, String[]> parameterMap) {
        StringBuilder sb = new StringBuilder();
        if (parameterMap != null && !parameterMap.isEmpty()) {
            String[] vv = parameterMap.get(this.action);
            if (vv != null && vv.length != 0) {
                if (this.actionValueSet == null || this.actionValueSet.contains(vv[0])) {
                    sb.append(this.targetUrl).append("?").append(this.action).append("=");
                    sb.append(vv[0]);
                }
            }
        }
        if(sb.length() == 0 && ConfigManager.isBlUrlActionNeedOther()) {
            sb.append(this.targetUrl).append("?").append(this.action).append("=");
            return sb.toString();
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    public UrlIncludePattern getUrlIncludePattern() {
        return this.urlIncludePattern;
    }

    public void setUrlIncludePattern(UrlIncludePattern urlIncludePattern) {
        this.urlIncludePattern = urlIncludePattern;
    }
}
