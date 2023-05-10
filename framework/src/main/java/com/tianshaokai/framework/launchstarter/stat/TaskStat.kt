package com.tianshaokai.framework.launchstarter.stat

import com.tianshaokai.framework.launchstarter.utils.DispatcherLog
import java.util.concurrent.atomic.AtomicInteger

object TaskStat {
    @Volatile
    private var sCurrentSituation = ""
    private val sBeans = arrayListOf<TaskStatBean>()
    private var sTaskDoneCount = AtomicInteger()
    private const val sOpenLaunchStat = false // 是否开启统计

    fun getCurrentSituation(): String? {
        return sCurrentSituation
    }

    fun setCurrentSituation(currentSituation: String) {
        if (!sOpenLaunchStat) {
            return
        }
        DispatcherLog.i("currentSituation   $currentSituation")
        sCurrentSituation = currentSituation
        setLaunchStat()
    }

    fun markTaskDone() {
        sTaskDoneCount.getAndIncrement()
    }

    fun setLaunchStat() {
        sBeans.add(TaskStatBean(sCurrentSituation, sTaskDoneCount.get()))
        sTaskDoneCount = AtomicInteger(0)
    }
}