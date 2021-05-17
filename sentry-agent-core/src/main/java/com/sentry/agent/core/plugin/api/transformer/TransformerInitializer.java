package com.sentry.agent.core.plugin.api.transformer;

import com.sentry.agent.core.plugin.asm.AdviceWeaver;
import com.sentry.agent.core.plugin.asm.http.ResponseCloseTransformer;
import com.sentry.agent.core.plugin.asm.http.HttpClientTransformer;
import com.sentry.agent.core.plugin.asm.http.HttpClientPoolTransformer;
import com.sentry.agent.core.plugin.asm.method.MethodTransformer;
import com.sentry.agent.core.plugin.asm.redis.JedisConnectionTransformer;
import com.sentry.agent.core.plugin.asm.redis.JedisTransformer;
import com.sentry.agent.core.plugin.asm.sql.mysql.MysqlConnectionTransformer;
import com.sentry.agent.core.plugin.asm.sql.mysql.MysqlPreparedStatementTransformer;
import com.sentry.agent.core.plugin.asm.sql.mysql.MysqlStatementTransformer;
import com.sentry.agent.core.plugin.asm.sql.oracle.OracleConnectionInfoTransformer;
import com.sentry.agent.core.plugin.asm.sql.oracle.OraclePreparedStatementTransformer;
import com.sentry.agent.core.plugin.asm.sql.oracle.OracleStatementTransformer;
import com.sentry.agent.core.config.ConfigManager;
import com.sentry.agent.core.plugin.Spy;
import com.sentry.agent.core.config.props.SentryAgentConfig;
import com.sentry.agent.core.config.resolver.ConfigParser;
import com.sentry.agent.core.config.resolver.ConfigResolver;
import com.sentry.agent.core.config.resolver.DefaultConfigResolver;
import com.sentry.agent.core.config.resolver.XmlConfigParser;
import com.sentry.agent.core.log.LogException;
import com.sentry.agent.core.log.LoggerFactory;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class TransformerInitializer implements ClassFileTransformer {

    private final Logger logger = LoggerFactory.getLogger(TransformerInitializer.class);

    private static volatile boolean inited = false;

    private static ConfigParser configParser;

    private static ConfigResolver configResolver;

    private static String location;

    private static final Map<String, NamedTransformer> namedTransformerMap = new ConcurrentHashMap<>();

    private static final List<NoneNamedTransformer> noneNamedTransformers = new ArrayList<>();

    private static final ConcurrentMap<ClassLoader, ConcurrentMap<String, Boolean>> loaderMap = new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, Boolean> transformedMap = new ConcurrentHashMap<>();

    {
        prepareIfNeed();
    }

    private void prepareIfNeed() {
        try {
            prepareSpy();
            if (!inited) {
                inited = true;
                if (configParser == null) {
                    configParser = new XmlConfigParser();
                }
                if (configResolver == null) {
                    String _location = location == null ? System.getProperty("sentry.agent.location") : location;
                    if (_location == null) {
                        System.out.println("config location not prepared... return...");
                    }
                    configResolver = new DefaultConfigResolver(_location);
                }

                ConfigManager.init(configParser.parse(configResolver));
                SentryAgentConfig config = ConfigManager.getAgentConfig();
                // 用于加载未经加载过的类
                if(config.getEnabled().isJavaMethod()) {
                    registerTransformer(new MethodTransformer());
                }
                if(config.getEnabled().isSql()) {
                    registerTransformer(new OracleConnectionInfoTransformer());
                    registerTransformer(new OraclePreparedStatementTransformer());
                    registerTransformer(new OracleStatementTransformer());
                    registerTransformer(new MysqlConnectionTransformer());
                    registerTransformer(new MysqlPreparedStatementTransformer());
                    registerTransformer(new MysqlStatementTransformer());
                }
                if(config.getEnabled().isHttpClient()) {
                    registerTransformer(new ResponseCloseTransformer());
                    registerTransformer(new HttpClientTransformer());
                    registerTransformer(new HttpClientPoolTransformer());
                }
                if(config.getEnabled().isRedis()) {
                    registerTransformer(new JedisTransformer());
                    registerTransformer(new JedisConnectionTransformer());
                }
            }
        } catch (Exception e) {
            logger.severe("transformer init failed...\n" + LogException.toString(e));
        }
    }

    private void prepareSpy() throws NoSuchMethodException {
        Spy.init(
                AdviceWeaver.class.getMethod("methodOnBegin",
                        int.class,
                        ClassLoader.class,
                        String.class,
                        String.class,
                        String.class,
                        Object.class,
                        Object[].class),
                AdviceWeaver.class.getMethod("methodOnReturnEnd",
                        Object.class,
                        int.class),
                AdviceWeaver.class.getMethod("methodOnThrowingEnd",
                        Throwable.class,
                        int.class)
        );
    }

    public static void reTransformIfNeeded() {
        // 获取已经加载过的类，并判断是否需要重新加载
        Instrumentation inst = ByteBuddyAgent.getInstrumentation();
        Class<?>[] classes = inst.getAllLoadedClasses();
        Set<Class<?>> forReTransformClasses = new HashSet<>();
        for (Class<?> clazz : classes) {
            for (NamedTransformer transformer : namedTransformerMap.values()) {
                if (transformer.canReTransform(clazz)) {
                    forReTransformClasses.add(clazz);
                    break;
                }
            }

            for (NoneNamedTransformer transformer : noneNamedTransformers) {
                if (transformer.canReTransform(clazz)) {
                    forReTransformClasses.add(clazz);
                    break;
                }
            }
        }
        if (forReTransformClasses.size() > 0) {
            Logger l = LoggerFactory.getLogger(TransformerInitializer.class);
            StringBuilder s = new StringBuilder();
            for(Class<?> cls : forReTransformClasses) {
                s.append(cls.getName()).append("\n");
            }
            l.info("need reTransform classes：\n" + s);
            try {
                inst.retransformClasses(forReTransformClasses.toArray(new Class<?>[0]));
            } catch (Throwable t) {
                l.severe("reTransform class failed... \n" + LogException.toString(t));
            }
        }
    }

    public static void registerTransformer(SentryTransformer transformer) {
        if(transformer instanceof NamedTransformer) {
            NamedTransformer namedTransformer = (NamedTransformer) transformer;
            namedTransformer.getNames().forEach(name -> namedTransformerMap.put(name, namedTransformer));
        } else if(transformer instanceof NoneNamedTransformer) {
            NoneNamedTransformer noneNamedTransformer = (NoneNamedTransformer) transformer;
            noneNamedTransformers.add(noneNamedTransformer);
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if(className == null) {
            return classfileBuffer;
        }
        try {
            ConcurrentMap<String, Boolean> transformedMap;
            NamedTransformer namedTransformer = namedTransformerMap.get(className);
            if (namedTransformer != null) {
                transformedMap = getObtainedMapByLoader(loader, className);
                if(transformedMap.putIfAbsent(className, true) == null) {
                    return namedTransformer.namedTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                }
            }
            for (NoneNamedTransformer noneNamedTransformer : noneNamedTransformers) {
                Object match = noneNamedTransformer.classNameMatch(className);
                if (match != null) {
                    transformedMap = getObtainedMapByLoader(loader, className);
                    if(transformedMap.putIfAbsent(className, true) == null) {
                        return noneNamedTransformer.noneNamedTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer, match);
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("failed to transform " + className + ", exception is " + e.getMessage() + System.lineSeparator() + LogException.toString(e));
        }
        return classfileBuffer;
    }

    public static void setConfigParser(ConfigParser configParser) {
        TransformerInitializer.configParser = configParser;
    }

    public static void setLocation(String location) {
        TransformerInitializer.location = location;
    }

    private ConcurrentMap<String, Boolean> getObtainedMapByLoader(ClassLoader loader, String className) {
        try {
            if(loader == null) {
                return transformedMap;
            }
            if(loader.getClass().getName().equals("sun.reflect.DelegatingClassLoader")) {
                return getObtainedMapByLoader(loader.getParent(), className);
            }
            // 放给所有的子类
            setToChildren(loader, className);
            // 自己没有加载过， 查询父类有没有
            return loaderMap.computeIfAbsent(loader, (l) -> {
                for(Map.Entry<ClassLoader, ConcurrentMap<String, Boolean>> entry : loaderMap.entrySet()) {
                    if(isParent(entry.getKey(), loader)) {
                        return new ConcurrentHashMap<>(entry.getValue());
                    }
                }
                return new ConcurrentHashMap<>();
            });
        } catch (Exception e) {
            return transformedMap;
        }
    }

    private void setToChildren(ClassLoader loader, String className) {
        loaderMap.forEach((k, v) -> {
            if(isParent(loader, k)) {
                v.putIfAbsent(className, true);
            }
        });
    }

    private boolean isParent(ClassLoader parent, ClassLoader child) {
        if(child.getParent() != null) {
            if(parent == child.getParent()) {
                return true;
            } else {
                return isParent(parent, child.getParent());
            }
        }
        return false;
    }
}
