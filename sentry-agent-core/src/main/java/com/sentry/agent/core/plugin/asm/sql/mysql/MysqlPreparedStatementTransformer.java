package com.sentry.agent.core.plugin.asm.sql.mysql;

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

public class MysqlPreparedStatementTransformer implements NamedTransformer {

    private final Logger logger = LoggerFactory.getLogger(MysqlPreparedStatementTransformer.class);

    Set<String> names = new HashSet<String>() {{
        add("com/mysql/jdbc/PreparedStatement");
        add("com/mysql/cj/jdbc/ClientPreparedStatement");
    }};

    @Override
    public Set<String> getNames() {
        return names;
    }

    @Override
    public byte[] namedTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws Exception {
        logger.info("try to resolve PreparedStatement transformer, loader = " + loader);
        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new AsmClassWriter(loader, cr, COMPUTE_MAXS | COMPUTE_FRAMES);
            cr.accept(new AdviceWeaver(className, cw, new MysqlAdviceListener(), new PreparedStatementMatcher()), ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } catch (Throwable t) {
            logger.severe("failed to resolve for MysqlPreparedStatement, exception is " + t.getMessage() + "," + LogException.toString(t));
            return classfileBuffer;
        }
    }
}
