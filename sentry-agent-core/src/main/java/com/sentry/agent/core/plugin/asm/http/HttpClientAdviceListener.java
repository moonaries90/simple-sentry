package com.sentry.agent.core.plugin.asm.http;

import com.sentry.agent.core.plugin.api.PrimaryKey;
import com.sentry.agent.core.plugin.asm.AsmAdviceListener;
import com.sentry.agent.core.util.IntegerMap;

import java.lang.reflect.Method;
import java.util.Stack;

public class HttpClientAdviceListener extends AsmAdviceListener {

    private final HttpClientAggregator httpClientAggregator = new HttpClientAggregator();

    private static Class<?> httpUriRequestClass = null;

    private static final IntegerMap urlCounter = new IntegerMap(2000);

    static {
        try {
            httpUriRequestClass = Thread.currentThread().getContextClassLoader().loadClass("org.apache.http.client.methods.HttpUriRequest");
        } catch (Exception ignore) {}
    }

    @Override
    protected PrimaryKey preparePrimaryKey(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args) throws Throwable {
        if(!"close".equals(methodName)) {
            Method toURIMethod = args[0].getClass().getMethod("toURI");
            toURIMethod.setAccessible(true);
            Method getRequestLineMethod = args[1].getClass().getMethod("getRequestLine");
            getRequestLineMethod.setAccessible(true);
            Object requestLine = getRequestLineMethod.invoke(args[1]);
            Method uriMethod = requestLine.getClass().getMethod("getUri");
            uriMethod.setAccessible(true);
            String methodType = "unknown";
            if (httpUriRequestClass != null && httpUriRequestClass.isAssignableFrom(args[1].getClass())) {
                Method typeMethod = args[1].getClass().getMethod("getMethod");
                typeMethod.setAccessible(true);
                methodType = (String) typeMethod.invoke(args[1]);
            }
            String host = (String) toURIMethod.invoke(args[0]);
            String requestUri = (String) uriMethod.invoke(requestLine);
            if(urlCounter.registerResource(requestUri) >= 0) {
                String[] params = new String[]{
                        host,
                        getRequestUri(host, requestUri),
                        methodType};
                return new PrimaryKey(params);
            }
        }
        return null;
    }

    @Override
    protected HttpClientAggregator getAggregator() {
        return httpClientAggregator;
    }

    @Override
    public void before(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args) throws Throwable {
        if ("close".equals(methodName)) {
            super.afterReturning(loader, className, methodName, methodDesc, target, args, null);
        } else {
            super.before(loader, className, methodName, methodDesc, target, args);
        }
    }

    @Override
    public void afterReturning(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) throws Throwable {
        if(!"close".equals(methodName) && returnObject != null) {
            Method getStatusLineMethod = returnObject.getClass().getMethod("getStatusLine");
            getStatusLineMethod.setAccessible(true);
            Object statusLine = getStatusLineMethod.invoke(returnObject);
            if (statusLine != null) {
                Method statusCodeMethod = statusLine.getClass().getMethod("getStatusCode");
                statusCodeMethod.setAccessible(true);
                Stack<PrimaryKey> stack = localStorage.get();
                if (stack != null) {
                    PrimaryKey key = stack.peek();
                    if (key != null) {
                        httpClientAggregator.onStatusCode((int) statusCodeMethod.invoke(statusLine), key);
                    }
                }
            }
            super.afterReturning(loader, className, methodName, methodDesc, target, args, returnObject);
        }
    }

    @Override
    public void afterThrowing(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) throws Throwable {
        if(!"close".equals(methodName)) {
            super.afterThrowing(loader, className, methodName, methodDesc, target, args, throwable);
        }
    }

    private static String getRequestUri(String url, String host) {
        if(url == null) {
            return url;
        }
        url = url.replace(host, "");
        if(!url.contains("?")) {
            return url;
        }
        return url.substring(0, url.indexOf("?"));
    }
}
