package com.example.aimin.stegano;

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
}
