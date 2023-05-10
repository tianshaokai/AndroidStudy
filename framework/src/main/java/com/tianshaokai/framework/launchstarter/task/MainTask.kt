package com.tianshaokai.framework.launchstarter.task

abstract class MainTask : Task() {
    override fun runOnMainThread(): Boolean {
        return true
    }
}