package com.sentry.agent.core.plugin.asm.method;

import com.sentry.agent.core.config.JavaMethodPatternItem;
import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.plugin.asm.AsmAdviceListener;
import com.sentry.agent.core.plugin.asm.AsmAggregator;
import com.sentry.agent.core.util.IntegerMap;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MethodAdviceListener extends AsmAdviceListener {

    private static final MethodAggregator methodAggregator = new MethodAggregator();

    private final JavaMethodPatternItem item;

    private static final IntegerMap counterMap = new IntegerMap(2000);

    private static final ConcurrentMap<String, List<Class<?>>> ignoredExceptionClassMap = new ConcurrentHashMap<>();

    public MethodAdviceListener(JavaMethodPatternItem item) {
        this.item = item;
    }

    @Override
    protected PrimaryKey preparePrimaryKey(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args) {
        String[] params = new String[]{className, getFullMethodName(methodName, methodDesc)};
        if(counterMap.registerResource(params[0] + "#" + params[1]) >= 0) {
            return new PrimaryKey(params);
        }
        return null;
    }

    @Override
    protected AsmAggregator<?> getAggregator() {
        return methodAggregator;
    }

    @Override
    public void afterThrowing(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) throws Throwable {
        String ignoredExceptions = item.getIgnoredExceptions();
        boolean throwout = true;
        if (ignoredExceptions != null && ignoredExceptions.length() > 0) {
            List<Class<?>> classes = ignoredExceptionClassMap.computeIfAbsent(ignoredExceptions, (i) -> {
                List<Class<?>> list = new ArrayList<>();
                for (String e : i.split(",")) {
                    try {
                        list.add(Thread.currentThread().getContextClassLoader().loadClass(e));
                    } catch (Exception ignore) {
                    }
                }
                return list;
            });
            if (classes.size() > 0) {
                for (Class<?> c : classes) {
                    if (c.isAssignableFrom(throwable.getClass())) {
                        throwout = false;
                        break;
                    }
                }
            }
        }
        if (throwout) {
            super.afterThrowing(loader, className, methodName, methodDesc, target, args, throwable);
        } else {
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
    }

    private String getFullMethodName(String methodName, String methodDesc) {
        Type[] types = Type.getArgumentTypes(methodDesc);
        StringBuilder r = new StringBuilder(methodName).append("(");
        if(types.length > 0) {
            for(Type type : types) {
                String typeClassName = type.getClassName();
                if(typeClassName.contains(".")) {
                    typeClassName = typeClassName.substring(typeClassName.lastIndexOf(".") + 1);
                }
                r.append(typeClassName).append(";");
            }
            r.deleteCharAt(r.length() - 1);
        }
        r.append(")");
        return r.toString();
    }
}
