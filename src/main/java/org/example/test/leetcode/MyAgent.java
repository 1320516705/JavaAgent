package org.example.test.leetcode;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyAgent {

    //JVM 首先尝试在代理类上调用以下方法
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("agentArgs:" + agentArgs);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable(){
            public void run(){
                JvmStack.printMemoryInfo();
                JvmStack.printGCInfo();
                System.out.println("=================");
            }
        },0,3000, TimeUnit.MILLISECONDS);
    }
}