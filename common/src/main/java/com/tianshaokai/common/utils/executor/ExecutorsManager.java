package com.tianshaokai.common.utils.executor;

import java.util.concurrent.ExecutorService;

public class ExecutorsManager {

    private static volatile ExecutorsManager instance = null;

    private ExecutorsManager() {

    }

    public static ExecutorsManager getInstance() {
        if (instance == null) {
            synchronized (ExecutorsManager.class) {
                if (instance == null) {
                    instance = new ExecutorsManager();
                }
            }
        }
        return instance;
    }

    private ExecutorService WORKER_NEW_FILE;
    private ExecutorService WORKER_NEW_PREFERENCE;

    public ExecutorService getNewFileThreadPool() {
        if (WORKER_NEW_FILE == null) {
            WORKER_NEW_FILE = BaseExecutorService.newSingleThreadExecutor(
                    new BaseThreadFactory("new_file_work", true));
        }
        return WORKER_NEW_FILE;
    }

    public ExecutorService getNewPreferenceThreadPool() {
        if (WORKER_NEW_PREFERENCE == null) {
            WORKER_NEW_PREFERENCE = BaseExecutorService.newSingleThreadExecutor(
                    new BaseThreadFactory("cache_preference_worker", true));
        }
        return WORKER_NEW_PREFERENCE;
    }



}
