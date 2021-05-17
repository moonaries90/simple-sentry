package com.sentry.agent.core.plugin.asm.sql.mysql;

import com.sentry.agent.core.config.MetricsNames;
import com.sentry.agent.core.log.LogException;
import com.sentry.agent.core.log.LoggerFactory;
import com.sentry.agent.core.plugin.api.binder.CommonInfoAggregator;
import com.sentry.agent.core.plugin.api.transformer.NamedTransformer;
import com.sentry.agent.core.plugin.asm.AsmClassWriter;
import com.sentry.agent.core.util.Util;
import org.objectweb.asm.*;

import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class MysqlConnectionTransformer implements NamedTransformer {

    private final Logger logger = LoggerFactory.getLogger(MysqlConnectionTransformer.class);

    Set<String> names = new HashSet<String>(){{
        add("com/mysql/cj/jdbc/ConnectionImpl");
        add("com/mysql/jdbc/ConnectionImpl");
    }};

    @Override
    public Set<String> getNames() {
        return names;
    }

    @Override
    public byte[] namedTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws Exception {
        String driverVersion = Util.getJarVersionFromProtectionDomain(protectionDomain);
        CommonInfoAggregator.instance.addInfo(MetricsNames.SQL, driverVersion);
        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new AsmClassWriter(loader, cr, COMPUTE_MAXS);
            cr.accept(
                    new ClassVisitor(ASM5, cw) {
                        @Override
                        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                            if ("origHostToConnectTo".equals(name) || "origPortToConnectTo".equals(name) || "database".equals(name)) {
                                return super.visitField(ACC_PUBLIC, name, descriptor, signature, value);
                            }
                            return super.visitField(access, name, descriptor, signature, value);
                        }
                    }, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } catch (Throwable t) {
            logger.severe("failed to transform oracleConnectionInfo...\n" + LogException.toString(t));
        }
        return classfileBuffer;
    }
}
