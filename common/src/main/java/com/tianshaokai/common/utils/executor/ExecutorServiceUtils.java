package com.tianshaokai.common.utils.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceUtils {

    //获取当前的cpu核心数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    //我们想在核心池中至少有2个线程，最多4个线程，更喜欢有1个小于CPU计数的CPU，以避免CPU背景饱和
    //核心线程数量大小
    private static final int corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    //线程池最大容纳线程数
    private static final int maximumPoolSize = CPU_COUNT * 2 + 1;

    private static ExecutorService singleExecutors = null;

    public static ExecutorService getSingleExecutors() {
        singleExecutors = new ThreadPoolExecutor(1,
                                                1,
                                                1,
                                                TimeUnit.MINUTES,
                                                new LinkedBlockingDeque<Runnable>() {});
        return singleExecutors;
    }
}
