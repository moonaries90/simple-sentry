package com.sentry.agent.core.plugin.asm.redis;

import com.sentry.agent.core.log.LogException;
import com.sentry.agent.core.log.LoggerFactory;
import com.sentry.agent.core.plugin.api.transformer.NamedTransformer;
import com.sentry.agent.core.plugin.asm.AdviceWeaver;
import com.sentry.agent.core.plugin.asm.AsmClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;

import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class JedisConnectionTransformer implements NamedTransformer {

    private final Logger logger = LoggerFactory.getLogger(JedisConnectionTransformer.class);

    private final Set<String> names = new HashSet<String>() {{
        add("redis/clients/jedis/Connection");
    }};

    @Override
    public Set<String> getNames() {
        return names;
    }

    @Override
    public byte[] namedTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws Exception {
        logger.warning("try to resolve JedisConnection... loader = " + loader);
        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new AsmClassWriter(loader, cr, COMPUTE_MAXS | COMPUTE_FRAMES);
            cr.accept(new ClassVisitor(ASM5,
                    new AdviceWeaver(className, cw,
                            new RedisConnectionAdviceListener(),
                            target -> "connect".equals(target.name) || "disconnect".equals(target.name))) {
                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    if("host".equals(name) || "port".equals(name)) {
                        return super.visitField(ACC_PUBLIC, name, descriptor, signature, value);
                    }
                    return super.visitField(access, name, descriptor, signature, value);
                }
            }, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } catch (Throwable t) {
            logger.severe("failed to resolve JedisConnection..., exception is " + LogException.toString(t));
            return classfileBuffer;
        }
    }
}
