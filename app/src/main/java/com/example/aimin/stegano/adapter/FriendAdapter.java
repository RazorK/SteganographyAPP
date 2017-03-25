package com.example.aimin.stegano.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.avos.avoscloud.AVObject;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.viewholder.FriendViewHolder;

import java.util.List;

/**
 * Created by aimin on 2017/3/25.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {
    private Context mContext;
    private List<AVObject> mList;

    public FriendAdapter(List<AVObject> list, Context context) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FriendViewHolder(LayoutInflater.from(mContext).inflate(R.layout.friend_list_item, parent, false),mContext);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, final int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
