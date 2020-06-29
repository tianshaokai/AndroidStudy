package com.tianshaokai.common.utils.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class BaseExecutorService {


    public static ExecutorService newSingleThreadExecutor(ThreadFactory paramThreadFactory) {
        return new BaseThreadPoolExecutor(1, 1, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), paramThreadFactory);
    }
}
