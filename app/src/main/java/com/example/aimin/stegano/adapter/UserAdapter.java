package com.example.aimin.stegano.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.avos.avoscloud.AVObject;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.viewholder.UserViewHolder;

import java.util.List;

/**
 * Created by aimin on 2017/4/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder>{
    private Context mContext;
    private List<AVObject> mList;

    public UserAdapter(List<AVObject> list, Context context) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_friend_list, parent, false),mContext);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
