package com.example.aimin.stegano.util;

import android.content.Context;

/**
 * Created by aimin on 2017/4/17.
 */

public abstract class SimpleNetTask extends NetAsyncTask {
    protected SimpleNetTask(Context cxt) {
        super(cxt);
    }

    protected SimpleNetTask(Context cxt, boolean openDialog) {
        super(cxt, openDialog);
    }


    @Override
    protected void onPost(Exception e) {
        if (e != null) {
            e.printStackTrace();
            Utils.toast(e.getMessage());
            //Utils.toast(ctx, R.string.pleaseCheckNetwork);
        } else {
            onSucceed();
        }
    }

    protected abstract void doInBack() throws Exception;

    protected abstract void onSucceed();
}
