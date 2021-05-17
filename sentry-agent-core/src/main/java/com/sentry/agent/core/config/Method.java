package com.sentry.agent.core.config;

import com.sentry.agent.core.config.url.*;

public enum Method {

    regex(UrlIncludePatternRegex.class, UrlExcludePatternRegex.class),
    startwith(UrlIncludePatternStartwith.class, UrlExcludePatternStartwith.class),
    endwith(UrlIncludePatternEndwith.class, UrlExcludePatternEndwith.class),
    equal(UrlIncludePatternEqual.class, UrlExcludePatternEqual.class),
    all(UrlIncludePatternAll.class, UrlExcludePatternAll.class),
    rest(UrlIncludePatternRest.class, UrlExcludePatternRest.class),
    include(UrlIncludePatternInclude.class, UrlExcludePatternInclude.class);

    private Class<? extends UrlIncludePattern> includeClass;

    private Class<? extends UrlExcludePattern> excludeClass;

    public static Method parseByName(String name) {
        for(Method method : values()) {
            if(method.name().equals(name)) {
                return method;
            }
        }
        return null;
    }

    Method(Class<? extends UrlIncludePattern> includeClass, Class<? extends UrlExcludePattern> excludeClass) {
        this.includeClass = includeClass;
        this.excludeClass = excludeClass;
    }

    public Class<? extends UrlIncludePattern> getIncludeClass() {
        return includeClass;
    }

    public void setIncludeClass(Class<? extends UrlIncludePattern> includeClass) {
        this.includeClass = includeClass;
    }

    public Class<? extends UrlExcludePattern> getExcludeClass() {
        return excludeClass;
    }

    public void setExcludeClass(Class<? extends UrlExcludePattern> excludeClass) {
        this.excludeClass = excludeClass;
    }
}
