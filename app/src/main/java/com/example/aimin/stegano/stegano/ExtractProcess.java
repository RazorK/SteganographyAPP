package com.example.aimin.stegano.stegano;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by aimin on 2017/3/27.
 */

public class ExtractProcess extends BaseProcess {
    private Context context;
    private String imageUrl;
    private TextView text;
    public String msg;

    public ExtractProcess(Context context, String path, TextView text) {
        this.context = context;
        this.imageUrl = path;
        this.text = text;
    }

    /**
     *
     * @return
     */
    public String LSBExtract() {
        msg = "";
        final ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.loadImage(imageUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                PixelHelper ph = new PixelHelper(loadedImage.getHeight(),loadedImage.getWidth());
                int key = 0;
                int temp = 0;
                char tempchar;
                msg="";
                boolean flag = true;
                do {
                    for(int i=0;i<16;i++){
                        key=key*2;
                        temp=loadedImage.getPixel(ph.getNowWidth(),ph.getNowHeight());
                        Log.d("raz","get"+temp);
                        temp=temp%2;
                        if(temp==-1)
                            temp = 1;
                        key+=temp;
                        flag = ph.next();
                    }
                    tempchar=(char)key;
                    msg=msg+tempchar;
                    if(msg.length()>=50){
                        flag = false;
                    }
                    Log.d("raz","nowMsg"+msg);
                } while (tempchar!='$' && flag);
                Log.d("raz",msg);
                text.setText(msg);
            }
        });
        return msg;
    }
}
