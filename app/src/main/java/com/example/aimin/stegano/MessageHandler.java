package com.example.aimin.stegano;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.example.aimin.stegano.event.TypedMessageEvent;

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
        if(message instanceof AVIMTextMessage){
            Log.d("Tom & Jerry",((AVIMTextMessage)message).getText());
            sendEvent(message,conversation);
        }
    }

    private void sendEvent(AVIMTypedMessage msg, AVIMConversation conv){
        TypedMessageEvent event = new TypedMessageEvent();
        event.message = msg;
        event.conversation = conv;
        EventBus.getDefault().post(event);
    }
}
