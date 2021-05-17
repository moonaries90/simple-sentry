package com.sentry.agent.core.plugin.asm;

import com.sentry.agent.core.log.LoggerFactory;
import com.sentry.agent.core.util.Ops;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.util.logging.Logger;

public class AsmClassWriter extends ClassWriter implements Ops {

    private ClassLoader classLoader = null;

    private final Logger logger = LoggerFactory.getLogger(AsmClassWriter.class);

    public AsmClassWriter(ClassReader cr, int flags) {
        super(cr, flags);
    }

    public AsmClassWriter(ClassLoader loader, ClassReader cr, int flags) {
        super(cr, flags);
        this.classLoader = loader;
    }

    protected ClassLoader getClassLoader() {
        return this.classLoader != null ? this.classLoader : this.getClass().getClassLoader();
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        try {
            return super.getCommonSuperClass(type1, type2);
        } catch (Exception e) {
            logger.severe("getCommonSuperClass failed, type1 = " + type1 + System.lineSeparator() +
                    "type2 = " + type2 + System.lineSeparator() + "exception is " + e.getMessage());
            return "java/lang/Object";
        }
    }
}
