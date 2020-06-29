package com.tianshaokai.common.utils.executor;

import android.os.Process;

import java.util.concurrent.ThreadFactory;

public class BaseThreadFactory implements ThreadFactory {

    private String a;
    private final boolean b;

    public BaseThreadFactory(String paramString) {
        this(paramString, false);
    }

    public BaseThreadFactory(String paramString, boolean paramBoolean) {
        this.a = paramString;
        this.b = paramBoolean;
    }

    @Override
    public Thread newThread(Runnable paramRunnable) {
        return new BaseThread(paramRunnable, this.a) {
            public void run() {
//                if (BaseThreadFactory.a(BaseThreadFactory.this)) {
//                    Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
//                }
                super.run();
            }
        };
    }
}
