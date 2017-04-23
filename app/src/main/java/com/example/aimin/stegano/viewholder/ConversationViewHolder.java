package com.example.aimin.stegano.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.event.FriendClickEvent;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by aimin on 2017/4/16.
 */

public class ConversationViewHolder extends RecyclerView.ViewHolder {
    private TextView contentView;
    private TextView timeView;
    private ImageView avatarImg;
    private TextView nameView;
    private Context context;
    private LinearLayout convLayout;

    public ConversationViewHolder(View itemView, Context act_context) {
        super(itemView);
        context = act_context;
        nameView = (TextView) itemView.findViewById(R.id.conversation_name);
        convLayout = (LinearLayout) itemView.findViewById(R.id.conversation_item_layout);
        contentView = (TextView) itemView.findViewById(R.id.last_msg);
        timeView = (TextView) itemView.findViewById(R.id.last_time);
        avatarImg = (ImageView) itemView.findViewById(R.id.conversation_avatar);
    }

    public void bindData(final AVIMConversation conversation) {
        final AVIMConversation target_conv = conversation;
        String target = "";
        for(String userId : conversation.getMembers()){
            if(AVUser.getCurrentUser().getObjectId().equals(userId))
                continue;
            target = userId;
        }
        AVQuery<AVUser> query = new AVQuery<>("_User");
        query.whereEqualTo("objectId", target);
        query.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if(list.size() > 0){
                    AVUser targetUser = list.get(0);
                    contentHandle(targetUser, target_conv);
                }
            }
        });

        final String targetId = target;
        convLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendClickEvent clickEvent = new FriendClickEvent();
                clickEvent.targetID = targetId;
                EventBus.getDefault().post(clickEvent);
            }
        });
    }

    protected void toast(String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
            toast(e.getMessage());
            return false;
        } else {
            return true;
        }
    }

    private void contentHandle(AVUser targetUser,AVIMConversation target_conv) {
        if(targetUser!=null) {
            nameView.setText(targetUser.getUsername());
            AVIMMessage msg = target_conv.getLastMessage();
            //avatar
            AVFile oriAvatar = targetUser.getAVFile("avatar");
            if (oriAvatar != null) {
                Picasso.with(context).load(oriAvatar.getUrl()).into(avatarImg);
            } else {
                avatarImg.setImageResource(R.drawable.default_avatar);
            }
        }

        AVIMMessage msg = target_conv.getLastMessage();
        if(msg!=null) {
            //时间戳
            //TODO: 时间格式调整
            SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
            Date d1=new Date(msg.getTimestamp());
            String t1=format.format(d1);
            timeView.setText(t1);

            //近聊
            if(msg instanceof AVIMTextMessage){
                contentView.setText(((AVIMTextMessage) msg).getText());
            } else if(msg instanceof AVIMImageMessage) {
                contentView.setText("[图片]");
            }
        }
    }
}
