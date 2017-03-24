package com.example.aimin.stegano.event;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;

/**
 * Created by aimin on 2017/3/24.
 */

public class TypedMessageEvent {
    public AVIMTypedMessage message;
    public AVIMConversation conversation;
}
