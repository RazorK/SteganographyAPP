package com.example.aimin.stegano.stegano;

import android.util.Log;

import com.example.aimin.stegano.event.EmptyEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by aimin on 2017/3/27.
 */

public class BaseProcess {

    public BaseProcess() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void finalize() throws Throwable {
        EventBus.getDefault().unregister(this);
        super.finalize();
    }

    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
            toast(e.getMessage());
            return false;
        } else {
            return true;
        }
    }

    protected void toast(String str) {
        Log.d("raz Process", str);
    }

    public void onEvent(EmptyEvent event) {}
}
