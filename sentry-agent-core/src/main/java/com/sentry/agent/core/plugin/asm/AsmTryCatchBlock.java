package com.sentry.agent.core.plugin.asm;

import org.objectweb.asm.Label;

public class AsmTryCatchBlock {

    public final Label start;
    public final Label end;
    public final Label handler;
    public final String type;

    AsmTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.start = start;
        this.end = end;
        this.handler = handler;
        this.type = type;
    }
}
