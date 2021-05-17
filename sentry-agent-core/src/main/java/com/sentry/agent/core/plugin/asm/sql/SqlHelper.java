package com.sentry.agent.core.plugin.asm.sql;

import com.sentry.agent.core.util.DoNotChange;
import com.sentry.agent.core.util.IntegerMap;

public class SqlHelper {

    public static final IntegerMap CONNECTION_TARGET_MAP = new IntegerMap(200);

    public static String getResource(int resourceId) {
        return CONNECTION_TARGET_MAP.getResource(resourceId);
    }

    @DoNotChange
    public static int getConnectionId(String url) {
        return null == url ? -1 : CONNECTION_TARGET_MAP.registerResource(url);
    }

    @DoNotChange
    public static String getDbName(String origHostToConnectTo, int origPortToConnectTo, String origDatabaseToConnectTo) {
        StringBuilder sb = new StringBuilder();
        if (origHostToConnectTo.indexOf(":") > 0) {
            sb.append(origHostToConnectTo).append(":").append(origDatabaseToConnectTo);
        } else {
            sb.append(origHostToConnectTo).append(":").append(origPortToConnectTo).append(":").append(origDatabaseToConnectTo);
        }
        return sb.toString();
    }
}
