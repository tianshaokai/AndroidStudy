package com.tianshaokai.common.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

/**
 * 粘贴板
 */
public class ClipboardUtil {

    /**
     * 实现文本复制功能
     * @param context 上下文
     * @param content 内容
     */
    public static void copy(Context context, String content) {
        if(context == null || TextUtils.isEmpty(content)) return;
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setPrimaryClip(ClipData.newPlainText(null, content.trim()));
    }

    /**
     * 实现粘贴功能
     * @param context 上下文
     * @return 返回粘贴内容
     */
    public static String paste(Context context) {
        if(context == null) return "";
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb.getPrimaryClip() != null && cmb.hasPrimaryClip()) {
            return cmb.getPrimaryClip().getItemAt(0).getText().toString();
        }
        return "";
    }

}
