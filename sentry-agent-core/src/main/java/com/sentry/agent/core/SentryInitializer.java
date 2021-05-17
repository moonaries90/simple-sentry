package com.sentry.agent.core;

import com.sentry.agent.core.config.ConfigManager;
import com.sentry.agent.core.log.LogException;
import com.sentry.agent.core.log.LoggerFactory;
import com.sentry.agent.core.plugin.api.transformer.TransformerInitializer;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class SentryInitializer {

    private static final List<Class<?>> appendClassesToClassLoader = new ArrayList<>();

    public static Instrumentation inst;

    public static void appendToClassLoader(Class<?> clazz) {
        appendClassesToClassLoader.add(clazz);
    }

    public static void init() {
        LoggerFactory.init(ConfigManager.getAppName());
        initInst(ByteBuddyAgent.install());
    }

    private static void initInst(Instrumentation inst) {
        SentryInitializer.inst = inst;
        Logger logger = LoggerFactory.getLogger(SentryInitializer.class);
        try {
            if (appendClassesToClassLoader.size() > 0) {
                for (Class<?> clazz : appendClassesToClassLoader) {
                    appendToClassPath(clazz, inst);
                }
            }
            inst.addTransformer(new TransformerInitializer(), true);
        } catch (Throwable t) {
            logger.severe("initInst failed, exception is " + t.getMessage() + "\n" + LogException.toString(t));
        }
    }

    private static void appendToClassPath(Class<?> clazz, Instrumentation inst) throws Throwable {
        String agentJar = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        JarFile jarFile = new JarFile(agentJar);
        inst.appendToSystemClassLoaderSearch(jarFile);
    }
}
