package com.example.aimin.stegano.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.example.aimin.stegano.R;

import java.text.DateFormat;
import java.util.Locale;

import butterknife.Bind;

/**
 * Created by aimin on 2017/3/25.
 */

public class LeftMessageViewHolder extends CommonViewHolder{
    @Bind(R.id.chat_left_text_tv_time)
    protected TextView timeView;

    @Bind(R.id.chat_left_text_tv_content)
    protected TextView contentView;

    @Bind(R.id.chat_left_text_tv_name)
    protected TextView nameView;

    public LeftMessageViewHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.left_message_layout);
    }

    @Override
    public void bindData(Object o) {
        AVIMMessage message = (AVIMMessage)o;
        int style = DateFormat.MEDIUM;
        DateFormat df;
        df = DateFormat.getDateInstance(style, Locale.US);
        // System.out.println("USA: " + df.format(date));
        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = df.format(message.getTimestamp());

        // TODO: add Chinese support
        String content =  "Sorry, the message type is not supported now.";
        if (message instanceof AVIMTextMessage) {
            content = ((AVIMTextMessage)message).getText();
        }

        contentView.setText(content);
        timeView.setText(time);

        AVQuery<AVObject> avQuery = new AVQuery<>("_User");
        avQuery.getInBackground(message.getFrom(), new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                nameView.setText(avObject.get("username").toString());
            }
        });
    }

    public void showTimeView(boolean isShow) {
        timeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
