package com.sentry.agent.core.plugin.asm.method;

import com.sentry.agent.core.config.ConfigManager;
import com.sentry.agent.core.config.JavaMethodPattern;
import com.sentry.agent.core.config.JavaMethodPatternItem;
import com.sentry.agent.core.log.LogException;
import com.sentry.agent.core.log.LoggerFactory;
import com.sentry.agent.core.plugin.api.transformer.NoneNamedTransformer;
import com.sentry.agent.core.plugin.asm.AdviceWeaver;
import com.sentry.agent.core.plugin.asm.AsmClassWriter;
import com.sentry.agent.core.plugin.asm.api.AdviceId;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.security.ProtectionDomain;
import java.util.logging.Logger;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class MethodTransformer implements NoneNamedTransformer, AdviceId {

    private final JavaMethodPattern pattern = ConfigManager.getJavaMethodPattern();

    private static final Logger logger = LoggerFactory.getLogger(MethodTransformer.class);

    @Override
    public boolean canReTransform(Class<?> clazz) {
        return classNameMatch(clazz.getName()) != null;
    }

    @Override
    public Object classNameMatch(String className) {
        className = className.replaceAll("/", ".");
        if (!className.startsWith("sun.") && !className.startsWith("java.")) {
            if (className.startsWith("com.sentry.agent")) {
                return null;
            } else if (this.pattern == null) {
                return null;
            } else if (className.contains("$")) {
                return null;
            } else {
                return this.pattern.classNameMatch(className);
            }
        } else {
            return null;
        }
    }

    @Override
    public byte[] noneNamedTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer, Object matchResult) throws Exception {
        logger.warning("start to transform class " + className + ", loader = " + loader);
        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            JavaMethodPatternItem item = (JavaMethodPatternItem) matchResult;
            if (!isInterface(cr.getAccess()) && !isEnum(cr.getAccess()) && !isAbstract(cr.getAccess()) && !isAnnotation(cr.getAccess())) {
                ClassWriter cw = new AsmClassWriter(loader, cr, COMPUTE_MAXS | COMPUTE_FRAMES);
                cr.accept(new AdviceWeaver(className, cw, new MethodAdviceListener(item), new MethodMatcher(item)), ClassReader.EXPAND_FRAMES);
                return cw.toByteArray();
            }
        } catch (Throwable t) {
            logger.severe("transform class " + className + " failed...\n" + LogException.toString(t));
        }
        return classfileBuffer;
    }
}
