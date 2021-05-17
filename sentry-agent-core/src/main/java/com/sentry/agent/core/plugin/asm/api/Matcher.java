package com.sentry.agent.core.plugin.asm.api;

import com.sentry.agent.core.plugin.asm.AsmMethod;
import com.sentry.agent.core.util.Ops;

public interface Matcher<T> extends Ops {

    boolean match(T target);

    TrueMatcher TRUE_MATCHER = new TrueMatcher();

    class TrueMatcher implements Matcher<AsmMethod> {
        @Override
        public boolean match(AsmMethod target) {
            return true;
        }
    }
}
