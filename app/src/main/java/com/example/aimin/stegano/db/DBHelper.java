package com.example.aimin.stegano.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by aimin on 2017/3/29.
 */

public class DBHelper extends SQLiteOpenHelper {

    //steganomsg type
    private static final int STEGANO_SEND_MESSAGE = 0;
    private static final int STEGANO_RECEIVE_MESSAGE = 1;

    public static final String CREATE_BOOK = "create table user ("
            + "id integer primary key autoincrement, "
            + "leanid text, "
            + "username text) ";

    public static final String CREATE_MESSAGE = "create table steganomsg ("
            + "id integer primary key autoincrement, "
            + "userid text,"
            + "conversationid text, "
            + "leanid text, "
            + "steganoid text, "
            + "imageUrl text, "
            + "steganomsg text, "
            + "type int, "
            + "imagecachepath text)";

    public static final String CREATE_CARRIER = "create table carrier ("
            + "id integer primary key autoincrement, "
            + "userid text,"
            + "username text,"
            + "size real,"
            + "storage int,"
            + "filepath text,"
            + "inserttime text)";

    private Context mContext;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_MESSAGE);
        db.execSQL(CREATE_CARRIER);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
