package com.example.aimin.stegano.util;

import android.util.Log;

import org.opencv.core.Mat;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by aimin on 2017/5/1.
 */

public class SteganoMatHelper {
    public Mat mat;
    private PixelHelper ph;

    /**
     * 默认传入8*8 mat
     * @param mat
     */
    public SteganoMatHelper(Mat mat) {
        this.mat = mat;
        ph = new PixelHelper(mat.rows(),mat.cols());
    }

    public boolean insertInt(int s) {
        //跳过直流分量
        MatUtils.log("in insertting Int"+s);
        if(ph.getNowWidth()==0&&ph.getNowHeight()==0){
            ph.next();
        }

        do{
            short temp =(short) mat.get(ph.getNowHeight(),ph.getNowWidth())[0];
            if(temp == 0 || temp == 1 || temp == -1 ) {
                continue;
            }
            if((temp>0 && temp%2==0) || (temp<0 && temp%2 == -1)) {
                MatUtils.log("before");
                printMat(mat);
                short []a = new short[1];
                a[0] =(short) ((temp>0 && s ==1)||(temp<0&&s==0)?temp+1:temp);
                mat.put(ph.getNowHeight(),ph.getNowWidth(),a);
                MatUtils.log("after");
                printMat(mat);
                return true;
            } else {
                MatUtils.log("before");
                printMat(mat);
                short []a = new short[1];
                a[0] = (short)((temp>0&&s==0)||(temp<0&&s==1)?temp-1:temp);
                mat.put(ph.getNowHeight(),ph.getNowWidth(),a);
                MatUtils.log("after");
                printMat(mat);
                return true;
            }
        } while(ph.next());
        return false;
    }

    public ArrayList getIntArray() {
        ArrayList<Integer> a = new ArrayList<>();
        ph.resetPh();
        while(ph.next()) {
            int temp =(int) mat.get(ph.getNowHeight(),ph.getNowWidth())[0];
            if(temp == 0 || temp == 1 || temp == -1 ) {
                continue;
            }
            Integer tempI = temp%2;
            a.add(abs(tempI));
            MatUtils.log("in getIntArray get tempI"+ abs(tempI));
        }
        return a;
    }

    public PixelHelper getPh() {
        return ph;
    }

    private void printMat(Mat mat) {
        String dst = "get Mat in rows: "+ mat.rows() + ", cols: "+mat.cols()+"\n";
        for(int i=0;i<mat.rows();i++){
            for(int j=0 ; j <mat.cols(); j++){
                dst+=mat.get(i,j)[0]+" ";
            }
            dst += "\n";
        }
        Log.d("raz",dst);
    }
}
