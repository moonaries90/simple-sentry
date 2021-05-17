package com.sentry.agent.core.config.props;

import java.util.List;

public class JavaMethodPatternConfig {
    /**
     * 模式
     */
    private String pattern;

    // 限定符匹配， 可以为空， 或者是 public|private|protected
    private String methodModifier;

    // 忽略的异常， 如果可以加载类， 那么会使用类型匹配（包括父类）， 如果无法加载， 会使用类的全限定名匹配, 逗号分隔
    private String ignoredExceptions;

    private List<JavaMethodPatternConfig> methods;

    private List<JavaMethodPatternConfig> annotations;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getMethodModifier() {
        return methodModifier;
    }

    public void setMethodModifier(String methodModifier) {
        this.methodModifier = methodModifier;
    }

    public List<JavaMethodPatternConfig> getMethods() {
        return methods;
    }

    public void setMethods(List<JavaMethodPatternConfig> methods) {
        this.methods = methods;
    }

    public List<JavaMethodPatternConfig> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<JavaMethodPatternConfig> annotations) {
        this.annotations = annotations;
    }

    public String getIgnoredExceptions() {
        return ignoredExceptions;
    }

    public void setIgnoredExceptions(String ignoredExceptions) {
        this.ignoredExceptions = ignoredExceptions;
    }
}
