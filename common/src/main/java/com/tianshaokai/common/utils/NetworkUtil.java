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
    @SuppressWarnings("deprecation")
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
        } else {
            NetworkInfo mWiFiNetworkInfo = cm.getActiveNetworkInfo();
            if (mWiFiNetworkInfo != null) {
                if (mWiFiNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {//WIFI
                    return true;
                } else if (mWiFiNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {//移动数据
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断网路是否可用
     * @param context
     * @return
     */
    public static boolean isNetWorkAvailable(Context context){
        ConnectivityManager manager  = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取所有可用的网络连接信息
        NetworkInfo[] infos = manager.getAllNetworkInfo();
        if(null != infos){
            for(NetworkInfo info : infos){
                if(info.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isConnected(Context context) {
        if (null == context) {
            return false;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network[] networks = new Network[0];
            if (connectivity != null) {
                networks = connectivity.getAllNetworks();
            }
            for (Network network : networks) {
                NetworkCapabilities capabilities = connectivity.getNetworkCapabilities(network);
//                if (capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
//                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
//                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
//                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))) {
//                    return true;
//                }
                if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    return true;
                }
            }
        } else {
            NetworkInfo info = null;
            if (connectivity != null) {
                info = connectivity.getActiveNetworkInfo();
            }
            if (info != null && info.isAvailable()) {
                return true;
            }

        }
        return false;
    }


    public static boolean isWifi(Context context) {
        if (null == context) {
            return false;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (connectivity != null) {
                Network networks = connectivity.getActiveNetwork();
                NetworkCapabilities networkCapabilities = connectivity.getNetworkCapabilities(networks);
                if (networkCapabilities != null) {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }
                }
            }
        } else {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
            }

        }

        return false;
    }


    public static boolean isMobile(Context context) {
        if (null == context) {
            return false;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (connectivity != null) {
                Network networks = connectivity.getActiveNetwork();
                NetworkCapabilities networkCapabilities = connectivity.getNetworkCapabilities(networks);
                if (networkCapabilities != null) {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    }
                }
            }
        } else {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                }
            }

        }

        return false;
    }

}
