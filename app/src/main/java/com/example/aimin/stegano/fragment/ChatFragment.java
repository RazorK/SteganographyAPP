package com.example.aimin.stegano.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.adapter.MessageAdapter;
import com.example.aimin.stegano.event.InputBottomBarEvent;
import com.example.aimin.stegano.event.InputBottomBarTextEvent;
import com.example.aimin.stegano.event.TypedMessageEvent;
import com.example.aimin.stegano.layout.InputBar;

import java.io.IOException;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by aimin on 2017/3/25.
 */

public class ChatFragment extends Fragment {
    //Result Intent Flag
    private static final int REQUEST_IMAGE_PICK = 2;

    private AVIMConversation mConversation;
    protected MessageAdapter itemAdapter;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;
    protected SwipeRefreshLayout refreshLayout;
    protected InputBar inputBottomBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_rv_chat);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_chat_srl_pullrefresh);
        refreshLayout.setEnabled(false);
        inputBottomBar = (InputBar) view.findViewById(R.id.fragment_chat_inputbottombar);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        itemAdapter = new MessageAdapter();
        recyclerView.setAdapter(itemAdapter);

        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AVIMMessage message = itemAdapter.getFirstMessage();
                if (null != mConversation) {
                    mConversation.queryMessages(message.getMessageId(), message.getTimestamp(), 20, new AVIMMessagesQueryCallback() {
                        @Override
                        public void done(List<AVIMMessage> list, AVIMException e) {
                            refreshLayout.setRefreshing(false);
                            if (filterException(e)) {
                                if (null != list && list.size() > 0) {
                                    itemAdapter.addMessageList(list);
                                    itemAdapter.notifyDataSetChanged();
                                    layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                                }
                            }
                        }
                    });
                } else {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        /*if (null != imConversation) {
            NotificationUtils.addTag(imConversation.getConversationId());
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        /*NotificationUtils.removeTag(imConversation.getConversationId());*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 处理图像选取Activity返回图像后的过程。
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_IMAGE_PICK:
                    Log.d("raz","get Picture url, "+data.getData());
                    sendImage(getRealPathFromURI(getActivity(), data.getData()));
                    break;
                default:
                    break;
            }
        }
    }

    public void setConversation(AVIMConversation conversation) {
        if (null != conversation) {
            mConversation = conversation;
            refreshLayout.setEnabled(true);
            //inputBottomBar.setTag(mConversation.getConversationId());
            fetchMessages();
            //NotificationUtils.addTag(conversation.getConversationId());
        }
    }

    /**
     * 根据 Uri 获取文件所在的位置
     *
     * @param context
     * @param contentUri
     * @return
     */
    private String getRealPathFromURI(Context context, Uri contentUri) {
        if (contentUri.getScheme().equals("file")) {
            return contentUri.getEncodedPath();
        } else {
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                if (null != cursor) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    return cursor.getString(column_index);
                } else {
                    return "";
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    /**
     * 发送图片消息
     * TODO 隐写加入
     *
     * @param imagePath
     */
    protected void sendImage(String imagePath) {
        try {
            AVIMImageMessage picture = new AVIMImageMessage(imagePath);
            sendMessage(picture);
        } catch (IOException e) {
            Log.e("raz image send error",e.getMessage());
        }
    }

    /**
     * 拉取消息，必须加入 conversation 后才能拉取消息
     */
    private void fetchMessages() {
        if (null != mConversation) {
            mConversation.queryMessages(new AVIMMessagesQueryCallback() {
                @Override
                public void done(List<AVIMMessage> list, AVIMException e) {
                    if (filterException(e)) {
                        itemAdapter.setMessageList(list);
                        recyclerView.setAdapter(itemAdapter);
                        itemAdapter.notifyDataSetChanged();
                        scrollToBottom();
                    }
                }
            });
        }
    }

    /**
     * 输入事件处理，接收后构造成 AVIMTextMessage 然后发送
     * 因为不排除某些特殊情况会受到其他页面过来的无效消息，所以此处加了 tag 判断
     */
    public void onEvent(InputBottomBarTextEvent textEvent) {
        if (null != mConversation && null != textEvent) {
            if (!TextUtils.isEmpty(textEvent.sendContent)) {
                AVIMTextMessage message = new AVIMTextMessage();
                message.setText(textEvent.sendContent);
                itemAdapter.addMessage(message);
                itemAdapter.notifyDataSetChanged();
                scrollToBottom();
                mConversation.sendMessage(message, new AVIMConversationCallback() {
                    @Override
                    public void done(AVIMException e) {
                        if(filterException(e)) {
                            itemAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }

    /**
     * 处理所有未继承的底部输入框点击事件
     * @param event
     */

    public void onEvent(InputBottomBarEvent event) {
        if (null != mConversation && null != event) {
            switch (event.eventAction){
                case InputBottomBarEvent.INPUTBOTTOMBAR_ADD_ACTION:
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, null);
                    photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICK);
                    break;
                default:
                    LogUtil.log.e("InputBottomBarEvent unknown event type");
            }
        }
    }

    /**
     * 处理推送过来的消息
     * 同理，避免无效消息，此处加了 conversation id 判断
     */
    public void onEvent(TypedMessageEvent event) {
        if (null != mConversation && null != event &&
                mConversation.getConversationId().equals(event.conversation.getConversationId())) {
            itemAdapter.addMessage(event.message);
            itemAdapter.notifyDataSetChanged();
            scrollToBottom();
        }
    }

    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(AVIMMessage message, boolean addToList) {
        Log.d("raz sendmsg", message.getContent());
        if (addToList) {
            itemAdapter.addMessage(message);
        }
        itemAdapter.notifyDataSetChanged();
        scrollToBottom();
        mConversation.sendMessage(message, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                itemAdapter.notifyDataSetChanged();
                if (null != e) {
                    Log.e("raz sendMessage error",e.getMessage());
                }
            }
        });
    }

    public void sendMessage(AVIMMessage message) {
        sendMessage(message, true);
    }

    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(itemAdapter.getItemCount() - 1, 0);
    }

    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
            Log.e("raz", "filterException: "+e.getStackTrace());
            toast(e.getMessage());
            return false;
        } else {
            return true;
        }
    }

    protected void toast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }
}
