package com.sentry.agent.core.config;

import com.sentry.agent.core.config.props.MethodPattern;
import com.sentry.agent.core.util.ModifierUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaMethodPatternItem {

    private String classNamePattern;

    private Pattern classNamePatternExe;

    private String methodModifierText;

    private int methodModifier = 1;

    private List<MethodPattern> methodPatternList;

    private List<MethodPattern> annotationMethodPatternList;

    private String ignoredExceptions;

    public boolean compile() {
        try {
            this.classNamePatternExe = Pattern.compile(this.classNamePattern);
            this.methodModifier = ModifierUtil.computeModifierVal(this.methodModifierText, 1);
        } catch (Exception ignore) {
            return false;
        }

        try {
            if (null != this.methodPatternList && !this.methodPatternList.isEmpty()) {
                for(MethodPattern methodPattern : this.methodPatternList) {
                    methodPattern.compile(this.methodModifierText);
                }
            }
        } catch (Throwable ignore) {
            return false;
        }

        try {
            if (null != this.annotationMethodPatternList && !this.annotationMethodPatternList.isEmpty()) {
                for(MethodPattern methodPattern : this.annotationMethodPatternList) {
                    methodPattern.compile(this.methodModifierText);
                }
            }
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }

    public String getClassNamePattern() {
        return this.classNamePattern;
    }

    public void setClassNamePattern(String classNamePattern) {
        this.classNamePattern = classNamePattern;
    }

    boolean classMatch(String className) {
        Matcher m = this.classNamePatternExe.matcher(className);
        return m.matches();
    }

    public boolean matchMethod(int access) {
        if((access & this.methodModifier) == 0) {
            return false;
        } else {
            return (null == this.methodPatternList || this.methodPatternList.isEmpty()) &&
                    (null == this.annotationMethodPatternList || this.annotationMethodPatternList.isEmpty());
        }
     }

    public void setMethodModifierText(String methodModifierText) {
        this.methodModifierText = methodModifierText;
    }

    public void setMethodPatternConfigList(List<MethodPattern> methodPatternList) {
        this.methodPatternList = methodPatternList;
    }

    public void setAnnotationMethodPatternConfigList(List<MethodPattern> annotationMethodPatternList) {
        this.annotationMethodPatternList = annotationMethodPatternList;
    }

    public String getIgnoredExceptions() {
        return ignoredExceptions;
    }

    public void setIgnoredExceptions(String ignoredExceptions) {
        this.ignoredExceptions = ignoredExceptions;
    }
}
