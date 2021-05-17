package com.sentry.agent.core.plugin.asm.http;

import com.sentry.agent.core.log.LogException;
import com.sentry.agent.core.log.LoggerFactory;
import com.sentry.agent.core.plugin.api.transformer.NamedTransformer;
import com.sentry.agent.core.plugin.asm.AdviceWeaver;
import com.sentry.agent.core.plugin.asm.AsmClassWriter;
import com.sentry.agent.core.plugin.asm.api.AdviceListener;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class HttpClientPoolTransformer implements NamedTransformer {

    private final Logger logger = LoggerFactory.getLogger(HttpClientPoolTransformer.class);

    Set<String> names = new HashSet<String>(){{
        add("org/apache/http/impl/conn/PoolingHttpClientConnectionManager");
    }};

    @Override
    public Set<String> getNames() {
        return names;
    }

    @Override
    public byte[] namedTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws Exception {
        logger.info("try to resolve PoolingHttpClientConnectionManager..., loader = " + loader);
        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new AsmClassWriter(loader, cr, COMPUTE_MAXS | COMPUTE_FRAMES);
            cr.accept(new AdviceWeaver(className, cw, new AdviceListener() {
                @Override
                public void before(ClassLoader loader, String className, String methodName, String methodDesc, Object target, Object[] args) throws Throwable {
                    HttpClientPoolAggregator.register(target);
                }
            }, target -> "<init>".equals(target.name)), ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } catch (Throwable t) {
            logger.severe("failed to resolve PoolingHttpClientConnectionManager... exception is " + LogException.toString(t));
            return classfileBuffer;
        }
    }
}
