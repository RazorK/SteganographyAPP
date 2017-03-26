package com.example.aimin.stegano.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.example.aimin.stegano.ClientManager;
import com.example.aimin.stegano.viewholder.CommonViewHolder;
import com.example.aimin.stegano.viewholder.LeftMessageViewHolder;
import com.example.aimin.stegano.viewholder.RightMessageViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by aimin on 2017/3/25.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_LEFT_TEXT = 0;
    private final int ITEM_RIGHT_TEXT = 1;

    // 时间间隔最小为十分钟
    private final long TIME_INTERVAL = 5 * 60 * 1000;

    private List<AVIMMessage> messageList = new ArrayList<AVIMMessage>();

    public MessageAdapter() {
    }

    public void setMessageList(List<AVIMMessage> messages) {
        messageList.clear();
        if (null != messages) {
            messageList.addAll(messages);
        }
    }

    public void addMessageList(List<AVIMMessage> messages) {
        messageList.addAll(0, messages);
    }

    public void addMessage(AVIMMessage message) {
        messageList.addAll(Arrays.asList(message));
    }

    public AVIMMessage getFirstMessage() {
        if (null != messageList && messageList.size() > 0) {
            return messageList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_LEFT_TEXT) {
            return new LeftMessageViewHolder(parent.getContext(), parent);
        } else if (viewType == ITEM_RIGHT_TEXT) {
            return new RightMessageViewHolder(parent.getContext(), parent);
        } else {
            //TODO
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CommonViewHolder<AVIMMessage>)holder).bindData(messageList.get(position));
        if (holder instanceof LeftMessageViewHolder) {
            ((LeftMessageViewHolder)holder).showTimeView(shouldShowTime(position));
        } else if (holder instanceof RightMessageViewHolder) {
            ((RightMessageViewHolder)holder).showTimeView(shouldShowTime(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        AVIMMessage message = messageList.get(position);
        if (message.getFrom().equals(ClientManager.getInstance().getClientId())) {
            return ITEM_RIGHT_TEXT;
        } else {
            return ITEM_LEFT_TEXT;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private boolean shouldShowTime(int position) {
        if (position == 0) {
            return true;
        }
        long lastTime = messageList.get(position - 1).getTimestamp();
        long curTime = messageList.get(position).getTimestamp();
        return curTime - lastTime > TIME_INTERVAL;
    }
}
