package com.example.aimin.stegano.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.example.aimin.stegano.manager.ClientManager;
import com.example.aimin.stegano.viewholder.CommonViewHolder;
import com.example.aimin.stegano.viewholder.LeftImageViewHolder;
import com.example.aimin.stegano.viewholder.LeftMessageViewHolder;
import com.example.aimin.stegano.viewholder.LeftSteganoViewHolder;
import com.example.aimin.stegano.viewholder.RightImageViewHolder;
import com.example.aimin.stegano.viewholder.RightMessageViewHolder;
import com.example.aimin.stegano.viewholder.RightSteganoViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by aimin on 2017/3/25.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_LEFT_TEXT = 0;
    private final int ITEM_LEFT_IMAGE = 1;
    private final int ITEM_LEFT_STEGANO = 2;

    private final int ITEM_RIGHT_TEXT = 3;
    private final int ITEM_RIGHT_IMAGE = 4;
    private final int ITEM_RIGHT_STEGANO = 5;

    private final int ITEM_TYPE_UNKNOWN = 6;


    // 时间间隔最小为十分钟
    private final long TIME_INTERVAL = 5 * 60 * 1000;

    private List<AVIMMessage> messageList = new ArrayList<AVIMMessage>();

    public MessageAdapter() {
    }

    public void setMessageList(List<AVIMMessage> messages) {
        messageList.clear();
        if (null != messages) {
            addMessageList(messages);
        }
    }

    /**
     * 添加坏消息过滤
     * @param messages
     */
    public void addMessageList(List<AVIMMessage> messages) {
        int i=0;
        for(AVIMMessage msg : messages){
            if(msg instanceof AVIMImageMessage) {
                AVIMImageMessage img = (AVIMImageMessage) msg;
                if(!img.getFileMetaData().containsKey("format")) {
                    continue;
                }
                messageList.addAll(i, Arrays.asList(msg));
            } else
                messageList.addAll(i, Arrays.asList(msg));
            i++;
        }
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
        } else if(viewType ==ITEM_RIGHT_IMAGE) {
            return new RightImageViewHolder(parent.getContext(),parent);
        } else if(viewType ==ITEM_LEFT_IMAGE) {
            return new LeftImageViewHolder(parent.getContext(),parent);
        } else if(viewType ==ITEM_LEFT_STEGANO) {
            return new LeftSteganoViewHolder(parent.getContext(),parent);
        } else if(viewType ==ITEM_RIGHT_STEGANO) {
            return new RightSteganoViewHolder(parent.getContext(),parent);
        }
        else {
            //TODO
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("raz","in binding view holder"+position);
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
            //return ITEM_RIGHT_TEXT;
            if(message instanceof AVIMTextMessage) {
                return ITEM_RIGHT_TEXT;
            } else if(message instanceof AVIMImageMessage) {
                AVIMImageMessage temp = (AVIMImageMessage) message;
                Map<String, Object> metaData = temp.getAttrs();
                if(metaData!= null){
                    if(metaData.containsKey("stegano")){
                        if((boolean)metaData.get("stegano")){
                            return ITEM_RIGHT_STEGANO;
                        }
                    }
                }
                return ITEM_RIGHT_IMAGE;
            } else {
                return ITEM_TYPE_UNKNOWN;
            }
        } else {
            if(message instanceof AVIMTextMessage) {
                return ITEM_LEFT_TEXT;
            } else if(message instanceof AVIMImageMessage) {
                AVIMImageMessage temp = (AVIMImageMessage) message;
                Map<String, Object> metaData = temp.getAttrs();
                if(metaData!= null){
                    if(metaData.containsKey("stegano")){
                        if((boolean)metaData.get("stegano")){
                            return ITEM_LEFT_STEGANO;
                        }
                    }
                }
                return ITEM_LEFT_IMAGE;
            } else
                return ITEM_TYPE_UNKNOWN;
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
