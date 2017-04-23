package com.example.aimin.stegano.util;

import android.os.Environment;
import android.util.Log;

import com.example.aimin.stegano.LeanApplication;

import java.io.File;

/**
 * Created by aimin on 2017/4/21.
 */

public class PathUtils {

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static File getAvailableCacheDir() {
        if (isExternalStorageWritable()) {
            return LeanApplication.lean.getExternalCacheDir();
        } else {
            return LeanApplication.lean.getCacheDir();
        }
    }
    public static String getAvatarTmpPath() {
        return new File(getAvailableCacheDir(), "avatar_tmp").getAbsolutePath();
    }

    public static String getAvatarCropPath() {
        String path = new File(getAvailableCacheDir(), "avatar_crop").getAbsolutePath();
        Log.d("raz","in getavatar cropPath"+path);
        return path;
    }

}
