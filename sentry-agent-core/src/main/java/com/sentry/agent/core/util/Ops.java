package com.sentry.agent.core.util;

import org.objectweb.asm.Opcodes;

public interface Ops extends Opcodes {

    default String getInnerName(Class<?> clazz) {
        return clazz.getName().replace(".", "/");
    }

    default String getClassName(String clazzName) {
        return clazzName != null ? clazzName.replace("/", ".") : null;
    }

    default boolean isInterface(int access) {
        return (access & ACC_INTERFACE) != 0;
    }

    default boolean isEnum(int access) {
        return (access & ACC_ENUM) != 0;
    }

    default boolean isAbstract(int access) {
        return (access & ACC_ABSTRACT) != 0;
    }

    default boolean isNative(int access) {
        return (access & ACC_NATIVE) != 0;
    }

    default boolean isStatic(int access) {
        return (access & ACC_STATIC) != 0;
    }

    default boolean isPublic(int access) {
        return (access & ACC_PUBLIC) != 0;
    }

    default boolean isAnnotation(int access) {
        return (access & ACC_ANNOTATION) != 0;
    }

    default boolean isBridge(int access) {
        return (access & ACC_BRIDGE) != 0;
    }
}
