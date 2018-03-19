package com.example.aimin.stegano.viewholder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.db.DBConsult;
import com.example.aimin.stegano.stegano.ExtractProcess;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by aimin on 2017/5/8.
 */

public class RightSteganoViewHolder extends CommonViewHolder {
    @Bind(R.id.chat_right_text_tv_time)
    protected TextView timeView;

    @Bind(R.id.chat_right_image_content)
    protected ImageView contentView;

    @Bind(R.id.chat_right_text_tv_name)
    protected TextView nameView;

    @Bind(R.id.chat_right_text_layout_status)
    protected FrameLayout statusView;

    @Bind(R.id.chat_right_text_progressbar)
    protected ProgressBar loadingBar;

    @Bind(R.id.chat_right_text_tv_error)
    protected ImageView errorView;

    @Bind(R.id.chat_right_stegano_msg)
    protected TextView steganoMsg;

    private static final int MAX_DEFAULT_HEIGHT = 400;
    private static final int MAX_DEFAULT_WIDTH = 300;

    private AVIMImageMessage message;

    private Context context;

    public RightSteganoViewHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.right_image_layout);
        this.context = context;
        Log.d("raz","in vh init");
        steganoMsg.setVisibility(View.GONE);
    }

    /**
     * TODO: resend
     @OnClick(R.id.chat_right_text_tv_error)
     public void onErrorClick(View view) {
     ImTypeMessageResendEvent event = new ImTypeMessageResendEvent();
     event.message = message;
     EventBus.getDefault().post(event);
     }
     */

    @Override
    public void bindData(Object o) {
        message = (AVIMImageMessage)o;
        if(message instanceof AVIMImageMessage) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//            String time = dateFormat.format(message.getTimestamp());
            int style = DateFormat.MEDIUM;
            DateFormat df;
            df = DateFormat.getDateInstance(style, Locale.US);
            String time = df.format(message.getTimestamp());

            String localFilePath = message.getLocalFilePath();
            Log.d("raz", "in raz getlocalFilePath"+localFilePath);

            // 图片的真实高度与宽度
            double actualHight = message.getHeight();
            double actualWidth = message.getWidth();

            double viewHeight = MAX_DEFAULT_HEIGHT;
            double viewWidth = MAX_DEFAULT_WIDTH;

            Constants.HW hw = Constants.resize(actualHight, actualWidth, viewHeight,viewWidth);
            viewHeight = hw.height;
            viewWidth = hw.width;

            if (!TextUtils.isEmpty(message.getFileUrl())) {
                if(message.getFileMetaData().get("format")!= null && message.getFileMetaData().get("format").toString().equals("image/bmp")) {
                    Picasso.with(getContext().getApplicationContext())
                            .load(message.getFileUrl()).into(contentView);
                }
                else
                    Picasso.with(getContext().getApplicationContext()).load(message.getFileUrl()).
                            resize((int) viewWidth, (int) viewHeight).centerCrop().into(contentView);
            } else if(!TextUtils.isEmpty(localFilePath)) {
                if(localFilePath.substring(localFilePath.length()-3, localFilePath.length()).equals("bmp"))
                    Picasso.with(getContext().getApplicationContext()).
                            load(new File(localFilePath)).into(contentView);
                else
                    Picasso.with(getContext().getApplicationContext()).load(new File(localFilePath)).
                            resize((int) viewWidth, (int) viewHeight).centerCrop().into(contentView);
            } else
                contentView.setImageResource(0);

            timeView.setText(time);

            //getUsername
            //TODO: 一次获取存储
            AVQuery<AVObject> avQuery = new AVQuery<>("_User");
            avQuery.getInBackground(message.getFrom(), new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    nameView.setText(avObject.get("username").toString());
                }
            });

            if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed == message.getMessageStatus()) {
                Log.d("resend", "resend1");
                errorView.setVisibility(View.VISIBLE);
                loadingBar.setVisibility(View.GONE);
                statusView.setVisibility(View.VISIBLE);
            } else if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSending == message.getMessageStatus()) {
                Log.d("resend", "resend2");
                errorView.setVisibility(View.GONE);
                loadingBar.setVisibility(View.VISIBLE);
                statusView.setVisibility(View.VISIBLE);
            } else {
                statusView.setVisibility(View.GONE);
            }

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
                                    //获取MSG
                                    //情形1:发送传来msg 无leanid/imagurl
                                    //情形2:刷新从前的消息，包含(leanid/imageurl)
                                    //情形3:异地登陆，无法找到相关数据库
                                    //TODO: 加入Userid检测
                                    //TODO： 加入完备储存
                                    Map<String, Object> metaData = message.getAttrs();
                                    if(metaData!=null && metaData.containsKey("stegano") && (boolean)metaData.get("stegano")) {
                                        if (metaData.containsKey("steganoId") && !metaData.get("steganoId").toString().equals("")) {

                                            String sid = metaData.get("steganoId").toString();

                                            //获取数据库中信息
                                            String msg = new DBConsult(getContext()).getSteganoMsgBySteganoId(sid);

                                            //没查到或者没有记录，都要保存
                                            if (!msg.equals("")) {
                                                //TODO：不确定要不要保存url/leanid
                                                steganoMsg.setText(msg);
                                                steganoMsg.setVisibility(View.VISIBLE);
                                            } else {
                                                //todo:保存信息
                                                String steMsg = getExtractMsg(message.getFileUrl());
                                                steganoMsg.setText(steMsg);

                                                //TODO：先做数据缓存，之后做图片缓存
                                                bindSteganoMsg(sid,steMsg,Constants.getCachePath(getContext(),AVUser.getCurrentUser().getObjectId(),sid),message.getConversationId(),
                                                        message.getMessageId(),message.getFileUrl());
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

    private String getExtractMsg(String url){
        //TODO： 存入数据库
        ExtractProcess ext = new ExtractProcess(getContext(), message.getFileUrl(),steganoMsg);
        steganoMsg.setVisibility(View.VISIBLE);
        return ext.JstegExtract_V1();
    }

    //完备bind函数，包含全部信息
    private void bindSteganoMsg(String steganoId, String msg, String cachePath,
                                String conversationId,String leanId, String imageurl) {
        String userId = AVUser.getCurrentUser().getObjectId();
        new DBConsult(getContext()).fullBindSteganoMsg(steganoId,msg,cachePath, userId,conversationId,leanId,imageurl);
    }
}
