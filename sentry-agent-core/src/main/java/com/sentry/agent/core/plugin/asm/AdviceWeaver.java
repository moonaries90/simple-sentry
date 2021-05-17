package com.sentry.agent.core.plugin.asm;

import com.sentry.agent.core.plugin.asm.api.AdviceId;
import com.sentry.agent.core.plugin.asm.api.AdviceListener;
import com.sentry.agent.core.plugin.asm.api.Matcher;
import com.sentry.agent.core.plugin.asm.stack.GaStack;
import com.sentry.agent.core.plugin.asm.stack.ThreadUnsafeFixGaStack;
import com.sentry.agent.core.plugin.asm.stack.ThreadUnsafeGaStack;
import com.sentry.agent.core.util.Ops;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知编织者<br/>
 * <p/>
 * <h2>线程帧栈与执行帧栈</h2>
 * 编织者在执行通知的时候有两个重要的栈:线程帧栈(threadFrameStack),执行帧栈(frameStack)
 * <p/>
 * Created by oldmanpushcart@gmail.com on 15/5/17.
 */
public class AdviceWeaver extends ClassVisitor implements Ops, AdviceId {

    // 线程帧栈堆栈大小
    private final static int FRAME_STACK_SIZE = 7;

    // 通知监听器集合
    private final static Map<Integer/*ADVICE_ID*/, AdviceListener> advices = new ConcurrentHashMap<>();

    // 线程帧封装
    private static final Map<Thread, GaStack<GaStack<Object>>> threadBoundContexts = new ConcurrentHashMap<>();

    // 防止自己递归调用
    private static final ThreadLocal<Boolean> isSelfCallRef = ThreadLocal.withInitial(() -> false);


    /**
     * 方法开始<br/>
     * 用于编织通知器,外部不会直接调用
     *
     * @param loader     类加载器
     * @param adviceId   通知ID
     * @param className  类名
     * @param methodName 方法名
     * @param methodDesc 方法描述
     * @param target     返回结果
     *                   若为无返回值方法(void),则为null
     * @param args       参数列表
     */
    public static void methodOnBegin(
            int adviceId,
            ClassLoader loader, String className, String methodName, String methodDesc,
            Object target, Object[] args) {

        if (!advices.containsKey(adviceId)) {
            return;
        }

        if (isSelfCallRef.get()) {
            return;
        } else {
            isSelfCallRef.set(true);
        }

        try {
            // 构建执行帧栈,保护当前的执行现场
            final GaStack<Object> frameStack = new ThreadUnsafeFixGaStack<>(FRAME_STACK_SIZE);
            frameStack.push(loader);
            frameStack.push(className);
            frameStack.push(methodName);
            frameStack.push(methodDesc);
            frameStack.push(target);
            frameStack.push(args);

            final AdviceListener listener = getListener(adviceId);
            frameStack.push(listener);

            // 获取通知器并做前置通知
            before(listener, loader, className, methodName, methodDesc, target, args);

            // 保护当前执行帧栈,压入线程帧栈
            threadFrameStackPush(frameStack);
        } finally {
            isSelfCallRef.set(false);
        }

    }


    /**
     * 方法以返回结束<br/>
     * 用于编织通知器,外部不会直接调用
     *
     * @param returnObject 返回对象
     *                     若目标为静态方法,则为null
     */
    public static void methodOnReturnEnd(Object returnObject, int adviceId) {
        methodOnEnd(adviceId, false, returnObject);
    }

    /**
     * 方法以抛异常结束<br/>
     * 用于编织通知器,外部不会直接调用
     *
     * @param throwable 抛出异常
     */
    public static void methodOnThrowingEnd(Throwable throwable, int adviceId) {
        methodOnEnd(adviceId, true, throwable);
    }

