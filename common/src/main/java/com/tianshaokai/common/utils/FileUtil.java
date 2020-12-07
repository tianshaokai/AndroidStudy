package com.tianshaokai.common.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static String saveBitmapToDCIM(Context context, Bitmap bitmap) {
        String path = SDUtils.getPackageDCIMPath(context);
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
            LogUtil.e("getFileSize", "获取文件大小异常", e);
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
     * @param fileDir
     * @return
     */
    private static long getFileSizes(File fileDir) {
        long size = 0;
        if (fileDir == null) {
            return size;
        }
        File[] fileArray = fileDir.listFiles();
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

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static boolean deleteDir(String dirName) {
        if (!dirName.endsWith(File.separator)) {//dirName不以分隔符结尾则自动添加分隔符
            dirName = dirName + File.separator;
        }
        File fileDir = new File(dirName);//根据指定的文件名创建File对象
        if (!fileDir.exists() || (!fileDir.isDirectory())) { //目录不存在或者
            LogUtil.e(TAG, "目录删除失败" + dirName + "目录不存在！");
            return false;
        }
        File[] fileArrays = fileDir.listFiles();//列出源文件下所有文件，包括子目录
        if (fileArrays == null || fileArrays.length == 0) {
            LogUtil.e(TAG, "目录" + dirName + " 获取文件为空");
            return false;
        }
        for (File file : fileArrays) {//将源文件下的所有文件逐个删除
            deleteAnyone(file.getAbsolutePath());
        }
        if (fileDir.delete()) {//删除当前目录
            LogUtil.e(TAG, "目录" + dirName + "删除成功！");
        }
        return true;
    }

    /**
     * 判断指定的文件或文件夹删除是否成功
     * @param FileName 文件或文件夹的路径
     * @return true or false 成功返回true，失败返回false
     */
    public static boolean deleteAnyone(String FileName) {
        File file = new File(FileName);//根据指定的文件名创建File对象
        if (!file.exists()) {  //要删除的文件不存在
            LogUtil.e(TAG, "文件" + FileName + "不存在，删除失败！");
            return false;
        } else { //要删除的文件存在
            if (file.isFile()) { //如果目标文件是文件
                return deleteFile(FileName);
            } else {  //如果目标文件是目录
                return deleteDir(FileName);
            }
        }
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);//根据指定的文件名创建File对象
        if (file.exists() && file.isFile()) { //要删除的文件存在且是文件
            boolean isDelete = file.delete();
            if (isDelete) {
                LogUtil.d(TAG, "文件" + fileName + "删除成功！");
            } else {
                LogUtil.e(TAG, "文件" + fileName + "删除失败！");
            }
            return isDelete;
        } else {
            LogUtil.e(TAG, "文件" + fileName + "不存在，删除失败！");
            return false;
        }
    }

    public static String getFileContent(String path) {
        FileReader fileReader = null;
        BufferedReader bufferReader = null;
        try {
            fileReader = new FileReader(path);
            bufferReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String str;
            while ((str = bufferReader.readLine()) != null) {
                stringBuilder.append(str);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void writeFile(String fileName, String filePath, String content) {
        writeFile(fileName, filePath, content, false);
    }

    public static void writeFile(String fileName, String filePath, String content, boolean isAppend) {
        File sf = new File(filePath);
        if (!sf.exists()) {
            sf.mkdirs();
        }
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(filePath + fileName, isAppend);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content + "\n");
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
