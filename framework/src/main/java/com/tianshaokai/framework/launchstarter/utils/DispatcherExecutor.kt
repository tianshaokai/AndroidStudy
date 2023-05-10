package com.tianshaokai.framework.launchstarter.utils

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
object DispatcherExecutor {
    private var sCPUThreadPoolExecutor: ThreadPoolExecutor
    private var sIOThreadPoolExecutor: ExecutorService
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val CORE_POOL_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(5))
    private val MAXIMUM_POOL_SIZE = CORE_POOL_SIZE
    private const val KEEP_ALIVE_SECONDS = 5.toLong()
    private val sPoolWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue()
    private val sThreadFactory: DefaultThreadFactory = DefaultThreadFactory()
    private val sHandler =
        RejectedExecutionHandler { r, _ ->
            // 一般不会到这里
            Executors.newCachedThreadPool().execute(r)
        }

    init {
        sCPUThreadPoolExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory, sHandler
        )
        sCPUThreadPoolExecutor.allowCoreThreadTimeOut(true)
        sIOThreadPoolExecutor =
            Executors.newCachedThreadPool(sThreadFactory)
    }

    /**
     * 获取IO线程池
     * @return
     */
    fun getIOExecutor(): ExecutorService {
        return sIOThreadPoolExecutor
    }

    /**
     * The default thread factory.
     */
    private class DefaultThreadFactory : ThreadFactory {
        private val group: ThreadGroup?
        private val threadNumber =
            AtomicInteger(1)
        private val namePrefix: String
        override fun newThread(r: Runnable): Thread {
            val t = Thread(
                group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0
            )
            if (t.isDaemon) t.isDaemon = false
            if (t.priority != Thread.NORM_PRIORITY) t.priority = Thread.NORM_PRIORITY
            return t
        }

        companion object {
            private val poolNumber =
                AtomicInteger(1)
        }

        init {
            val s = System.getSecurityManager()
            group =
                if (s != null) s.threadGroup else Thread.currentThread().threadGroup
            namePrefix = "TaskDispatcherPool-" +
                    poolNumber.getAndIncrement() +
                    "-Thread-"
        }
    }
}