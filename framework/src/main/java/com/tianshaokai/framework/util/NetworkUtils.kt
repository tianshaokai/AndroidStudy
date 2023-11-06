package com.tianshaokai.framework.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


object NetworkUtils {

    /**
     * 判断网路是否可用
     * @param context
     * @return
     */
    fun isConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val networkCapabilities = manager.getNetworkCapabilities(manager.activeNetwork)
            if (networkCapabilities != null) {
                return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) // Wifi
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)// 蜂窝传输
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) //以太网
            }
        } else {
            val networkInfo = manager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
        return false
    }

}