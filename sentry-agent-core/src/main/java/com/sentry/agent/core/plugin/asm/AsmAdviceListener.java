package com.sentry.agent.core.plugin.asm;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.plugin.asm.api.AdviceListener;

import java.util.Stack;

public abstract class AsmAdviceListener implements AdviceListener {

    protected final ThreadLocal<Stack<PrimaryKey>> localStorage = new ThreadLocal<>();

    protected abstract PrimaryKey preparePrimaryKey(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args) throws Throwable;

    protected abstract AsmAggregator<?> getAggregator();

    @Override
    public void before(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args) throws Throwable {
        PrimaryKey primaryKey = preparePrimaryKey(loader, className, methodName, methodDesc, target, args);
        if(primaryKey != null) {
            getAggregator().onStart(primaryKey);
            Stack<PrimaryKey> sqlStack = localStorage.get();
            if (sqlStack == null) {
                sqlStack = new Stack<>();
            }
            sqlStack.push(primaryKey);
            localStorage.set(sqlStack);
        }
    }

    @Override
    public void afterReturning(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) throws Throwable {
        Stack<PrimaryKey> sqlStack = localStorage.get();
        if(sqlStack != null) {
            PrimaryKey key = sqlStack.pop();
            if (key != null) {
                getAggregator().onFinally(key);
            }
            if(sqlStack.size() == 0) {
                localStorage.set(null);
            }
        }
    }

    @Override
    public void afterThrowing(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) throws Throwable {
        Stack<PrimaryKey> sqlStack = localStorage.get();
        if(sqlStack != null) {
            PrimaryKey key = sqlStack.pop();
            if (key != null) {
                getAggregator().onThrowable(throwable, key);
                getAggregator().onFinally(key);
            }
            if(sqlStack.size() == 0) {
                localStorage.set(null);
            }
        }
    }
}
