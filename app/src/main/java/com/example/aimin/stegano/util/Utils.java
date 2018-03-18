package com.example.aimin.stegano.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.aimin.stegano.LeanApplication;
import com.example.aimin.stegano.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by aimin on 2017/4/17.
 */

public class Utils {
    public static ProgressDialog showSpinnerDialog(Activity activity) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setMessage(LeanApplication.lean.getString(R.string.chat_utils_hardLoading));
        if (!activity.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    public static void toast(String s) {
        toast(LeanApplication.lean, s);
    }

    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void saveBitmap(String filePath, Bitmap bitmap) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 根据 Uri 获取文件所在的位置
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        if (contentUri.getScheme().equals("file")) {
            Log.d("raz","1in ChatFragment getRealpath "+ contentUri.getEncodedPath());
            return contentUri.getEncodedPath();
        } else {
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                if (null != cursor) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    return cursor.getString(column_index);
                } else {
                    return "";
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    public static String getImagePathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        String path = null;
        if (cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();
            cursor = context.getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }
        }
        return path;
    }

    public static String bitsFormat(double bits){
        return bytesFormat(bits/8);
    }

    public static String bytesFormat(double bytes){
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize="0B";
        if(bytes==0){
            return wrongSize;
        }
        if (bytes < 1024){
            fileSizeString = df.format(bytes) + "B";
        }
        else if (bytes < 1048576){
            fileSizeString = df.format(bytes / 1024) + "KB";
        }
        else if (bytes < 1073741824){
            fileSizeString = df.format(bytes / 1048576) + "MB";
        }
        else{
            fileSizeString = df.format(bytes / 1073741824) + "GB";
        }
        return fileSizeString;
    }
}
