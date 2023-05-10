package com.tianshaokai.framework.launchstarter.task

import android.os.Looper
import android.os.Process
import androidx.core.os.TraceCompat
import com.tianshaokai.framework.launchstarter.LaunchStarter
import com.tianshaokai.framework.launchstarter.stat.TaskStat
import com.tianshaokai.framework.launchstarter.utils.DispatcherLog

class DispatchRunnable(
    private val mTask: Task?,
    private val mLaunchStarter: LaunchStarter? = null
) : Runnable {

    override fun run() {
        if (mTask == null) return
        TraceCompat.beginSection(mTask.javaClass.simpleName)
        DispatcherLog.i(
            "${mTask.javaClass.simpleName} begin run  Situation  ${TaskStat.getCurrentSituation()}"
        )
        Process.setThreadPriority(mTask.priority())
        var startTime = System.currentTimeMillis()

        mTask.setWaiting(true)
        mTask.waitToSatisfy()

        val waitTime = System.currentTimeMillis() - startTime
        startTime = System.currentTimeMillis()

        // 执行Task
        mTask.setRunning(true)
        mTask.run()

        // 执行Task的尾部任务
        mTask.getTailRunnable()?.run()

        if (!mTask.needCall() || !mTask.runOnMainThread()) {
            printTaskLog(startTime, waitTime)
            TaskStat.markTaskDone()
            mTask.setFinished(true)
            if (mLaunchStarter != null) {
                mLaunchStarter.satisfyChildren(mTask)
                mLaunchStarter.markTaskDone(mTask)
            }
            DispatcherLog.i("${mTask.javaClass.simpleName} finish")
        }
        TraceCompat.endSection()
    }

    /**
     * 打印出来Task执行的日志
     *
     * @param startTime
     * @param waitTime
     */
    private fun printTaskLog(startTime: Long, waitTime: Long) {
        if (mTask == null) return
        val runTime = System.currentTimeMillis() - startTime
        if (DispatcherLog.isDebug()) {
            DispatcherLog.i(
                mTask.javaClass.simpleName + "  wait " + waitTime + "    run "
                        + runTime + "   isMain " + (Looper.getMainLooper() == Looper.myLooper())
                        + "  needWait " + (mTask.needWait() || Looper.getMainLooper() == Looper.myLooper())
                        + "  ThreadId " + Thread.currentThread().id
                        + "  ThreadName " + Thread.currentThread().name
                        + "  Situation  " + TaskStat.getCurrentSituation()
            )
        }
    }
}