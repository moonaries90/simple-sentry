package com.sentry.agent.core.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.*;

public class LoggerFactory {

    private static final int FILE_COUNT = 3;

    private static final int FILE_SIZE = 52428800;

    private static FileHandler fileHandler;

    private static final Formatter formatter = new Formatter() {

        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder(1000);
            builder.append(sdf.format(new Date(record.getMillis()))).append(" - ");
            builder.append("[").append(record.getLevel()).append("] - ");
            builder.append("[").append(record.getLoggerName()).append("] - ");
            builder.append("").append(record.getSourceClassName()).append("#").append(record.getSourceMethodName()).append(" - ");
            builder.append(formatMessage(record));
            builder.append("\n");
            return builder.toString();
        }
    };

    private static final ConcurrentMap<String, Logger> loggerConcurrentMap = new ConcurrentHashMap<>();

    public static void init(String app) {
        String homePath = (String) System.getProperties().get("user.home");
        if (homePath == null) {
            homePath = "/tmp";
        }

        String folder = homePath + File.separator + "sentry";
        String filepath;
        if (app == null) {
            filepath = folder + File.separator + "error.log";
        } else {
            filepath = folder + File.separator + "" + app + ".log";
        }

        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdir();
        }

        try {
            fileHandler = new FileHandler(filepath, FILE_SIZE, FILE_COUNT);
            fileHandler.setFormatter(formatter);
        } catch (Exception var8) {
            throw new RuntimeException("failed to init fileHandler,filepath:" + filepath);
        }
    }

    public static Logger getLogger(String name) {
        return loggerConcurrentMap.computeIfAbsent(name, (n) -> {
            Logger logger = Logger.getLogger(n);
            logger.setUseParentHandlers(false);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
            return logger;
        });
    }

    public static Logger getLogger(Class<?> cls) {
        return getLogger(cls.getSimpleName());
    }
}
