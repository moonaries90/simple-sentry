package com.sentry.agent.core.plugin.asm;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.plugin.api.aggreagtor.AbstractPrimaryKeyValueAggregator;
import com.sentry.agent.core.plugin.api.binder.BaseMeterBinder;
import com.sentry.agent.core.plugin.api.stats.BaseStats;

import java.util.Stack;

public abstract class AsmAggregator<T extends BaseStats> extends AbstractPrimaryKeyValueAggregator<PrimaryKey, T> {

    protected abstract BaseMeterBinder getMeterBinder();

    private final ThreadLocal<Stack<Long>> startTime = new ThreadLocal<>();

    public void onStart(PrimaryKey key) {
        if(key != null) {
            T stats = super.getValue(key);
            if(getMeterBinder() != null) {
                getMeterBinder().addStats(stats);
            }
            long startTime = stats.onStart();
            Stack<Long> stack = this.startTime.get();
            if(stack == null) {
                stack = new Stack<>();
            }
            stack.push(startTime);
            this.startTime.set(stack);
        }
    }

    public void onFinally(PrimaryKey key) {
        if(key != null) {
            T stats = super.getValue(key, false);
            if(stats != null) {
                Stack<Long> stack = this.startTime.get();
                if(stack != null) {
                    Long startTime = stack.pop();
                    if(startTime != null) {
                        stats.onFinally(startTime);
                    }
                    if (stack.size() == 0) {
                        this.startTime.remove();
                    }
                }
            }
        }
    }

    public void onThrowable(Throwable t, PrimaryKey key) {
        if(key != null) {
            T stats = super.getValue(key, false);
            if(stats != null) {
                stats.onThrowable(t);
            }
        }
    }
}
