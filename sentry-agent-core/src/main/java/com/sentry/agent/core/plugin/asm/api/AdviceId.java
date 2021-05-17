package com.sentry.agent.core.plugin.asm.api;

import java.util.concurrent.atomic.AtomicInteger;

public interface AdviceId {

    AtomicInteger id = new AtomicInteger(0);

    default int nextId() {
        return id.incrementAndGet();
    };
}
