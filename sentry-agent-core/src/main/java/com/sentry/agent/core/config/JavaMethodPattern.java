package com.sentry.agent.core.config;

import java.util.List;

public class JavaMethodPattern {

    private List<JavaMethodPatternItem> patternItemList;

    public JavaMethodPattern(List<JavaMethodPatternItem> patternItemList) {
        this.patternItemList = patternItemList;
    }

    public List<JavaMethodPatternItem> getPatternItemList() {
        return this.patternItemList;
    }

    public void setPatternItemList(List<JavaMethodPatternItem> patternItemList) {
        this.patternItemList = patternItemList;
    }

    public JavaMethodPatternItem classNameMatch(String className) {
        if (this.patternItemList.isEmpty()) {
            return null;
        } else {
            for(JavaMethodPatternItem patternItem : this.patternItemList) {
                if(patternItem.classMatch(className)) {
                    return patternItem;
                }
            }
            return null;
        }
    }
}
