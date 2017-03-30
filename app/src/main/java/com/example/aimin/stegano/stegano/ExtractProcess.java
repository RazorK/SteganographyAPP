package com.example.aimin.stegano.stegano;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by aimin on 2017/3/27.
 */

public class ExtractProcess extends BaseProcess {
    private Context context;
    private String imageUrl;
    private String msg;

    public ExtractProcess(Context context, String path) {
        this.context = context;
        this.imageUrl = path;
    }

    public String LSBExtract() {
        msg = "";
        ImageLoader imageLoader = ImageLoader.getInstance();

        // Load image, decode it to Bitmap and return Bitmap to callback
        msg = "hi I just met you";
        return msg;
    }
}