    /**
     * 所有的返回都统一处理
     *
     * @param isThrowing        标记正常返回结束还是抛出异常结束
     * @param returnOrThrowable 正常返回或者抛出异常对象
     */
    private static void methodOnEnd(int adviceId, boolean isThrowing, Object returnOrThrowable) {

        if (!advices.containsKey(adviceId)) {
            return;
        }

        if (isSelfCallRef.get()) {
            return;
        } else {
            isSelfCallRef.set(true);
        }

        try {
            // 弹射线程帧栈,恢复Begin所保护的执行帧栈
            final GaStack<Object> frameStack = threadFrameStackPop();

            // 用于保护reg和before执行并发的情况
            // 如果before没有注入,则不对end做任何处理
            if (null == frameStack) {
                return;
            }

            // 弹射执行帧栈,恢复Begin所保护的现场
            final AdviceListener listener = (AdviceListener) frameStack.pop();
            final Object[] args = (Object[]) frameStack.pop();
            final Object target = frameStack.pop();
            final String methodDesc = (String) frameStack.pop();
            final String methodName = (String) frameStack.pop();
            final String className = (String) frameStack.pop();
            final ClassLoader loader = (ClassLoader) frameStack.pop();

            // 异常通知
            if (isThrowing) {
                afterThrowing(listener, loader, className, methodName, methodDesc, target, args, (Throwable) returnOrThrowable);
            }

            // 返回通知
            else {
                afterReturning(listener, loader, className, methodName, methodDesc, target, args, returnOrThrowable);
            }
        } finally {
            isSelfCallRef.set(false);
        }

    }


    /*
     * 线程帧栈压栈<br/>
     * 将当前执行帧栈压入线程栈
     */
    private static void threadFrameStackPush(GaStack<Object> frameStack) {
        final Thread thread = Thread.currentThread();
        GaStack<GaStack<Object>> threadFrameStack = threadBoundContexts.get(thread);
        if (null == threadFrameStack) {
            threadBoundContexts.put(thread, threadFrameStack = new ThreadUnsafeGaStack<>());
        }
        threadFrameStack.push(frameStack);
    }

    private static GaStack<Object> threadFrameStackPop() {
        final GaStack<GaStack<Object>> stackGaStack = threadBoundContexts.get(Thread.currentThread());

        // 用于保护reg和before并发导致before/end乱序的场景
        if (null == stackGaStack || stackGaStack.isEmpty()) {
            return null;
        }
        return stackGaStack.pop();
    }

    private static AdviceListener getListener(int adviceId) {
        return advices.get(adviceId);
    }

    private static void before(AdviceListener listener,
                               ClassLoader loader, String className, String methodName, String methodDesc,
                               Object target, Object[] args) {

        if (null != listener) {
            try {
                listener.before(loader, className, methodName, methodDesc, target, args);
            } catch (Throwable ignore) {
            }
        }

    }

    private static void afterReturning(AdviceListener listener,
                                       ClassLoader loader, String className, String methodName, String methodDesc,
                                       Object target, Object[] args, Object returnObject) {
        if (null != listener) {
            try {
                listener.afterReturning(loader, className, methodName, methodDesc, target, args, returnObject);
            } catch (Throwable ignore) {
            }
        }
    }

    private static void afterThrowing(AdviceListener listener,
                                      ClassLoader loader, String className, String methodName, String methodDesc,
                                      Object target, Object[] args, Throwable throwable) {
        if (null != listener) {
            try {
                listener.afterThrowing(loader, className, methodName, methodDesc, target, args, throwable);
            } catch (Throwable ignore) {
            }
        }
    }

    private final int adviceId;

    private final String javaClassName;

    private final Matcher<AsmMethod> matcher;

    /**
     * 构建通知编织器
     *
     * @param internalClassName 类名称(透传)
     * @param cv                ClassVisitor for ASM
     */
    public AdviceWeaver(
            final String internalClassName,
            final ClassVisitor cv,
            final AdviceListener listener,
            final Matcher<AsmMethod> matcher) {
        super(ASM5, cv);
        this.adviceId = nextId();
        advices.put(adviceId, listener);
        this.javaClassName = getClassName(internalClassName);
        this.matcher = matcher;
    }

    /**
     * 是否需要忽略
     */
    private boolean isIgnore(MethodVisitor mv, int access, String name, String desc) {
        return null == mv
                || isAbstract(access)
                || isAnnotation(access)
                || isEnum(access)
                || isInterface(access)
                || isBridge(access)
                || "<clinit>".equals(name)
                || !this.matcher.match(new AsmMethod(access, name, desc));
    }

