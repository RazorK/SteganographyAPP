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

    private static String getPrefixConstant(String str) {
        return LEANMESSAGE_CONSTANTS_PREFIX + str;
    }
}
