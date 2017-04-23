package com.example.aimin.stegano.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.aimin.stegano.R;
import com.example.aimin.stegano.friend.AddRequest;
import com.example.aimin.stegano.viewholder.AddRequestViewHolder;

import java.util.List;

/**
 * Created by aimin on 2017/4/17.
 */

public class AddRequestAdapter extends RecyclerView.Adapter<AddRequestViewHolder> {
    private Context mContext;
    private List<AddRequest> mList;

    public AddRequestAdapter(List<AddRequest> list, Context context) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public AddRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AddRequestViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_add_request_list, parent, false),mContext);
    }

    @Override
    public void onBindViewHolder(AddRequestViewHolder holder, final int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
