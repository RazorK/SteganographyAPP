package com.example.aimin.stegano.stegano;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
        imageLoader.loadImage(imageUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.d("raz",loadedImage.getPixel(0,0)+"");
            }
        });
        return msg;
    }
}
