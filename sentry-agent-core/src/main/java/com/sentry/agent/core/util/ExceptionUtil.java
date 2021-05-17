package com.sentry.agent.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    public static int MAX_ERROR_STACK_LENGTH = 1024;

    public static String getThrowableStackTrace(Throwable t) {
        return getThrowableStackTrace(t, MAX_ERROR_STACK_LENGTH);
    }

    public static String getThrowableStackTrace(Throwable t, int maxStackLength) {
        if (maxStackLength <= 0) {
            maxStackLength = MAX_ERROR_STACK_LENGTH;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String s = sw.toString();
        if (s.length() > maxStackLength) {
            s = s.substring(0, maxStackLength);
        }
        return s;
    }
}
