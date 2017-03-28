package com.example.aimin.stegano.viewholder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.activity.ImageActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import butterknife.Bind;

/**
 * Created by aimin on 2017/3/26.
 */

public class LeftImageViewHolder extends CommonViewHolder {
    @Bind(R.id.chat_left_text_tv_time)
    protected TextView timeView;

    @Bind(R.id.chat_left_image_content)
    protected ImageView contentView;

    @Bind(R.id.chat_left_text_tv_name)
    protected TextView nameView;

    private static final int MAX_DEFAULT_HEIGHT = 400;
    private static final int MAX_DEFAULT_WIDTH = 300;

    public LeftImageViewHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.left_image_layout);
    }

    @Override
    public void bindData(Object o) {
        if(o instanceof AVIMImageMessage) {
            final AVIMImageMessage message = (AVIMImageMessage)o;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            String time = dateFormat.format(message.getTimestamp());

            String localFilePath = message.getLocalFilePath();

            // 图片的真实高度与宽度
            double actualHight = message.getHeight();
            double actualWidth = message.getWidth();

            double viewHeight = MAX_DEFAULT_HEIGHT;
            double viewWidth = MAX_DEFAULT_WIDTH;

            Constants.HW hw = Constants.resize(actualHight, actualWidth, viewHeight,viewWidth);
            viewHeight = hw.height;
            viewWidth = hw.width;
            Log.d("raz","in left"+viewHeight+"  "+viewWidth);

            if (!TextUtils.isEmpty(message.getFileUrl())) {
                if (message.getFileMetaData().get("format").toString().equals("image/bmp")) {
                    Log.d("raz", "in LeftViewHolder");
                    Picasso.with(getContext().getApplicationContext())
                            .load(message.getFileUrl()).into(contentView);
                }
                else
                    Picasso.with(getContext().getApplicationContext()).load(message.getFileUrl()).
                            resize((int) viewWidth, (int) viewHeight).centerCrop().into(contentView);
            }

            /*if (!TextUtils.isEmpty(localFilePath)) {
                if(localFilePath.substring(localFilePath.length()-3, localFilePath.length()).equals("bmp"))
                    Picasso.with(getContext().getApplicationContext()).
                            load(new File(localFilePath)).into(contentView);
                else
                    Picasso.with(getContext().getApplicationContext()).load(new File(localFilePath)).
                            resize((int) viewWidth, (int) viewHeight).centerCrop().into(contentView);
            } else if (!TextUtils.isEmpty(message.getFileUrl())) {
                if(message.getFileMetaData().get("format") == "image/bmp")
                    Picasso.with(getContext().getApplicationContext())
                            .load(message.getFileUrl()).into(contentView);
                else
                    Picasso.with(getContext().getApplicationContext()).load(message.getFileUrl()).
                            resize((int) viewWidth, (int) viewHeight).centerCrop().into(contentView);
            } else {
                contentView.setImageResource(0);
            }*/

            timeView.setText(time);

            AVQuery<AVObject> avQuery = new AVQuery<>("_User");
            avQuery.getInBackground(message.getFrom(), new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    nameView.setText(avObject.get("username").toString());
                }
            });

            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ImageActivity.class);
                    intent.setPackage(getContext().getPackageName());
                    intent.putExtra(Constants.IMAGE_LOCAL_PATH, message.getLocalFilePath());
                    intent.putExtra(Constants.IMAGE_URL, message.getFileUrl());
                    intent.putExtra(Constants.IMAGE_HEIGHT,(double) message.getHeight());
                    intent.putExtra(Constants.IMAGE_WIDTH,(double) message.getWidth());
                    getContext().startActivity(intent);
                }
            });
        }

    }

    public void showTimeView(boolean isShow) {
        timeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
