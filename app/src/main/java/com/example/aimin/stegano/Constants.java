package com.example.aimin.stegano;

/**
 * Created by aimin on 2017/3/25.
 */

public class Constants {
    private static final String LEANMESSAGE_CONSTANTS_PREFIX = "com.example.aimin.Stegano";
    public static final String MEMBER_ID = getPrefixConstant("member_id");

    private static String getPrefixConstant(String str) {
        return LEANMESSAGE_CONSTANTS_PREFIX + str;
    }
}
