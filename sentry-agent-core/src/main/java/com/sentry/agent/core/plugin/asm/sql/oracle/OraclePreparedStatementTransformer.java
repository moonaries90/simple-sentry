package com.sentry.agent.core.plugin.asm.sql.oracle;

import com.sentry.agent.core.log.LogException;
import com.sentry.agent.core.log.LoggerFactory;
import com.sentry.agent.core.plugin.api.transformer.NamedTransformer;
import com.sentry.agent.core.plugin.asm.AdviceWeaver;
import com.sentry.agent.core.plugin.asm.AsmClassWriter;
import com.sentry.agent.core.plugin.asm.sql.PreparedStatementMatcher;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class OraclePreparedStatementTransformer implements NamedTransformer {

    private final Logger logger = LoggerFactory.getLogger("oracleTransformer");

    private static final Set<String> names = new HashSet<String>(){{
        add("oracle/jdbc/driver/OraclePreparedStatement");
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
        logger.warning("try to resolve OraclePreparedStatement... loader = " + loader);
        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new AsmClassWriter(loader, cr, COMPUTE_MAXS | COMPUTE_FRAMES);
            cr.accept(new AdviceWeaver(className, cw, new OracleAdviceListener(), new PreparedStatementMatcher()), ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } catch (Throwable t) {
            logger.severe("failed to transform OraclePrepareStatement...\n" + LogException.toString(t));
            return classfileBuffer;
        }
    }
}
