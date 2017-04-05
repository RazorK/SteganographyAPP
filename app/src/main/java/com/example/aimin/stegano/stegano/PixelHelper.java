package com.example.aimin.stegano.stegano;

/**
 * Created by aimin on 2017/4/4.
 */

public class PixelHelper {

    private int height;
    private int width;

    private int nowHeight;
    private int nowWidth;

    public PixelHelper(int length,int width){
        this.height = length-1;
        this.width = width-1;
        this.nowHeight = this.nowWidth = 0;
    }

    public int getNowHeight() {
        return nowHeight;
    }

    public int getNowWidth() {
        return nowWidth;
    }

    public boolean next(){
        if(nowWidth>=width){
            if(nowHeight>=height){
                return false;
            }
            nowWidth = 0;
            nowHeight ++;
            return true;
        } else{
            nowWidth++;
            return true;
        }
    }
}
