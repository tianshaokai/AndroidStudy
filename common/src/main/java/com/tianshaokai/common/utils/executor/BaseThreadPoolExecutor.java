package com.tianshaokai.common.utils.executor;

import com.tianshaokai.common.utils.LogUtil;

import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class BaseThreadPoolExecutor extends ThreadPoolExecutor {

    private final ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();
    private final AtomicLong b = new AtomicLong();
    private final AtomicLong c = new AtomicLong();

    public BaseThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit paramTimeUnit,
                                  BlockingQueue<Runnable> paramBlockingQueue, ThreadFactory paramThreadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, paramTimeUnit, paramBlockingQueue, paramThreadFactory);
    }

    @Override
    protected void afterExecute(Runnable paramRunnable, Throwable paramThrowable) {
        try {
            long l = System.nanoTime() - threadLocal.get().longValue();
            this.b.incrementAndGet();
            this.c.addAndGet(l);
            LogUtil.d("BMThreadPoolExecutor", String.format(Locale.getDefault(),
                    "Thread %s : end %s,time = %d ns", Thread.currentThread(), paramRunnable, l));
        } finally {
            super.afterExecute(paramRunnable, paramThrowable);
        }
    }

    @Override
    protected void beforeExecute(Thread paramThread, Runnable paramRunnable) {
        super.beforeExecute(paramThread, paramRunnable);
        LogUtil.d("BMThreadPoolExecutor", String.format("Thread %s : start %s", paramThread, paramRunnable));
        this.threadLocal.set(System.nanoTime());
    }

    @Override
    protected void terminated() {
        try {
            LogUtil.d("BMThreadPoolExecutor", String.format(Locale.getDefault(),
                    "Terminated : total time = %d, avg time = %d ns", this.c.get(), this.c.get() / this.b.get()));
        } finally {
            super.terminated();
        }
    }
}
