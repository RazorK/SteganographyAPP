package com.example.aimin.stegano.util;

/**
 * Created by aimin on 2017/4/4.
 */

public class PixelHelper {

    private int height;
    private int width;

    private int nowHeight;
    private int nowWidth;

    public PixelHelper(int height,int width){
        this.height = height-1;
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

    public void resetPh() {
        this.nowHeight = this.nowWidth = 0;
    }

    public int getAheadHeight(){
        if(nowWidth==0)
            return nowHeight-1;
        else return nowHeight;
    }

    public int getAheadWidth(){
        if(nowWidth==0)
            return width;
        else
            return nowWidth-1;
    }
}
