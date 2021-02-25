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

    public WifiInfo getConnectedWifiInfo() {
        return wifiInfo;
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


    /**
     * 连接有密码的wifi.
     *
     * @param SSID     ssid
     * @param Password Password
     * @return apConfig
     */
    private WifiConfiguration setWifiParamsPassword(String SSID, String Password) {
        WifiConfiguration apConfig = new WifiConfiguration();
        apConfig.SSID = "\"" + SSID + "\"";
        apConfig.preSharedKey = "\"" + Password + "\"";
        //不广播其SSID的网络
        apConfig.hiddenSSID = true;
        apConfig.status = WifiConfiguration.Status.ENABLED;
        //公认的IEEE 802.11验证算法。
        apConfig.allowedAuthAlgorithms.clear();
        apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        //公认的的公共组密码
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        //公认的密钥管理方案
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        //密码为WPA。
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        //公认的安全协议。
        apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return apConfig;
    }

    /**
     * 连接没有密码wifi.
     *
     * @param ssid ssid
     * @return configuration
     */
    private WifiConfiguration setWifiParamsNoPassword(String ssid) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + ssid + "\"";
        configuration.status = WifiConfiguration.Status.ENABLED;
        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        configuration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        configuration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return configuration;
    }

    public static final int WIFI_NO_PASS = 0;
    private static final int WIFI_WEP = 1;
    private static final int WIFI_PSK = 2;
    private static final int WIFI_EAP = 3;

    /**
     * 判断是否有密码.
     *
     * @param result ScanResult
     * @return 0
     */
    public static int getSecurity(ScanResult result) {
        if (null != result && null != result.capabilities) {
            if (result.capabilities.contains("WEP")) {
                return WIFI_WEP;
            } else if (result.capabilities.contains("PSK")) {
                return WIFI_PSK;
            } else if (result.capabilities.contains("EAP")) {
                return WIFI_EAP;
            }
        }
        return WIFI_NO_PASS;
    }

}
