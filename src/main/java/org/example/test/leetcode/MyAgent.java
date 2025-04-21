package org.example.test.leetcode;

import java.lang.instrument.Instrumentation;

public class MyAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("嗨，JavaAgent"+agentArgs);
        MyMonitorTransformer transformer = new MyMonitorTransformer();
        inst.addTransformer(transformer);
    }
    public static void premain(String agentArgs) {
    }
}