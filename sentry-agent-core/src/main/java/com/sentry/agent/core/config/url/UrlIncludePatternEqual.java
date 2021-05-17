package com.sentry.agent.core.config.url;

import com.sentry.agent.core.meta.MatchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlIncludePatternEqual extends AbstractUrlIncludePattern {

    public Map<String, UrlIncludeItem> patternMap = new HashMap<>();

    public UrlIncludePatternEqual(List<UrlIncludeItem> slist) {
        super(null, null, null);
        if (slist != null) {
            for(UrlIncludeItem item : slist) {
                this.patternMap.put(item.getSource(), item);
            }
        }
    }

    public boolean isValid() {
        return true;
    }

    public boolean matches(String url) {
        throw new RuntimeException("should not call");
    }

    public MatchResult matchAndTransform(String url) {
        UrlIncludeItem item = this.patternMap.get(url);
        if (item == null) {
            return null;
        } else {
            String resultUrl = url;
            String target = item.getTarget();
            if (target != null) {
                resultUrl = target;
            }

            MatchResult r = new MatchResult(resultUrl, item.getAct(), item.getActionSet());
            EqualInnerIncludePattern ipattern = item.getInnerPattern();
            if (null != ipattern) {
                ipattern.setParent(this);
            }
            r.setUrlIncludePattern(ipattern);
            return r;
        }
    }
}
