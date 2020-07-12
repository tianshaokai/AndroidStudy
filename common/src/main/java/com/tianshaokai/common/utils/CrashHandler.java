package com.tianshaokai.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.support.v4.content.pm.PackageInfoCompat;

import com.tianshaokai.common.utils.executor.ExecutorsManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static final CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * @return 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        //收集设备参数信息
        collectDeviceInfo(mContext);
        ExecutorsManager.getInstance().getNewFileThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //保存日志文件
                saveCrashInfo2File(ex);
            }
        });
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    private void collectDeviceInfo(Context ctx) {
        PackageInfo pi = AppUtil.getInstance().getPackageInfo(ctx);
        if (pi != null) {
            String versionName = pi.versionName == null ? "null" : pi.versionName;
            String versionCode = String.valueOf(PackageInfoCompat.getLongVersionCode(pi));
            infos.put("versionName", versionName);
            infos.put("versionCode", versionCode);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                LogUtil.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                LogUtil.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     * @param ex
     */
    private void saveCrashInfo2File(Throwable ex) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        LogUtil.e(TAG + "\n" + result);
        try {
            if (!SDUtils.isSdMounted()) {
                return;
            }
            String path = SDUtils.getPackageCrashPath(mContext);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String time = DateUtil.getStringDate(DateUtil.Format_y_M_d_H_m_s_2);
            String fileName = "crash-" + time + ".log";
            FileOutputStream fos = new FileOutputStream(path + File.separator + fileName);
            fos.write(sb.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            LogUtil.e(TAG, "an error occured while writing file...", e);
        }
    }
}
