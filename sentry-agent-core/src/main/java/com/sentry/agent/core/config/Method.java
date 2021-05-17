package com.sentry.agent.core.config;

public enum Method {

    regex,
    startwith,
    endwith,
    equal,
    all,
    rest,
    include;

    public static Method parseByName(String name) {
        for (Method method : values()) {
            if (method.name().equals(name)) {
                return method;
            }
        }
        return null;
    }
}
