package com.tianshaokai.framework.launchstarter

import android.os.Looper
import android.os.MessageQueue.IdleHandler
import com.tianshaokai.framework.launchstarter.task.DispatchRunnable
import com.tianshaokai.framework.launchstarter.task.Task
import java.util.*

class DelayInitDispatcher {
    private val mDelayTasks = LinkedList<Task>()

    private val mIdleHandler = IdleHandler {
        if (mDelayTasks.size > 0) {
            val task = mDelayTasks.poll()
            DispatchRunnable(task).run()
        }
        !mDelayTasks.isEmpty()
    }

    fun addTask(task: Task): DelayInitDispatcher {
        mDelayTasks.add(task)
        return this
    }

    fun start() {
        Looper.myQueue().addIdleHandler(mIdleHandler)
    }

    companion object {
        @JvmStatic
        fun createInstance(): DelayInitDispatcher {
            return DelayInitDispatcher()
        }
    }
}