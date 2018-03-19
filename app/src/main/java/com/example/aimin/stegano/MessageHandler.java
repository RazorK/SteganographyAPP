package com.example.aimin.stegano;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.example.aimin.stegano.event.TypedMessageEvent;
import com.example.aimin.stegano.manager.ClientManager;

import de.greenrobot.event.EventBus;

/**
 * Created by aimin on 2017/3/23.
 */

public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

    private Context context;

    public MessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        Log.d("raz","receiveMSG");
        String clientID = "";
        try {
            clientID = ClientManager.getInstance().getClientId();
            if (client.getClientId().equals(clientID)) {

                // 过滤掉自己发的消息
                if (!message.getFrom().equals(clientID)) {
                    sendEvent(message, conversation);
                }
            } else {
                client.close(null);
            }
        } catch (IllegalStateException e) {
            client.close(null);
        }
    }

    /**
     * chat fragment 中接受
     * @param msg
     * @param conv
     */
    private void sendEvent(AVIMTypedMessage msg, AVIMConversation conv){
        Log.d("raz","sendingEvent");
        Log.d("raz", msg.toString());
        TypedMessageEvent event = new TypedMessageEvent();
        event.message = msg;
        event.conversation = conv;
        EventBus.getDefault().post(event);
    }
}
