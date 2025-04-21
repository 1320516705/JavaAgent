package org.example.test.leetcode;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

public class MyMonitorTransformer implements ClassFileTransformer {
    private static final Set<String> classNameSet = new HashSet<>();

    static {
        classNameSet.add("org.example.test.test.ApiTest");
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String currentClassName = className.replaceAll("/", ".");
        if (!classNameSet.contains(currentClassName)) {
            return null;
        }
        try {
            System.out.println("transform:" + currentClassName);
            CtClass ctClass = ClassPool.getDefault().get(currentClassName);
            CtBehavior[] behaviors = ctClass.getDeclaredBehaviors();
            for (CtBehavior behavior : behaviors) {
                enhanceMethod(behavior);
            }
            return ctClass.toBytecode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void enhanceMethod(CtBehavior method) throws Exception {
        if (method == null) {
            return;
        }
        String methodName = method.getName();
        if ("main".equalsIgnoreCase(methodName)) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{")
                .append("long start = System.nanoTime();\n") //前置增强: 打入时间戳
                .append("$_ = $proceed($$);\n")              //调用原有代码，类似于method();($$)表示所有的参数
                .append("System.out.print(\"method:[")
                .append(methodName).append("]\");").append("\n")
                .append("System.out.println(\" cost:[\" +(System.nanoTime() - start)+ \"ns]\");") // 后置增强，计算输出方法执行耗时
                .append("}");
//        {
//            long start = System.nanoTime();/n
//            System.out.println("method:[" + methodName + "]");/n
//            $_ = $process($$);/n
//            System.out.println("cost:[" + (System.nanoTime() - start) + "]");/n
//        }


        ExprEditor editor = new ExprEditor() {
            @Override
            public void edit(MethodCall methodCall) throws CannotCompileException {
                methodCall.replace(stringBuilder.toString());
            }
        };
        method.instrument(editor);
    }
}
