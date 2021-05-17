package com.sentry.agent.core.plugin.asm;

public class AsmMethod {

    public final int access;

    public final String name;

    public final String desc;

    AsmMethod(int access, String name, String desc) {
        this.access = access;
        this.name = name;
        this.desc = desc;
    }
}
