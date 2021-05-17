package com.sentry.agent.core.plugin.asm.http;

import com.sentry.agent.core.plugin.asm.AsmMethod;
import com.sentry.agent.core.plugin.asm.api.Matcher;
import org.objectweb.asm.Type;

public class HttpClientMatcher implements Matcher<AsmMethod> {

    private final String methodName;

    public HttpClientMatcher(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public boolean match(AsmMethod target) {
        if(methodName.equals(target.name)) {
            Type[] argTypes = Type.getArgumentTypes(target.desc);
            return argTypes.length == 3 &&
                    argTypes[0].getClassName().equals("org.apache.http.HttpHost") &&
                    argTypes[1].getClassName().equals("org.apache.http.HttpRequest") &&
                    argTypes[2].getClassName().equals("org.apache.http.protocol.HttpContext");
        }
        return false;
    }
}