    @Override
    public MethodVisitor visitMethod(
            final int access,
            final String name,
            final String desc,
            final String signature,
            final String[] exceptions) {

        final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (isIgnore(mv, access, name, desc)) {
            return mv;
        }
        return new AdviceAdapter(ASM5, new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions), access, name, desc) {

            // -- Lebel for try...catch block
            private final Label beginLabel = new Label();
            private final Label endLabel = new Label();

            // -- KEY of advice --
            private final int KEY_GREYS_ADVICE_BEFORE_METHOD = 0;
            private final int KEY_GREYS_ADVICE_RETURN_METHOD = 1;
            private final int KEY_GREYS_ADVICE_THROWS_METHOD = 2;

            // -- KEY of ASM_TYPE or ASM_METHOD --
            private final Type ASM_TYPE_SPY = Type.getType("Lcom/sentry/agent/core/plugin/Spy;");
            private final Type ASM_TYPE_OBJECT = Type.getType(Object.class);
            private final Type ASM_TYPE_OBJECT_ARRAY = Type.getType(Object[].class);
            private final Type ASM_TYPE_CLASS = Type.getType(Class.class);
            private final Type ASM_TYPE_INTEGER = Type.getType(Integer.class);
            private final Type ASM_TYPE_CLASS_LOADER = Type.getType(ClassLoader.class);
            private final Type ASM_TYPE_STRING = Type.getType(String.class);
            private final Type ASM_TYPE_THROWABLE = Type.getType(Throwable.class);
            private final Type ASM_TYPE_INT = Type.getType(int.class);
            private final Type ASM_TYPE_METHOD = Type.getType(java.lang.reflect.Method.class);
            private final Method ASM_METHOD_METHOD_INVOKE = Method.getMethod("Object invoke(Object,Object[])");

            /**
             * 加载通知方法
             * @param keyOfMethod 通知方法KEY
             */
            private void loadAdviceMethod(int keyOfMethod) {
                switch (keyOfMethod) {
                    case KEY_GREYS_ADVICE_BEFORE_METHOD:
                        getStatic(ASM_TYPE_SPY, "ON_BEFORE_METHOD", ASM_TYPE_METHOD);
                        break;
                    case KEY_GREYS_ADVICE_RETURN_METHOD:
                        getStatic(ASM_TYPE_SPY, "ON_RETURN_METHOD", ASM_TYPE_METHOD);
                        break;
                    case KEY_GREYS_ADVICE_THROWS_METHOD:
                        getStatic(ASM_TYPE_SPY, "ON_THROWS_METHOD", ASM_TYPE_METHOD);
                        break;
                    default:
                        throw new IllegalArgumentException("illegal keyOfMethod=" + keyOfMethod);
                }
            }

            /**
             * 加载ClassLoader<br/>
             * 这里分开静态方法中ClassLoader的获取以及普通方法中ClassLoader的获取
             * 主要是性能上的考虑
             */
            private void loadClassLoader() {
                if (this.isStaticMethod()) {
                    // 这里不得不用性能极差的Class.forName()来完成类的获取,因为有可能当前这个静态方法在执行的时候
                    // 当前类并没有完成实例化,会引起JVM对class文件的合法性校验失败
                    // 未来我可能会在这一块考虑性能优化,但对于当前而言,功能远远重要于性能,也就不打算折腾这么复杂了
                    visitLdcInsn(javaClassName);
                    invokeStatic(ASM_TYPE_CLASS, Method.getMethod("Class forName(String)"));
                } else {
                    loadThis();
                    invokeVirtual(ASM_TYPE_OBJECT, Method.getMethod("Class getClass()"));
                }
                invokeVirtual(ASM_TYPE_CLASS, Method.getMethod("ClassLoader getClassLoader()"));
            }

            /**
             * 加载before通知参数数组
             */
            private void loadArrayForBefore() {
                push(7);
                newArray(ASM_TYPE_OBJECT);

                dup();
                push(0);
                push(adviceId);
                box(ASM_TYPE_INT);
                arrayStore(ASM_TYPE_INTEGER);

                dup();
                push(1);
                loadClassLoader();
                arrayStore(ASM_TYPE_CLASS_LOADER);

                dup();
                push(2);
                push(getClassName(javaClassName));
                arrayStore(ASM_TYPE_STRING);

                dup();
                push(3);
                push(name);
                arrayStore(ASM_TYPE_STRING);

                dup();
                push(4);
                push(desc);
                arrayStore(ASM_TYPE_STRING);

                dup();
                push(5);
                loadThisOrPushNullIfIsStatic();
                arrayStore(ASM_TYPE_OBJECT);

                dup();
                push(6);
                loadArgArray();
                arrayStore(ASM_TYPE_OBJECT_ARRAY);
            }

            @Override
            protected void onMethodEnter() {
                // 加载before方法
                loadAdviceMethod(KEY_GREYS_ADVICE_BEFORE_METHOD);
                // 推入Method.invoke()的第一个参数
                pushNull();
                // 方法参数
                loadArrayForBefore();
                // 调用方法
                invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
                pop();
                mark(beginLabel);
            }

            /*
             * 加载return通知参数数组
             */
            private void loadReturnArgs() {
                dup2X1();
                pop2();
                push(2);
                newArray(ASM_TYPE_OBJECT);
                dup();
                dup2X1();
                pop2();
                push(0);
                swap();
                arrayStore(ASM_TYPE_OBJECT);

                dup();
                push(1);
                push(adviceId);
                box(ASM_TYPE_INT);
                arrayStore(ASM_TYPE_INTEGER);
            }

            @Override
            protected void onMethodExit(final int opcode) {
                if (!isThrow(opcode)) {
                    // 加载返回对象
                    loadReturn(opcode);
                    // 加载returning方法
                    loadAdviceMethod(KEY_GREYS_ADVICE_RETURN_METHOD);
                    // 推入Method.invoke()的第一个参数
                    pushNull();
                    // 加载return通知参数数组
                    loadReturnArgs();
                    invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
                    pop();
                }
            }

            /*
             * 创建throwing通知参数本地变量
             */
            private void loadThrowArgs() {
                dup2X1();
                pop2();
                push(2);
                newArray(ASM_TYPE_OBJECT);
                dup();
                dup2X1();
                pop2();
                push(0);
                swap();
                arrayStore(ASM_TYPE_THROWABLE);

                dup();
                push(1);
                push(adviceId);
                box(ASM_TYPE_INT);
                arrayStore(ASM_TYPE_INTEGER);
            }

            @Override
            public void visitMaxs(int maxStack, int maxLocals) {
                mark(endLabel);
                visitTryCatchBlock(beginLabel, endLabel, mark(), ASM_TYPE_THROWABLE.getInternalName());
                // 加载异常
                loadThrow();
                // 加载throwing方法
                loadAdviceMethod(KEY_GREYS_ADVICE_THROWS_METHOD);
                // 推入Method.invoke()的第一个参数
                pushNull();
                // 加载throw通知参数数组
                loadThrowArgs();
                // 调用方法
                invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
                pop();
                throwException();
                super.visitMaxs(maxStack, maxLocals);
            }

            /**
             * 是否静态方法
             * @return true:静态方法 / false:非静态方法
             */
            private boolean isStaticMethod() {
                return (methodAccess & ACC_STATIC) != 0;
            }

            /**
             * 是否抛出异常返回(通过字节码判断)
             * @param opcode 操作码
             * @return true:以抛异常形式返回 / false:非抛异常形式返回(return)
             */
            private boolean isThrow(int opcode) {
                return opcode == ATHROW;
            }

            /**
             * 将NULL推入堆栈
             */
            private void pushNull() {
                push((Type) null);
            }

            /**
             * 加载this/null
             */
            private void loadThisOrPushNullIfIsStatic() {
                if (isStaticMethod()) {
                    pushNull();
                } else {
                    loadThis();
                }
            }

            /**
             * 加载返回值
             * @param opcode 操作吗
             */
            private void loadReturn(int opcode) {
                switch (opcode) {
                    case RETURN: {
                        pushNull();
                        break;
                    }
                    case ARETURN: {
                        dup();
                        break;
                    }
                    case LRETURN:
                    case DRETURN: {
                        dup2();
                        box(Type.getReturnType(methodDesc));
                        break;
                    }
                    default: {
                        dup();
                        box(Type.getReturnType(methodDesc));
                        break;
                    }
                }
            }

            /**
             * 加载异常
             */
            private void loadThrow() {
                dup();
            }

            // 用于try-catch的冲排序,目的是让tracing的try...catch能在exceptions tables排在前边
            private final Collection<AsmTryCatchBlock> asmTryCatchBlocks = new ArrayList<>();

            @Override
            public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                asmTryCatchBlocks.add(new AsmTryCatchBlock(start, end, handler, type));
            }

            @Override
            public void visitEnd() {
                for (AsmTryCatchBlock tcb : asmTryCatchBlocks) {
                    super.visitTryCatchBlock(tcb.start, tcb.end, tcb.handler, tcb.type);
                }
                super.visitEnd();
            }
        };
    }
}