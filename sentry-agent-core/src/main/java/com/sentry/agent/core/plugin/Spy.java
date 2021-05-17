package com.sentry.agent.core.plugin;

import java.lang.reflect.Method;

public class Spy {

    // -- 各种Advice的钩子引用 --
    public static volatile Method ON_BEFORE_METHOD;
    public static volatile Method ON_RETURN_METHOD;
    public static volatile Method ON_THROWS_METHOD;

    /*
     * 用于普通的间谍初始化
     */
    public static void init(
            Method onBeforeMethod,
            Method onReturnMethod,
            Method onThrowsMethod) {
        ON_BEFORE_METHOD = onBeforeMethod;
        ON_RETURN_METHOD = onReturnMethod;
        ON_THROWS_METHOD = onThrowsMethod;
    }

    public static void clean() {
        ON_BEFORE_METHOD = null;
        ON_RETURN_METHOD = null;
        ON_THROWS_METHOD = null;
    }
}
