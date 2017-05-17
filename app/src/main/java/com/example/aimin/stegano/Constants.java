package com.example.aimin.stegano;

import android.content.Context;

import java.io.File;

/**
 * Created by aimin on 2017/3/25.
 */

public class Constants {
    private static final String LEANMESSAGE_CONSTANTS_PREFIX = "com.example.aimin.Stegano";
    public static final String MEMBER_ID = getPrefixConstant("member_id");

    public static final String IMAGE_URL = getPrefixConstant("imageurl");
    public static final String IMAGE_LOCAL_PATH = getPrefixConstant("image_local_path");

    public static final String IMAGE_HEIGHT = getPrefixConstant("image_height");
    public static final String IMAGE_WIDTH = getPrefixConstant("image_width");

    public static final String STEGANO_SETIMAGE_PATH = getPrefixConstant("stegano_setimage_path");
    public static final String STEGANO_MESSAGE = getPrefixConstant("stegano_message");
    public static final String STEGANO_ID = getPrefixConstant("stegano_id");

    public static final String TARGET_USER_ID = getPrefixConstant("target_user_id");

    public static final String CARRIER_SELECT = getPrefixConstant("carrier_select");
    public static final String CARRIER_SELECT_ITEM = getPrefixConstant("carrier_select_item");

    public static final String DATABASE_NAME  = "Stegano.db";

    private static String getPrefixConstant(String str) {
        return LEANMESSAGE_CONSTANTS_PREFIX + str;
    }

    //图片宽高设置
    public static HW resize(double actualHeight, double actualWidth, double viewHeight, double viewWidth) {
        if (0 != actualHeight && 0 != actualWidth) {
            // 要保证图片的长宽比不变
            double ratio = actualHeight / actualWidth;
            if (ratio > viewHeight / viewWidth) {
                viewHeight = (actualHeight > viewHeight ? viewHeight : actualHeight);
                viewWidth = viewHeight / ratio;
            } else {
                viewWidth = (actualWidth > viewWidth ? viewWidth : actualWidth);
                viewHeight = viewWidth * ratio;
            }
            return  new HW(viewHeight,viewWidth);
        } else
            return new HW(viewHeight,viewWidth);
    }

    public static class HW {
        public double height;
        public double width;
        public HW(double h, double w){
            this.height = h;
            this.width = w;
        }
    }

    //设置隐写图像解析时缓存地址
    public static String getCachePath(Context activity , String userId, String steganoId){
        String fileDir = activity.getFilesDir().toString();
        return fileDir+"/"+userId+"/"+steganoId;
    }

    //设置载体图片保存地址
    public static String getCarrierPath(Context activity , String userId, String time){
        String fileDir = activity.getFilesDir().toString();
        return fileDir+"/"+userId+"_"+"carrier"+"_"+time+".png";
    }


    public static void createCacheFolder(Context activity , String userId){
        String fileDir = activity.getFilesDir().toString();
        File folder = new File(fileDir+"/"+userId);
        if(!folder.exists()){
            folder.mkdirs();
        }
    }
}
