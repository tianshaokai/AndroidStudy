package com.tianshaokai.common.utils;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class ImageUtil {

    private static final String TAG = "ImageUtil";

    //保存文件到系统图库
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        if (context == null) {
            LogUtil.e(TAG, "Context is null");
            return false;
        }
        if (bmp == null) {
            return false;
        }
        String saveFilePath = FileUtil.saveBitmapToDCIM(context, bmp);
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, saveFilePath);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

            ContentResolver cr = context.getContentResolver();
            Uri url = null;

            try {
                //直接插入系统数据库
                //使用 MediaStore.Images.Media.insertImage 插入图片系统会生成一张缩略图
                url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } catch (Exception e) {
                LogUtil.e("插入图片失败：" + e.toString());
            } finally {
                if (url != null) {
                    cr.delete(url, null, null);
                }
            }
            syncAlbum(context, saveFilePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 同步刷新系统相册
     * @param imageUrl
     */
    private static void syncAlbum(Context context, String imageUrl) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageUrl)));
    }
}
