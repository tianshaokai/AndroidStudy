package com.tianshaokai.framework.util

import android.util.Log
import com.google.gson.*

object GsonUtil {
    private val gson: Gson = Gson()

    fun toJson(any: Any): String {
        return gson.toJson(any)
    }

    fun <T> fromJsonToList(json: String, cls: Class<T>): List<T> {
        val list: MutableList<T> = ArrayList()
        try {
            val array = JsonParser().parse(json).asJsonArray
            for (elem in array) {
                list.add(gson.fromJson<T>(elem, cls))
            }
        } catch (e: JsonSyntaxException) {
            Log.e("非法json字符串", e.message!!)
        }
        return list
    }

    fun <T> fromJsonToBean(json: String, cls: Class<T>): T? {
        var t: T? = null
        try {
            t = gson.fromJson<T>(json, cls)
        } catch (e: JsonSyntaxException) {
            Log.e("非法json字符串", e.message!!)
        }
        return t
    }
}