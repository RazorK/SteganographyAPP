package com.example.aimin.stegano.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.example.aimin.stegano.R;

/**
 * Created by aimin on 2017/4/9.
 */

public class SearchFriendViewHolder extends RecyclerView.ViewHolder {
    private TextView nameView;
    private LinearLayout friendLayout;
    private Context context;

    public SearchFriendViewHolder(View itemView, Context act_context) {
        super(itemView);
        context = act_context;
        nameView = (TextView) itemView.findViewById(R.id.friend_name_text);
        friendLayout = (LinearLayout) itemView.findViewById(R.id.friend_item_layout);
    }

    public void bindData(final AVObject user) {
        nameView.setText(user.get("username").toString());
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
