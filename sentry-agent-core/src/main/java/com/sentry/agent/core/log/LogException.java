package com.sentry.agent.core.log;

import com.sentry.agent.core.util.ExceptionUtil;

public class LogException {

    public static String toString(Throwable t) {
        return ExceptionUtil.getThrowableStackTrace(t);
    }
}
