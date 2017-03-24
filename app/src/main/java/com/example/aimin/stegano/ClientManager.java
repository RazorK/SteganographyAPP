package com.example.aimin.stegano;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

/**
 * Created by aimin on 2017/3/23.
 */

public class ClientManager {
    private static ClientManager imClientManager;

    private AVIMClient avimClient;
    private String clientId;

    public synchronized static ClientManager getInstance() {
        if (null == imClientManager) {
            imClientManager = new ClientManager();
        }
        return imClientManager;
    }

    private ClientManager() {
    }

    public void open(String clientId, AVIMClientCallback callback) {
        this.clientId = clientId;
        avimClient = AVIMClient.getInstance(clientId);
        avimClient.open(callback);
    }

    public AVIMClient getClient() {
        return avimClient;
    }

    public String getClientId() {
        if (TextUtils.isEmpty(clientId)) {
            throw new IllegalStateException("Please call AVImClientManager.open first");
        }
        return clientId;
    }
}
