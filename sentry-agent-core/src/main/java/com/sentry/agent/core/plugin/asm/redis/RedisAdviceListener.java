package com.sentry.agent.core.plugin.asm.redis;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.plugin.asm.AsmAdviceListener;
import com.sentry.agent.core.plugin.asm.AsmAggregator;
import com.sentry.agent.core.util.IntegerMap;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Stack;

public class RedisAdviceListener extends AsmAdviceListener {

    static final IntegerMap hostPortMap = new IntegerMap(30);

    static final IntegerMap actionMap = new IntegerMap(200);

    private static final RedisAggregator redisAggregator = new RedisAggregator();

    @Override
    protected PrimaryKey preparePrimaryKey(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args) throws Throwable {
        Field clientField = target.getClass().getField("client");
        clientField.setAccessible(true);
        Object client = clientField.get(target);
        Method getHostMethod = client.getClass().getMethod("getHost");
        Method getPortMethod = client.getClass().getMethod("getPort");
        getHostMethod.setAccessible(true);
        getPortMethod.setAccessible(true);

        String host = (String) getHostMethod.invoke(client);
        int port = (int) getPortMethod.invoke(client);
        if(hostPortMap.registerResource(host + ":" + port) < 0) {
            return null;
        }
        if(actionMap.registerResource(methodName) < 0) {
            return null;
        }
        return new PrimaryKey(host, String.valueOf(port), methodName);
    }

    @Override
    protected AsmAggregator<?> getAggregator() {
        return redisAggregator;
    }

    @Override
    public void afterReturning(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) throws Throwable {
        if(returnObject != null || Type.getReturnType(methodDesc).getSort() == Type.VOID) {
            Stack<PrimaryKey> sqlStack = localStorage.get();
            if(sqlStack != null) {
                PrimaryKey key = sqlStack.pop();
                if (key != null) {
                    redisAggregator.onFinallyHint(key, true);
                }
                if(sqlStack.size() == 0) {
                    localStorage.set(null);
                }
            }
        } else {
            super.afterReturning(loader, className, methodName, methodDesc, target, args, returnObject);
        }
    }
}
