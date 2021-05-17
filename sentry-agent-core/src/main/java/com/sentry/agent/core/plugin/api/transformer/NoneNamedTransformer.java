package com.sentry.agent.core.plugin.api.transformer;

import java.security.ProtectionDomain;

public interface NoneNamedTransformer extends SentryTransformer {

    Object classNameMatch(String className);

    byte[] noneNamedTransform(ClassLoader         loader,
                              String              className,
                              Class<?>            classBeingRedefined,
                              ProtectionDomain protectionDomain,
                              byte[]              classfileBuffer,
                              Object matchResult) throws Exception;
}
