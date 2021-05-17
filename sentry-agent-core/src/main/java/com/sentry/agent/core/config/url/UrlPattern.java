package com.sentry.agent.core.config.url;

import com.sentry.agent.core.config.props.UrlConfig;
import com.sentry.agent.core.meta.MatchResult;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

public class UrlPattern {

    private final LinkedList<UrlExcludePattern> excludePatternList = new LinkedList<>();

    private final LinkedList<UrlIncludePattern> includePatternList = new LinkedList<>();

    public UrlPattern(UrlConfig urlConfig) {
        if(urlConfig != null) {
            if (urlConfig.getExcludes() != null) {
                this.excludePatternList.addAll(urlConfig.getExcludes().stream().map(UrlPattern::getExclude).filter(Objects::nonNull).collect(Collectors.toList()));
            }
            if (urlConfig.getIncludes() != null) {
                this.includePatternList.addAll(urlConfig.getIncludes().stream().map(UrlPattern::getInclude).filter(Objects::nonNull).collect(Collectors.toList()));
            }
        }
    }

    private static UrlIncludePattern getInclude(UrlConfig.IncludeConfig config) {
        if(config.getMethod() != null) {
            return new UrlIncludePattern(config.getMethod(), config.getPattern(), config.getAction(), config.getTarget(), config.getSuccessCode());
        }
        return null;
    }

    private static UrlExcludePattern getExclude(UrlConfig.ExcludeConfig config) {
        if(config.getMethod() != null) {
            return new UrlExcludePattern(config.getMethod(), config.getPattern());
        }
        return null;
    }

    public MatchResult matchedUrl(String url) {
        if(this.excludePatternList.stream().anyMatch(i -> i.match(url))) {
            return null;
        }
        for(UrlIncludePattern includePattern : this.includePatternList) {
            MatchResult result = includePattern.matchAndTransform(url);
            if(result != null) {
                return result;
            }
        }
        return null;
    }
}
