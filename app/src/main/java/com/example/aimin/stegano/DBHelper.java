package com.example.aimin.stegano;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by aimin on 2017/3/29.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String CREATE_BOOK = "create table user ("
            + "id integer primary key autoincrement, "
            + "leanid text, "
            + "username text) ";

    public static final String CREATE_MESSAGE = "create table steganomsg ("
            + "id integer primary key autoincrement, "
            + "leanid text, "
            + "steganoid text,"
            + "imageUrl text,"
            + "steganomsg text,"
            + "imagecachepath text)";

    private Context mContext;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_MESSAGE);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
