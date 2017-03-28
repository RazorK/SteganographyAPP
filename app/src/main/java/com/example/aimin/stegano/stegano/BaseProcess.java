package com.example.aimin.stegano.stegano;

import android.util.Log;

/**
 * Created by aimin on 2017/3/27.
 */

public class BaseProcess {

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
}
