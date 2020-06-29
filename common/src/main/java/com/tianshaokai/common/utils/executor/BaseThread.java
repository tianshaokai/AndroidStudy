package com.tianshaokai.common.utils.executor;

import java.util.concurrent.atomic.AtomicInteger;

public class BaseThread extends Thread {

    private static final AtomicInteger b = new AtomicInteger();
    private static final AtomicInteger c = new AtomicInteger();

    public BaseThread(Runnable paramRunnable, String paramString) {
        super(paramRunnable, paramString + "-" + b.incrementAndGet());
    }
}
