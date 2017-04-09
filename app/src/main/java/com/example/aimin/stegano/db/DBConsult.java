package com.example.aimin.stegano.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.aimin.stegano.Constants;


/**
 * Created by aimin on 2017/3/29.
 * 提供处理数据库的接口
 */

public class DBConsult {

    private SQLiteDatabase db;

    public DBConsult(Context context){
        db = new DBHelper(context, Constants.DATABASE_NAME, null, 1).getWritableDatabase();
    }

    /**
     * 用于在登陆界面显示所有登陆过的用户
     * @return
     */
    public Cursor getAllLoginedUser() {
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        return cursor;
    }

    /**
     * 获取以前登陆过的用户
     * @param id
     * @param username
     * @return
     */
    public boolean tryAddLoginedUser(String id, String username){
        String [] ids = {id};
        Cursor cursor = db.query("user",null,"leanid = ?",ids,null,null,null);
        if(!cursor.moveToFirst()){
            Log.d("raz","adding to logined list" + id + username);
            ContentValues values = new ContentValues();

            values.put("leanID", id);
            values.put("username", username);
            db.insert("user", null, values); // 插入第一条数据
            values.clear();
            return true;
        }
        Log.d("raz","already exist logined user"+ id +username);
        return false;
    }

    /**
     * 绑定SteganoMsg与图片储存位置
     * 用于ChatFragment中
     * @param msg
     * @param steganoId
     */
    public void bindSteganoMsg(String cachePath, String msg, String steganoId, String userId, String conversationId){
        ContentValues values = new ContentValues();
        values.put("imagecachepath",cachePath);
        values.put("userid",userId);
        values.put("conversationid",conversationId);
        values.put("steganomsg", msg);
        values.put("steganoid",steganoId);
        db.insert("steganomsg",null,values);
        Log.d("raz","in addSteganoMsg, after insert");
        values.clear();
    }

    public void fullBindSteganoMsg(String steganoId, String msg, String cachePath, String userId, String conversationId, String leanId, String imageUrl){
        ContentValues values = new ContentValues();
        values.put("imagecachepath",cachePath);
        values.put("userid",userId);
        values.put("conversationid",conversationId);
        values.put("steganomsg", msg);
        values.put("steganoid",steganoId);
        values.put("leanid",leanId);
        values.put("imagecachepath",imageUrl);
        db.insert("steganomsg",null,values);
        Log.d("raz","in addSteganoMsg, after insert");
        values.clear();
    }

    public String getSteganoMsgBySteganoId(String steganoId){
        Cursor c = db.rawQuery("select * from steganomsg", null);
        if(c.getCount()<=0)
            return "";

        String [] ids = {steganoId};
        Cursor cs = db.query("steganomsg", null, "steganoid = ?", ids, null, null, null);
        if(cs.moveToFirst()){
            String msg = cs.getString(cs.getColumnIndex("steganomsg"));
            Log.d("raz","in getSteganoMsgByLeanId, getmsg"+msg);
            return msg;
        } else {
            return "";
        }
    }
}
