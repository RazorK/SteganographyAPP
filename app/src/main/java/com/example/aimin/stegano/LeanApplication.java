package com.example.aimin.stegano;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by aimin on 2017/3/22.
 */

public class LeanApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"UboyLKTrabwKqgzzUq3L1uVu-gzGzoHsz","8UPPz1S1EGRHEUKkvbPLqmtJ");
        AVOSCloud.setDebugLogEnabled(true);
    }
}
