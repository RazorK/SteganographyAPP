package com.example.aimin.stegano.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.event.UserClickEvent;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

/**
 * Created by aimin on 2017/4/17.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {
    private TextView nameView;
    private LinearLayout friendLayout;
    private Context context;
    private ImageView avatar;

    public UserViewHolder(View itemView, Context act_context) {
        super(itemView);
        context = act_context;
        nameView = (TextView) itemView.findViewById(R.id.friend_name_text);
        friendLayout = (LinearLayout) itemView.findViewById(R.id.friend_item_layout);
        avatar = (ImageView) itemView.findViewById(R.id.friend_avatar);
    }

    public void bindData(final AVObject user) {
        Log.d("raz","in friendViewHolder getUser"+user.get("username").toString());
        nameView.setText(user.get("username").toString());

        //头像部分 TODO:继承AVUSer
        AVFile oriAvatar = user.getAVFile("avatar");
        if(oriAvatar != null){
            Picasso.with(context).load(oriAvatar.getUrl()).into(avatar);
        } else {
            avatar.setImageResource(R.drawable.default_avatar);
        }

        friendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("raz","in click User");
                UserClickEvent clickEvent = new UserClickEvent();
                clickEvent.targetID = user.getObjectId();
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
