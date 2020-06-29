package com.tianshaokai.studyuiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class DbActivity : AppCompatActivity() {

    private val TAG:String = "DbActivity"

    companion object {
        fun lanchMode(context: Context) {
            val intent = Intent(context, DbActivity::class.java);
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val path = "/data/data/com.android.providers.contacts/databases/"

        val file = File(path)
        val fileArray = file.listFiles()
        if (fileArray == null || fileArray.isEmpty()) {
            Log.e(TAG, "获取db文件列表为空");
            return
        }
        for(file in fileArray) {
            Log.d(TAG, "文件：" + file.name + " path: " + file.absolutePath)
        }

        Log.d("TAG", "改名完成")
    }
}