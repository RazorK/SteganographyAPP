package com.example.aimin.stegano.util;

import android.util.Log;

/**
 * Created by aimin on 2017/5/1.
 */

public class StringHelper {
    private String raz;
    private int pointer;

    public StringHelper() {
        raz = "";
        setPointer(0);
    }

    public StringHelper(String raz) {
        this.raz = raz;
        setPointer(0);
    }

    public void setString(String raz) {
        this.raz = raz;
    }

    public String getString() {
        return raz;
    }

    public int getPointer() {
        return  pointer;
    }

    public  void setPointer(int p) {
        if(p >= raz.length()-1) {
            pointer = raz.length() -1;
        } else
            pointer = p;
    }

    public boolean isEnd() {
        if(pointer >= raz.length()-1){
            return true;
        } else
            return false;
    }

    public char getNow() {
        return raz.toCharArray()[pointer];
    }

    public boolean next() {
        if(isEnd())
            return false;
        else{
            pointer++;
            return true;
        }
    }

    public static int[] CharToIntArray(char c) {
        int[] a = new int[8];
        int temp = 0;
        for(int i=0;i<8;i++){
            temp = (c>>(7-i)&0x01);
            a[i] = temp;
        }
        Log.d("raz",a.toString());
        return a;
    }
}
