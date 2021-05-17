package com.sentry.agent.core.util;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.ProtectionDomain;

public class Util {

    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static <T> boolean objectEqual(T s1, T s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }

    public static void writeToFile(String path, String content) throws IOException {
        FileOutputStream outSTr = new FileOutputStream(path);
        BufferedOutputStream buff = new BufferedOutputStream(outSTr);
        buff.write(content.getBytes(StandardCharsets.UTF_8));
        buff.flush();
        outSTr.close();
        buff.close();
    }

    public static String getHomePath() {
        String homePath = (String)System.getProperties().get("user.home");
        if (homePath == null) {
            homePath = "/tmp";
        }
        return homePath;
    }

    public static String getMD5String(String value) {
        if (value == null) {
            return null;
        } else {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("md5");
                messageDigest.update(value.getBytes(StandardCharsets.UTF_8));
                return getFormattedText(messageDigest.digest());
            } catch (Exception var2) {
                throw new RuntimeException(var2);
            }
        }
    }

    public static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);

        for (byte aByte : bytes) {
            buf.append(HEX_DIGITS[aByte >> 4 & 15]);
            buf.append(HEX_DIGITS[aByte & 15]);
        }

        return buf.toString();
    }

    public static String readDubboAbstractInvokerInterceptorCode() {
        return readClasspathFile("sentry.dubbo.AbstractInvoker.txt");
    }

    public static String readClasspathFile(String fileName) {
        BufferedReader br = null;

        try {
            StringBuilder sb = new StringBuilder();
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while(true) {
                String line = br.readLine();
                if (line == null) {
                    line = sb.toString();
                    return line;
                }
                sb.append(line).append("\n");
            }
        } catch (Exception var13) {
            throw new RuntimeException("failed to read file:" + fileName, var13);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignore) {
                }
            }

        }
    }

    private static String getlastFileName(String path) {
        int i = path.lastIndexOf("/");
        if (i < 0) {
            i = path.lastIndexOf("\\");
        }
        if (i > 0) {
            return path.substring(i + 1);
        } else {
            return path;
        }
    }

    private static String getJarFileName(String path) {
        if (!path.endsWith(".jar")) {
            int i = path.lastIndexOf(".jar");
            if (i > -1) {
                path = path.substring(0, i + 4);
            }
        }
        return getlastFileName(path);
    }

    public static String getJarVersionFromProtectionDomain(ProtectionDomain protectionDomain) {
        if (protectionDomain == null) {
            return "domainNull";
        } else {
            CodeSource cs = protectionDomain.getCodeSource();
            if (cs == null) {
                return "codeSourceNull";
            } else {
                URL url = cs.getLocation();
                if (url == null) {
                    return "urlNull";
                } else {
                    String jarpath = url.getPath();
                    if (jarpath == null) {
                        return "pathNull";
                    } else {
                        return getJarFileName(jarpath);
                    }
                }
            }
        }
    }
}
