package com.sentry.agent.core.plugin.asm.http;

import com.sentry.agent.core.config.MetricsNames;
import com.sentry.agent.core.log.LogException;
import com.sentry.agent.core.log.LoggerFactory;
import com.sentry.agent.core.plugin.api.binder.CommonInfoAggregator;
import com.sentry.agent.core.plugin.api.transformer.NamedTransformer;
import com.sentry.agent.core.plugin.asm.AdviceWeaver;
import com.sentry.agent.core.plugin.asm.AsmClassWriter;
import com.sentry.agent.core.util.Util;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class HttpClientTransformer implements NamedTransformer {

    private final Logger logger = LoggerFactory.getLogger(HttpClientTransformer.class);

    Set<String> names = new HashSet<String>(){{
        add("org/apache/http/impl/client/InternalHttpClient");
        add("org/apache/http/impl/client/AbstractHttpClient");
    }};

    @Override
    public Set<String> getNames() {
        return names;
    }

    @Override
    public byte[] namedTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws Exception {
        logger.info("try to resolve " + className + "..., loader = " + loader);
        String version = Util.getJarVersionFromProtectionDomain(protectionDomain);
        CommonInfoAggregator.instance.addInfo(MetricsNames.HTTP, version);
        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new AsmClassWriter(loader, cr, COMPUTE_MAXS | COMPUTE_FRAMES);
            String methodName = className.contains("AbstractHttpClient") ? "execute" : "doExecute";
            cr.accept(new AdviceWeaver(className, cw, new HttpClientAdviceListener(), new HttpClientMatcher(methodName)), ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } catch (Throwable t) {
            logger.severe("failed to resolve " + className + "... exception is " + LogException.toString(t));
            return classfileBuffer;
        }
    }
}
