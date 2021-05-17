package com.sentry.agent.core.plugin.asm.method;

import com.sentry.agent.core.config.JavaMethodPatternItem;
import com.sentry.agent.core.plugin.asm.AsmMethod;
import com.sentry.agent.core.plugin.asm.api.Matcher;

import java.util.Arrays;
import java.util.List;

public class MethodMatcher implements Matcher<AsmMethod> {

    private static final List<String> ignored = Arrays.asList("getClass", "hashCode", "wait", "equals", "clone", "toString", "notify", "notifyAll", "finalize", "main");

    private final JavaMethodPatternItem item;

    public MethodMatcher(JavaMethodPatternItem item) {
        this.item = item;
    }

    @Override
    public boolean match(AsmMethod param) {
        int access = param.access;
        String name = param.name;
        if (name != null && !name.contains("$") && !name.startsWith("get") && !name.startsWith("set") && !name.startsWith("is")) {
            if(!"<init>".equals(name) && !"<clinit>".equals(name)) {
                if((access & ACC_NATIVE) == 0 && ((access & ACC_ABSTRACT) == 0) && (access & ACC_BRIDGE) == 0) {
                    return !ignored.contains(name) && item.matchMethod(access);
                }
            }
        }
        return false;
    }
}
