package com.example.aimin.stegano.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.stegano.ExtractProcess;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by aimin on 2017/3/26.
 */

public class ImageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);


        String fileUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
        String localPath = getIntent().getStringExtra(Constants.IMAGE_LOCAL_PATH);

        double actualHight = getIntent().getDoubleExtra(Constants.IMAGE_HEIGHT,0);
        double actualWidth = getIntent().getDoubleExtra(Constants.IMAGE_WIDTH,0);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        double viewHeight = wm.getDefaultDisplay().getWidth()-100;
        double viewWidth = wm.getDefaultDisplay().getHeight()-100;

        Constants.HW a = Constants.resize(actualHight,actualWidth,viewHeight,viewWidth);
        viewHeight = a.height;
        viewWidth = a.width;


        /*if (TextUtils.isEmpty(localPath)) {
            Picasso.with(this).load(fileUrl). resize((int) viewWidth, (int) viewHeight)
                    .centerCrop().into(imageView);
        } else {
            Picasso.with(this).load(new File(localPath)). resize((int) viewWidth, (int) viewHeight)
                    .centerCrop().into(imageView);
        }*/

        //change to AUIL
        /*if(fileUrl.substring(fileUrl.length()-3, fileUrl.length()).equals("bmp"))
            Picasso.with(this)
                    .load(fileUrl).into(imageView);
        else
            Picasso.with(this).load(fileUrl). resize((int) viewWidth, (int) viewHeight)
                    .centerCrop().into(imageView);*/

        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        /*imageLoader.loadImage(fileUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.d("raz","getloadedImage");
                Log.d("raz",loadedImage.toString());
            }
        });*/
        imageLoader.displayImage(fileUrl, imageView);
        new ExtractProcess(this,fileUrl).LSBExtract();

    }
}
