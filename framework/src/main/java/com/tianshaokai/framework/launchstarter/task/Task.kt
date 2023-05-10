package com.tianshaokai.framework.launchstarter.task

import android.os.Process
import com.tianshaokai.framework.launchstarter.utils.DispatcherExecutor
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService

abstract class Task : ITask {

    @Volatile
    private var mIsWaiting = false// 是否正在等待
    @Volatile
    private var mIsRunning = false// 是否正在执行
    @Volatile
    private var mIsFinished = false // Task是否执行完成
    @Volatile
    private var mIsSend = false// Task是否已经被分发

    // 当前Task依赖的Task数量（需要等待被依赖的Task执行完毕才能执行自己），默认没有依赖
    private val mDepends = CountDownLatch(if (dependsOn() == null) 0 else dependsOn()!!.size)

    /**
     * 当前Task等待，让依赖的Task先执行
     */
    fun waitToSatisfy() {
        try {
            mDepends.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 依赖的Task执行完一个
     */
    fun satisfy() {
        mDepends.countDown()
    }

    /**
     * 是否需要尽快执行，解决特殊场景的问题：一个Task耗时非常多但是优先级却一般，很有可能开始的时间较晚，
     * 导致最后只是在等它，这种可以早开始。
     *
     * @return
     */
    open fun needRunAsSoon(): Boolean {
        return false
    }

    /**
     * Task的优先级，运行在主线程则不要去改优先级
     *
     * @return
     */
    override fun priority(): Int {
        return Process.THREAD_PRIORITY_BACKGROUND
    }

    /**
     * Task执行在哪个线程池，默认在IO的线程池；
     * CPU 密集型的一定要切换到DispatcherExecutor.getCPUExecutor();
     *
     * @return
     */
    override fun runOn(): ExecutorService? {
        return DispatcherExecutor.getIOExecutor()
    }

    /**
     * 异步线程执行的Task是否需要在被调用await的时候等待，默认不需要
     *
     * @return
     */
    override fun needWait(): Boolean {
        return false
    }

    /**
     * 当前Task依赖的Task集合（需要等待被依赖的Task执行完毕才能执行自己），默认没有依赖
     *
     * @return
     */
    override fun dependsOn(): List<Class<out Task>>? {
        return null
    }

    /**
     * 是否运行在主线程 默认false
     */
    override fun runOnMainThread(): Boolean {
        return false
    }

    override fun getTailRunnable(): Runnable? {
        return null
    }

    override fun setTaskCallBack(callBack: TaskCallBack) {

    }

    /**
     * 是否只在主进程，默认是
     *
     * @return
     */
    override fun onlyInMainProcess(): Boolean {
        return true
    }

    override fun needCall(): Boolean {
        return false
    }

    fun isRunning(): Boolean {
        return mIsRunning
    }

    fun setRunning(mIsRunning: Boolean) {
        this.mIsRunning = mIsRunning
    }

    fun isFinished(): Boolean {
        return mIsFinished
    }

    fun setFinished(finished: Boolean) {
        mIsFinished = finished
    }

    fun isSend(): Boolean {
        return mIsSend
    }

    fun setSend(send: Boolean) {
        mIsSend = send
    }

    fun isWaiting(): Boolean {
        return mIsWaiting
    }

    fun setWaiting(mIsWaiting: Boolean) {
        this.mIsWaiting = mIsWaiting
    }

}