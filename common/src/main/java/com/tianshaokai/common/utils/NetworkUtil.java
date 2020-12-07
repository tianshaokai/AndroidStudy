package com.tianshaokai.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkUtil {
    /**
     * 检测网络是否连接
     *
     * @param context 上下文
     * @return 结果
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (context != null) {
            if (Build.VERSION.SDK_INT < 23) {
                NetworkInfo mWiFiNetworkInfo = cm.getActiveNetworkInfo();
                if (mWiFiNetworkInfo != null) {
                    if (mWiFiNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {//WIFI
                        return true;
                    } else if (mWiFiNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {//移动数据
                        return true;
                    }
                }
            } else {
                Network network = cm.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                    if (nc != null) {
                        if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {//WIFI
                            return true;
                        } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {//移动数据
                            return true;
                        }
                    }
                }

            }

        }
        return false;
    }


}
