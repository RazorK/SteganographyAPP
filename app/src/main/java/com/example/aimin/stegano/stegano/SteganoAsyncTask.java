package com.example.aimin.stegano.stegano;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.aimin.stegano.util.Utils;

/**
 * Created by aimin on 2017/5/8.
 */

public abstract class SteganoAsyncTask extends AsyncTask<Void,Void,Bitmap> {
    protected Context ctx;
    ProgressDialog dialog;
    boolean openDialog = true;
    Exception exception;

    protected SteganoAsyncTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (openDialog) {
            dialog = Utils.showSpinnerDialog((Activity) ctx);
        }
    }

    @Override
    protected abstract Bitmap doInBackground(Void... params);

    @Override
    protected void onPostExecute(Bitmap bm) {
        super.onPostExecute(bm);
        Log.d("raz","in onPostExecute");
        onSucceed(bm);
    }

    protected abstract void onSucceed(Bitmap bm);
}
