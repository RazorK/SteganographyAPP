package com.example.aimin.stegano.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.event.FriendClickEvent;
import com.example.aimin.stegano.model.ContactItem;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

/**
 * Created by aimin on 2017/3/25.
 */

public class FriendViewHolder extends RecyclerView.ViewHolder {

    private TextView nameView;
    private LinearLayout friendLayout;
    private Context context;
    private ImageView avatar;
    private TextView hint;

    public FriendViewHolder(View itemView, Context act_context) {
        super(itemView);
        context = act_context;
        nameView = (TextView) itemView.findViewById(R.id.friend_name_text);
        friendLayout = (LinearLayout) itemView.findViewById(R.id.friend_item_layout);
        avatar = (ImageView) itemView.findViewById(R.id.friend_avatar);
        hint = (TextView) itemView.findViewById(R.id.alpha);
    }

    public void bindData(final ContactItem contact) {
        nameView.setText(contact.user.get("username").toString());
        Log.d("raz","in friend test init"+contact.initialVisible);
        hint.setVisibility(contact.initialVisible ? View.VISIBLE : View.GONE);

        if (!TextUtils.isEmpty(contact.sortContent)) {
            hint.setText(String.valueOf(Character.toUpperCase(contact.sortContent.charAt(0))));
        } else {
            hint.setText("");
        }

        //头像部分 TODO:继承AVUSer
        AVUser user = contact.user;
        AVFile oriAvatar = user.getAVFile("avatar");
        if(oriAvatar != null){
            Picasso.with(context).load(oriAvatar.getUrl()).into(avatar);
        } else {
            avatar.setImageResource(R.drawable.default_avatar);
        }

        friendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建ChatACtivity，在其中创建会话 这里传递TargetID进去即可, 为了整理结构，这里使用Event
                FriendClickEvent clickEvent = new FriendClickEvent();
                clickEvent.targetID = contact.user.getObjectId().toString();
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
}
