package com.example.aimin.stegano.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.squareup.picasso.Picasso;

import java.io.File;

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

        if (0 != actualHight && 0 != actualWidth) {
            // 要保证图片的长宽比不变
            double ratio = actualHight / actualWidth;
            if (ratio > viewHeight / viewWidth) {
                viewHeight = (actualHight > viewHeight ? viewHeight : actualHight);
                viewWidth = viewHeight / ratio;
            } else {
                viewWidth = (actualWidth > viewWidth ? viewWidth : actualWidth);
                viewHeight = viewWidth * ratio;
            }
        }


        if (TextUtils.isEmpty(localPath)) {
            Picasso.with(this).load(fileUrl). resize((int) viewWidth, (int) viewHeight)
                    .centerCrop().into(imageView);
        } else {
            Picasso.with(this).load(new File(localPath)). resize((int) viewWidth, (int) viewHeight)
                    .centerCrop().into(imageView);
        }

    }
}
