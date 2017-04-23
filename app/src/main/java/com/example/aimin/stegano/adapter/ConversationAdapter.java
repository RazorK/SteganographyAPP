package com.example.aimin.stegano.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.viewholder.ConversationViewHolder;

import java.util.List;

/**
 * Created by aimin on 2017/4/16.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationViewHolder> {
    private Context mContext;
    private List<AVIMConversation> mList;

    public ConversationAdapter(List<AVIMConversation> list, Context context) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConversationViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_conversation_list, parent, false),mContext);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, final int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
