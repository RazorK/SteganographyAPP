package com.example.aimin.stegano.viewholder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
import com.example.aimin.stegano.stegano.ExtractProcess;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by aimin on 2017/5/8.
 */

public class LeftSteganoViewHolder extends CommonViewHolder {
    @Bind(R.id.chat_left_text_tv_time)
    protected TextView timeView;

    @Bind(R.id.chat_left_image_content)
    protected ImageView contentView;

    @Bind(R.id.chat_left_text_tv_name)
    protected TextView nameView;

    @Bind(R.id.chat_left_stegano_msg)
    protected TextView steganoMsg;

    private static final int MAX_DEFAULT_HEIGHT = 400;
    private static final int MAX_DEFAULT_WIDTH = 300;

    private Context context;

    public LeftSteganoViewHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.left_image_layout);
        this.context = context;
    }

    @Override
    public void bindData(Object o) {
        if(o instanceof AVIMImageMessage) {
            final AVIMImageMessage message = (AVIMImageMessage)o;
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
//            String time = dateFormat.format(message.getTimestamp());
            int style = DateFormat.MEDIUM;
            DateFormat df;
            df = DateFormat.getDateInstance(style, Locale.US);
            String time = df.format(message.getTimestamp());

            String localFilePath = message.getLocalFilePath();

            // 图片的真实高度与宽度
            double actualHight = message.getHeight();
            double actualWidth = message.getWidth();

            double viewHeight = MAX_DEFAULT_HEIGHT;
            double viewWidth = MAX_DEFAULT_WIDTH;

            Constants.HW hw = Constants.resize(actualHight, actualWidth, viewHeight,viewWidth);
            viewHeight = hw.height;
            viewWidth = hw.width;

            if (!TextUtils.isEmpty(message.getFileUrl())) {
                if (message.getFileMetaData().get("format").toString().equals("image/bmp")) {
                    Picasso.with(getContext().getApplicationContext())
                            .load(message.getFileUrl()).into(contentView);
                }
                else
                    Picasso.with(getContext().getApplicationContext()).load(message.getFileUrl()).
                            resize((int) viewWidth, (int) viewHeight).centerCrop().into(contentView);
            }

            timeView.setText(time);

            AVQuery<AVObject> avQuery = new AVQuery<>("_User");
            avQuery.getInBackground(message.getFrom(), new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    nameView.setText(avObject.get("username").toString());
                }
            });

            steganoMsg.setVisibility(View.GONE);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(context);
                    normalDialog.setTitle("查看隐写");
                    normalDialog.setMessage("查看隐写内容?");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Map<String, Object> metaData = message.getAttrs();
                                    if(metaData!= null){
                                        if(metaData.containsKey("stegano")){
                                            if((boolean)metaData.get("stegano")){
                                                ExtractProcess ext = new ExtractProcess(getContext(), message.getFileUrl(),steganoMsg);
                                                //steganoMsg.setText(ext.LSBExtract());
                                                ext.JstegExtract_V1();
                                                //Log.d("raz ext",ext.msg);
                                                steganoMsg.setText(ext.msg);
                                                steganoMsg.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
                            });
                    normalDialog.setNegativeButton("关闭",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //...To-do
                                }
                            });
                    // 显示
                    normalDialog.show();
                }
            });
        }

    }

    public void showTimeView(boolean isShow) {
        timeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
