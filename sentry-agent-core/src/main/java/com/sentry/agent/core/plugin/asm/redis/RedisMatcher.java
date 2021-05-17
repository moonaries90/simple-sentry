package com.sentry.agent.core.plugin.asm.redis;

import com.sentry.agent.core.plugin.asm.AsmMethod;
import com.sentry.agent.core.plugin.asm.api.Matcher;

import java.util.HashSet;
import java.util.Set;

public class RedisMatcher implements Matcher<AsmMethod> {

    private final Set<String> noInterceptorMethodNames = new HashSet<String>() {{
        add("close");
        add("setDataSource");
        add("sentinelGetMasterAddrByName");
        add("subscribe");
        add("publish");
        add("psubscribe");
        add("dump");
        add("restore");
        add("pubsubChannels");
        add("pubsubNumPat");
        add("pubsubNumSub");

        add("auth");
        add("connect");
        add("getClient");
        add("getDB");
        add("resetState");
        add("disconnect");
        add("ping");
        add("quit");
        add("dbSize");
        add("watch");
        add("unwatch");
        add("pipelined");
        add("shutdown");
        add("info");
        add("isConnected");
    }};

    @Override
    public boolean match(AsmMethod target) {
        return isPublic(target.access) &&
                !target.name.startsWith("sentinel") &&
                !target.name.startsWith("cluster") &&
                !noInterceptorMethodNames.contains(target.name) &&
                !isAbstract(target.access) &&
                !isNative(target.access) &&
                !isStatic(target.access);
    }
}
