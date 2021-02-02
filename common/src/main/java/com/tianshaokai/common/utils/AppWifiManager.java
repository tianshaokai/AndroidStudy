package com.tianshaokai.common.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

public class AppWifiManager {

    private WifiManager wifiManager;// 声明Wifi管理对象
    private WifiInfo wifiInfo;// Wifi信息

    private List<ScanResult> scanResultList; // 扫描出来的网络连接列表
    private List<WifiConfiguration> wifiConfigList;// 网络配置列表

    public AppWifiManager(Context context) {
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);// 获取Wifi服务
        // 得到Wifi信息
        this.wifiInfo = wifiManager.getConnectionInfo();// 得到连接信息
    }

    /**
     * Wifi状态.
     * @return 返回wifi是否可用
     */
    public boolean isWifiEnabled(){
        return wifiManager.isWifiEnabled();
    }

    /**
     * 打开 wifi
     * @return 打开wifi
     */
    public boolean openWifi(){
        if (!isWifiEnabled()) {
            return wifiManager.setWifiEnabled(true);
        } else {
            return false;
        }
    }

    /**
     * 关闭Wifi
     * @return 关闭wifi
     */
    public boolean closeWifi(){
        if (!isWifiEnabled()) {
            return true;
        } else {
            return wifiManager.setWifiEnabled(false);
        }
    }


    /**
     * 扫描网络
     */
    public List<ScanResult> startScan() {
        wifiManager.startScan();
        scanResultList = wifiManager.getScanResults(); // 扫描返回结果列表
//        wifiConfigList = wifiManager.getConfiguredNetworks(); // 扫描配置列表
        return scanResultList;
    }


}
