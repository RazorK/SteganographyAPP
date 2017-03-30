package com.example.aimin.stegano.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.stegano.SteganoProcess;
import com.squareup.picasso.Picasso;

import java.io.File;

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

    private String oriFilePath;
    private String setFilePath;

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
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, null);
                photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICK);
            }
        });

        //隐写&&发送&&本地刷新
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString().trim();
                if(!TextUtils.isEmpty(msg)){
                    SteganoProcess stegano = new SteganoProcess(SteganoActivity.this,oriFilePath,msg);
                    setFilePath = stegano.LSBProcess();
                    Intent intent = new Intent();
                    intent.putExtra(Constants.STEGANO_SETIMAGE_PATH, setFilePath);
                    intent.putExtra(Constants.STEGANO_MESSAGE,msg);
                    setResult(RESULT_OK, intent);
                    finish();
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
                    oriFilePath = getRealPathFromURI(this, data.getData());
                    if(TextUtils.isEmpty(oriFilePath)){
                        toast("something bad");
                    } else {
                        toast(oriFilePath);
                    }
                    hintLayout.setVisibility(View.GONE);
                    setImageView(oriFilePath);
                    setTextEnable(true);
                default:
                    break;
            }
        }
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
}
