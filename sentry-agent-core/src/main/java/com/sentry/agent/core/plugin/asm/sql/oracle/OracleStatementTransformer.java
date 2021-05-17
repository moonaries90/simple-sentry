package com.sentry.agent.core.plugin.asm.sql.oracle;

import com.sentry.agent.core.log.LogException;
import com.sentry.agent.core.log.LoggerFactory;
import com.sentry.agent.core.plugin.api.transformer.NamedTransformer;
import com.sentry.agent.core.plugin.asm.AdviceWeaver;
import com.sentry.agent.core.plugin.asm.AsmClassWriter;
import com.sentry.agent.core.plugin.asm.sql.StatementMatcher;
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

public class OracleStatementTransformer implements NamedTransformer {

    private final Logger logger = LoggerFactory.getLogger("oracleTransformer");

    private static final Set<String> names = new HashSet<String>(){{
        add("oracle/jdbc/driver/OracleStatement");
    }};

    @Override
    public Set<String> getNames() {
        return names;
    }

    @Override
    public boolean canReTransform(Class<?> clazz) {
        return getNames().contains(getInnerName(clazz));
    }

    @Override
    public byte[] namedTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws Exception {
        logger.warning("try to resolve OracleStatement... loader = " + loader);
        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new AsmClassWriter(loader, cr, COMPUTE_MAXS | COMPUTE_FRAMES);
            cr.accept(new ClassVisitor(ASM5, new AdviceWeaver(className, cw, new OracleAdviceListener(), new StatementMatcher())) {
                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    if("connection".equals(name) || "url".equals(name)) {
                        return super.visitField(ACC_PUBLIC, name, descriptor, signature, value);
                    }
                    return super.visitField(access, name, descriptor, signature, value);
                }
            }, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } catch (Throwable t) {
            logger.severe("failed to transform OracleStatement...\n" + LogException.toString(t));
            return classfileBuffer;
        }
    }
}
