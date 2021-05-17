package com.sentry.agent.core.plugin.api.transformer;

import java.security.ProtectionDomain;
import java.util.Set;

public interface NamedTransformer extends SentryTransformer {

    Set<String> getNames();

    byte[] namedTransform(ClassLoader         loader,
                          String              className,
                          Class<?>            classBeingRedefined,
                          ProtectionDomain protectionDomain,
                          byte[]              classfileBuffer) throws Exception;
}
