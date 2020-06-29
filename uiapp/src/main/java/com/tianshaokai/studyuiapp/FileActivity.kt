package com.tianshaokai.studyuiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class FileActivity : AppCompatActivity() {

    companion object {
        fun lanchMode(context: Context) {
            val intent = Intent(context, FileActivity::class.java);
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val path = Environment.getExternalStorageDirectory().absolutePath + "/LuPingDaShi/Rec"

        val file = File(path)
        val fileArray = file.listFiles()
        if(fileArray == null || fileArray.isEmpty()) return
        for(file in fileArray) {
            if (!file.isHidden) {
                Log.d("TAG", "不是隐藏文件: " + file.name + " " + file.absolutePath)
                continue
            }
            Log.d("TAG", file.name + " " + file.absolutePath + " " + file.isHidden)

            val newName = file.name.substring(1, file.name.length)
            val newFile = File(path + "/" + newName)
            file.renameTo(newFile)
        }

        Log.d("TAG", "改名完成")
    }
}