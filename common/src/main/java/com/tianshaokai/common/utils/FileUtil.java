package com.tianshaokai.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static boolean isSdMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * @return 获取外部相册路径
     */
    public static String getStorageDCIMPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    /**
     * @return 获取外部音频路径
     */
    public static String getStorageAudioPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }

    /**
     * @return 获取外部视频路径
     */
    public static String getStorageVideoPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }


    public static String getExternalFilesDir(Context context, String dir) {
        if (context == null) {
            return "";
        }
        File filePath = context.getApplicationContext().getExternalFilesDir(dir);
        if (filePath != null) {
            return filePath.getAbsolutePath();
        }
        return "";
    }

    public static String getPackageDCIMPath(Context context) {
        if (context == null) {
            return "";
        }
        File filePath = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if (filePath != null) {
            return filePath.getAbsolutePath();
        }
        return "";
    }

    public static String getPackageAudioPath(Context context) {
        if (context == null) {
            return "";
        }
        File filePath = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (filePath != null) {
            return filePath.getAbsolutePath();
        }
        return "";
    }

    public static String getPackageMoviePath(Context context) {
        if (context == null) {
            return "";
        }
        File filePath = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (filePath != null) {
            return filePath.getAbsolutePath();
        }
        return "";
    }

    public static String getPackageCrashPath(Context context) {
        return getExternalFilesDir(context, "Crash");
    }

    public static String saveBitmapToDCIM(Context context, Bitmap bitmap) {
        String path = getPackageDCIMPath(context);
        String filePath = path + File.separator + "IMG_" + DateUtil.getTimeStamp() + ".png";
        File file = new File(filePath);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            out.flush();
        } catch (IOException e) {
            LogUtil.e("saveBitmap", "保存图片异常", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bitmap.recycle();
        }
        return filePath;
    }


    public static long getFileSize(String filePath) {
        long blockSize = 0;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return blockSize;
            }
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG,"获取文件大小失败!");
        }
        return blockSize;
    }

    /**
     * 获取指定文件大小
     * @param file 文件
     * @return 返回文件大小
     */
    public static long getFileSize(File file) {
        long size = 0;
        if (file == null) {
            return size;
        }
        FileInputStream fis = null;
        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                size = fis.available();
            }
        } catch (IOException e) {
            Log.e("getFileSize", "获取文件大小异常", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

    /**
     * 获取指定文件夹
     * @param f
     * @return
     */
    private static long getFileSizes(File f) {
        long size = 0;
        if (f == null) {
            return size;
        }
        File[] fileArray = f.listFiles();
        if (fileArray == null || fileArray.length == 0) {
            return size;
        }
        for (File file : fileArray) {
            if (file == null) {
                continue;
            }
            if (file.isDirectory()) {
                size = size + getFileSizes(file);
            } else {
                size = size + getFileSize(file);
            }
        }
        return size;
    }
}
