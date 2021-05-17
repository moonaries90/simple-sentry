package com.sentry.agent.core.config.url;

import com.sentry.agent.core.config.Method;
import com.sentry.agent.core.config.props.UrlConfig;
import com.sentry.agent.core.meta.MatchResult;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class UrlPattern {

    private final LinkedList<UrlExcludePattern> excludePatternList = new LinkedList<>();

    private final LinkedList<UrlIncludePattern> includePatternList = new LinkedList<>();

    public UrlPattern(UrlConfig urlConfig) {
        if(urlConfig != null) {
            if (urlConfig.getExcludes() != null) {
                List<String> equalConfig = urlConfig.getExcludes().stream().filter(i -> i.getMethod() == Method.equal).map(UrlConfig.ExcludeConfig::getPattern).collect(Collectors.toList());
                this.excludePatternList.addAll(urlConfig.getExcludes().stream().map(UrlPattern::getExclude).filter(i -> i != null && i.isValid()).collect(Collectors.toList()));
                if (equalConfig.size() > 0) {
                    this.excludePatternList.addFirst(new UrlExcludePatternEqual(equalConfig));
                }
            }
            if (urlConfig.getIncludes() != null) {
                List<UrlConfig.IncludeConfig> equalConfig = urlConfig.getIncludes().stream().filter(i -> i.getMethod() == Method.equal).collect(Collectors.toList());
                this.includePatternList.addAll(urlConfig.getIncludes().stream().map(UrlPattern::getInclude).filter(i -> i != null && i.isValid()).collect(Collectors.toList()));
                if (equalConfig.size() > 0) {
                    List<UrlIncludeItem> includeItems = equalConfig.stream().map(i ->
                            new UrlIncludeItem(i.getPattern(), i.getTarget(), i.getAction(), i.getSuccessCode())).collect(Collectors.toList());
                    this.includePatternList.addFirst(new UrlIncludePatternEqual(includeItems));
                }
            }
        }
    }

    private static UrlIncludePattern getInclude(UrlConfig.IncludeConfig config) {
        if(config.getMethod() != null) {
            Class<? extends UrlIncludePattern> clazz = config.getMethod().getIncludeClass();
            try {
                Constructor<? extends UrlIncludePattern> constructor = clazz.getConstructor(String.class, String.class, String.class);
                UrlIncludePattern includePattern = constructor.newInstance(config.getPattern(), config.getTarget(), config.getAction());
                if(config.getSuccessCode() != null && config.getSuccessCode().length() > 0) {
                    includePattern.sethHttpSuccCodePattern(config.getSuccessCode());
                }
                return includePattern;
            } catch (Exception ignore) {

            }
        }
        return null;
    }

    private static UrlExcludePattern getExclude(UrlConfig.ExcludeConfig config) {
        if(config.getMethod() != null) {
            Class<? extends UrlExcludePattern> clazz = config.getMethod().getExcludeClass();
            try {
                Constructor<? extends UrlExcludePattern> constructor = clazz.getConstructor(String.class);
                return constructor.newInstance(config.getPattern());
            } catch (Exception ignore) {

            }
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
