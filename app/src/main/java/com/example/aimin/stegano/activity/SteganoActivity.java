package com.example.aimin.stegano.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.stegano.SteganoProcess;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;

/**
 * Created by aimin on 2017/3/26.
 */

public class SteganoActivity extends BaseActivity {

    private static final int REQUEST_IMAGE_PICK = 0;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.stegano_add_button)
    protected ImageButton imageButton;

    @Bind(R.id.stegano_hint_layout)
    protected LinearLayout hintLayout;

    @Bind(R.id.stegano_image_view)
    protected ImageView imageView;

    @Bind(R.id.stegano_container)
    protected LinearLayout container;

    @Bind(R.id.stegano_text_view)
    protected EditText editText;

    @Bind(R.id.stegano_image_button)
    protected ImageButton sendButton;

    @Bind(R.id.stegano_hint_text)
    protected TextView hint;

    private String oriFilePath;
    private String setFilePath;

    private double simpleSize = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stegano_view);

        //设置toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.common_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setTitle(getString(R.string.stegano_title));

        setTextEnable(false);

        //选择图片
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, null);
                photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image*//*");
                startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICK);*/
                //这里改为打开载体选择界面
                Intent intent = new Intent(SteganoActivity.this,CarrierActivity.class);
                intent.putExtra(Constants.CARRIER_SELECT,true);
                startActivityForResult(intent,REQUEST_IMAGE_PICK);
            }
        });

        //隐写&&发送&&本地刷新
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString().trim();
                if(!TextUtils.isEmpty(msg)){
                    String sid = getSteganoId();
                    SteganoProcess stegano = null;
                    Constants.createCacheFolder(SteganoActivity.this, AVUser.getCurrentUser().getObjectId());
                    setFilePath = Constants.getCachePath(SteganoActivity.this,AVUser.getCurrentUser().getObjectId(),sid);
                    try {
                        stegano = new SteganoProcess(SteganoActivity.this,oriFilePath,msg,sid,simpleSize);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    stegano.JstegProcess_V1();
                    Intent intent = new Intent();
                    intent.putExtra(Constants.STEGANO_SETIMAGE_PATH, setFilePath);
                    intent.putExtra(Constants.STEGANO_MESSAGE,msg);
                    intent.putExtra(Constants.STEGANO_ID,sid);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    toast("消息不能为空");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_IMAGE_PICK:
                    oriFilePath = data.getStringExtra(Constants.CARRIER_SELECT_ITEM);
                    //String ori = getRealPathFromURI(this, data.getData());
                    //Log.d("raz","compare"+ori+"  finish\n"+ oriFilePath);
                    //TODO: cut picture size
                    getSimpleSize();
                    hintLayout.setVisibility(View.GONE);
                    setImageView(oriFilePath);
                    setTextEnable(true);
                default:
                    break;
            }
        }
    }

    /**
     * 用于根据时间生成SteganoId
     * @return
     */
    private String getSteganoId(){
        //get SteganoId by time
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    private void setTextEnable(boolean flag) {
        editText.setEnabled(flag);
        sendButton.setEnabled(flag);
    }

    /**
     * 传入图片地址，在ImageView中显示图片
     * @param oriFilePath
     */
    private void setImageView(String oriFilePath) {
        int viewHeight,viewWidth;
        viewHeight = container.getMeasuredHeight();
        viewWidth = container.getMeasuredWidth();

        //container 背景
        container.setBackgroundColor(getResources().getColor(R.color.common_black));

        Log.d("raz","in setImageView"+oriFilePath);
        if(oriFilePath.substring(oriFilePath.length()-3, oriFilePath.length()).equals("bmp")) {
            Picasso.with(this).load(new File(oriFilePath)).into(imageView);
        } else {
            Picasso.with(this).load(new File(oriFilePath)).resize(viewWidth, viewHeight)
                    .centerInside().into(imageView);
        }
    }

    /**
     * 根据 Uri 获取文件所在的位置
     *
     * @param context
     * @param contentUri
     * @return
     */
    private String getRealPathFromURI(Context context, Uri contentUri) {
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

    private double getSimpleSize(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(oriFilePath, options);
        if(options.outHeight>options.outWidth){
            simpleSize = options.outHeight/960;
        } else
            simpleSize = options.outWidth/960;
        if(simpleSize<1)
            simpleSize = 1;
        hint.setText("resize Picture size "+simpleSize);
        return simpleSize;
    }
}
