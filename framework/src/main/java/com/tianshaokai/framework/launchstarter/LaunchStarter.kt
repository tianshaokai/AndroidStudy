package com.tianshaokai.framework.launchstarter

import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.annotation.UiThread
import com.tianshaokai.framework.launchstarter.sort.TaskSortUtil
import com.tianshaokai.framework.launchstarter.stat.TaskStat
import com.tianshaokai.framework.launchstarter.task.DispatchRunnable
import com.tianshaokai.framework.launchstarter.task.Task
import com.tianshaokai.framework.launchstarter.task.TaskCallBack
import com.tianshaokai.framework.launchstarter.utils.DispatcherLog
import com.tianshaokai.framework.launchstarter.utils.Utils
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

class LaunchStarter {

    companion object {
        private const val WAIT_TIME = 10000

        @Volatile
        private var sHasInit = false
        private var sIsMainProcess = false
        private var mStartTime: Long = 0
        private val mFutures = arrayListOf<Future<*>>()
        private var mAllTasks = arrayListOf<Task>()
        private val mClsAllTasks = arrayListOf<Class<out Task?>>()

        private val mMainThreadTasks = mutableListOf<Task>()

        private var mCountDownLatch: CountDownLatch? = null
        private val mNeedWaitCount = AtomicInteger() //保存需要Wait的Task的数量
        private val mNeedWaitTasks = arrayListOf<Task>() //调用了await的时候还没结束的且需要等待的Task
        private val mFinishedTasks = mutableListOf<Class<out Task?>>() //已经结束了的Task
        private val mDependedHashMap = HashMap<Class<out Task?>, ArrayList<Task>>()
        private val mAnalyseCount = AtomicInteger() //启动器分析的次数，统计下分析的耗时；

        @JvmStatic
        fun init(context: Context?) {
            if (context != null) {
                sHasInit = true
                sIsMainProcess = Utils.isMainProcess(context)
            }
        }

        @JvmStatic
        fun createInstance(): LaunchStarter {
            if (!sHasInit) {
                throw RuntimeException("Must call TaskDispatcher.init first")
            }
            return LaunchStarter()
        }

        fun isMainProcess(): Boolean {
            return sIsMainProcess
        }
    }


    fun addTask(task: Task?): LaunchStarter {
        if (task != null) {
            collectDepends(task)
            mAllTasks.add(task)
            mClsAllTasks.add(task.javaClass)
            // 非主线程且需要wait的，主线程不需要CountDownLatch也是同步的
            if (ifNeedWait(task)) {
                mNeedWaitTasks.add(task)
                mNeedWaitCount.getAndIncrement()
            }
        }
        return this
    }

    private fun collectDepends(task: Task) {
        if (task.dependsOn() != null && task.dependsOn()!!.isNotEmpty()) {
            for (cls in task.dependsOn()!!) {
                if (mDependedHashMap[cls] == null) {
                    mDependedHashMap[cls] = ArrayList()
                }
                mDependedHashMap[cls]!!.add(task)
                if (mFinishedTasks.contains(cls)) {
                    task.satisfy()
                }
            }
        }
    }

    private fun ifNeedWait(task: Task): Boolean {
        return task.runOnMainThread().not() && task.needWait()
    }

    @UiThread
    fun start() {
        mStartTime = System.currentTimeMillis()
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw java.lang.RuntimeException("must be called from UiThread")
        }
        if (mAllTasks.size > 0) {
            mAnalyseCount.getAndIncrement()
            printDependedMsg()
            mAllTasks = TaskSortUtil.getSortResult(mAllTasks, mClsAllTasks) as ArrayList<Task>
            mCountDownLatch = CountDownLatch(mNeedWaitCount.get())
            sendAndExecuteAsyncTasks()
            DispatcherLog.i("task analyse cost ${(System.currentTimeMillis() - mStartTime)} begin main ")
            executeTaskMain()
        }
        DispatcherLog.i("task analyse cost startTime cost ${(System.currentTimeMillis() - mStartTime)}")

    }

    private fun executeTaskMain() {
        mStartTime = System.currentTimeMillis()
        for (task in mMainThreadTasks) {
            val time = System.currentTimeMillis()
            DispatchRunnable(task, this).run()
            DispatcherLog.i(
                "real main ${task.javaClass.simpleName} cost  ${(System.currentTimeMillis() - time)}"
            )
        }
        DispatcherLog.i("maintask cost ${(System.currentTimeMillis() - mStartTime)}")
    }

    private fun sendAndExecuteAsyncTasks() {
        mAllTasks.forEach {
            if (it.onlyInMainProcess() && !sIsMainProcess) {
                markTaskDone(it)
            } else {
                sendTaskReal(it)
            }
            it.setSend(true)
        }
    }

    /**
     * 查看被依赖的信息
     */
    private fun printDependedMsg() {
        DispatcherLog.i("needWait size : ${mNeedWaitCount.get()}")
        if (false) {
            for (cls in mDependedHashMap.keys) {
                DispatcherLog.i("cls ${cls.simpleName}   ${mDependedHashMap[cls]!!.size}")
                for (task in mDependedHashMap[cls]!!) {
                    DispatcherLog.i("cls       ${task.javaClass.simpleName}")
                }
            }
        }
    }

    /**
     * 通知Children一个前置任务已完成
     *
     * @param launchTask
     */
    fun satisfyChildren(launchTask: Task) {
        val arrayList = mDependedHashMap[launchTask.javaClass]
        if (arrayList != null && arrayList.size > 0) {
            arrayList.forEach {
                it.satisfy()
            }
        }
    }

    fun markTaskDone(task: Task) {
        if (ifNeedWait(task)) {
            mFinishedTasks.add(task.javaClass)
            mNeedWaitTasks.remove(task)
            mCountDownLatch?.countDown()
            mNeedWaitCount.getAndDecrement()
        }
    }

    private fun sendTaskReal(task: Task) {
        if (task.runOnMainThread()) {
            mMainThreadTasks.add(task)
            if (task.needCall()) {
                task.setTaskCallBack(object : TaskCallBack {
                    override fun call() {
                        TaskStat.markTaskDone()
                        task.setFinished(true)
                        satisfyChildren(task)
                        markTaskDone(task)
                        DispatcherLog.i("${task.javaClass.simpleName} finish")
                        Log.i("testLog", "call")
                    }
                })
            }
        } else { // 直接发，是否执行取决于具体线程池
            val future =
                task.runOn()!!.submit(DispatchRunnable(task, this))
            mFutures.add(future)
        }
    }

    fun executeTask(task: Task) {
        if (ifNeedWait(task)) {
            mNeedWaitCount.getAndIncrement()
        }
        task.runOn()!!.execute(DispatchRunnable(task, this))
    }

    @UiThread
    fun await() {
        try {
            if (DispatcherLog.isDebug()) {
                DispatcherLog.i("still has " + mNeedWaitCount.get())
//                for (task in mNeedWaitTasks) {
//                    DispatcherLog.i("needWait: " + task::class.java.simpleName)
//                }
            }
            if (mNeedWaitCount.get() > 0) {
                mCountDownLatch?.await(
                    WAIT_TIME.toLong(),
                    TimeUnit.MILLISECONDS
                )
            }
        } catch (e: InterruptedException) {
        }
    }

}