package com.example.aimin.stegano.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.aimin.stegano.model.CarrierItem;

/**
 * Created by aimin on 2017/5/17.
 */

public abstract class CarrierAsyncTast extends AsyncTask<Void,Void,CarrierItem> {
    protected Context ctx;
    ProgressDialog dialog;
    boolean openDialog = true;
    Exception exception;

    protected CarrierAsyncTast(Context ctx) {
        this.ctx = ctx;
    }

    protected CarrierAsyncTast(Context ctx, boolean openDialog) {
        this.ctx = ctx;
        this.openDialog = openDialog;
    }

    public CarrierAsyncTast setOpenDialog(boolean openDialog) {
        this.openDialog = openDialog;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (openDialog) {
            dialog = Utils.showSpinnerDialog((Activity) ctx);
        }
    }

    @Override
    protected void onPostExecute(CarrierItem ci) {
        super.onPostExecute(ci);
        if (openDialog) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}
