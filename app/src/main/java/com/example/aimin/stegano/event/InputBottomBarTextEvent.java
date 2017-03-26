package com.example.aimin.stegano.event;

/**
 * Created by aimin on 2017/3/25.
 */

public class InputBottomBarTextEvent extends InputBottomBarEvent {
    /**
     * 发送的文本内容
     */
    public String sendContent;

    public InputBottomBarTextEvent(int action, String content, Object tag) {
        super(action, tag);
        sendContent = content;
    }
}
