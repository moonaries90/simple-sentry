package com.sentry.agent.core.plugin.api.transformer;

import com.sentry.agent.core.util.Ops;

public interface SentryTransformer extends Ops {

    String line = System.lineSeparator();

    default boolean canReTransform(Class<?> clazz) {
        return false;
    }
}
