package com.example.aimin.stegano.util;

import android.util.Log;

import org.opencv.core.Mat;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static org.opencv.core.Core.dct;
import static org.opencv.core.Core.idct;
import static org.opencv.core.CvType.CV_16SC1;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_8UC1;

/**
 * Created by aimin on 2017/5/8.
 */

public class ComplexSteganoMat {
    //初始未处理FMAT 8*8
    public Mat mat;

    //mat遍历指针
    private PixelHelper ph;

    //移位Mat
    private Mat fixedMat;

    //middle Mat 承接F DCT
    private Mat middleMat;

    //16s mat
    private Mat sMat;

    public Mat dstMat;

    private boolean debug;

    /**
     * 默认传入未处理FY 8*8 32F
     * @param mat 32F
     */
    public ComplexSteganoMat(Mat mat, boolean debugFlag) {
        this.debug = debugFlag;
        debug("in Debug Constructer");
        this.mat = mat;
        debug("insert origin mat");
        debugPrintMat(mat);
        ph = new PixelHelper(8,8);
        //此处对8*8mat 进行移位 dct 量化
        this.fixedMat = new Mat(mat.size(),CV_32FC1);

        //移位
        MatUtils.initfix(mat,fixedMat);
        debug("fixed mat");
        debugPrintMat(fixedMat);

        //dct
        this.middleMat = new Mat();
        dct(fixedMat,middleMat);
        debug("middle dct mat");
        debugPrintMat(middleMat);

        //量化
        sMat = new Mat(mat.size(),CV_16SC1);
        MatUtils.razQuan(middleMat, sMat);

        debug("quantified mat");
        printMat(sMat);
    }

    public ComplexSteganoMat(Mat mat) {
        debug = false;
        this.mat = mat;
        ph = new PixelHelper(8,8);
        //此处对8*8mat 进行移位 dct 量化
        this.fixedMat = new Mat(mat.size(),CV_32FC1);

        //移位
        MatUtils.initfix(mat,fixedMat);

        //dct
        this.middleMat = new Mat();
        dct(fixedMat,middleMat);

        //量化
        sMat = new Mat(mat.size(),CV_16SC1);
        MatUtils.razQuan(middleMat, sMat);
    }

    public boolean insertInt(int s) {
        //跳过直流分量
        if(ph.getNowWidth()==0&&ph.getNowHeight()==0){
            ph.next();
        }
        do{
            short temp =(short) sMat.get(ph.getNowHeight(),ph.getNowWidth())[0];
            if(temp == 0 || temp == 1 || temp == -1 ) {
                continue;
            }
            if((temp>0 && temp%2==0) || (temp<0 && temp%2 == -1)) {
                Log.d("raz","in InsertInt before insert");
                printMat(sMat);
                Log.d("raz","insert int:"+s);
                short []a = new short[1];
                a[0] =(short) ((temp>0 && s ==1)||(temp<0&&s==0)?temp+1:temp);
                sMat.put(ph.getNowHeight(),ph.getNowWidth(),a);
                Log.d("raz","in InsertInt after insert");
                printMat(sMat);
                return true;
            } else {
                Log.d("raz","in InsertInt before insert");
                printMat(sMat);
                Log.d("raz","insert int:"+s);
                short []a = new short[1];
                a[0] = (short)((temp>0&&s==0)||(temp<0&&s==1)?temp-1:temp);
                sMat.put(ph.getNowHeight(),ph.getNowWidth(),a);
                Log.d("raz","in InsertInt after insert");
                printMat(sMat);
                return true;
            }
        } while(ph.next());
        return false;
    }

    /**
     * 从SMAT 16S TO DSTMAT 8UC1
     */
    public void recoverMat(){
        dstMat = new Mat(sMat.size(),CV_8UC1);

        debug("recoverMat");
        debugPrintMat(sMat);
        Mat middleMat = new Mat(sMat.size(),CV_32FC1);
        sMat.convertTo(middleMat,CV_32FC1);
        debug("sMat 转换F矩阵");
        debugPrintMat(middleMat);

        Mat iQuanMat = new Mat(sMat.size(),CV_32FC1);
        MatUtils.razIQuan(middleMat,iQuanMat);
        debug("取消量化");
        debugPrintMat(iQuanMat);

        Mat subFixed = new Mat(sMat.size(),CV_32FC1);
        idct(iQuanMat,subFixed);
        debug("idct");
        debugPrintMat(subFixed);

        MatUtils.recoverFix(subFixed,dstMat);
        debug("recoverFix");
        debugPrintMat(dstMat);
    }

    public PixelHelper getPh() {
        return ph;
    }

    public ArrayList getIntArray() {
        ArrayList<Integer> a = new ArrayList<>();
        ph.resetPh();
        while(ph.next()) {
            int temp =(int) sMat.get(ph.getNowHeight(),ph.getNowWidth())[0];
            if(temp == 0 || temp == 1 || temp == -1 ) {
                continue;
            }
            printMat(sMat);
            Integer tempI = temp%2;
            a.add(abs(tempI));
            Log.d("raz","in get temp:"+abs(tempI));
        }
        return a;
    }

    private void printMat(Mat mat) {
        String dst = "get Mat in rows: "+ mat.rows() + ", cols: "+mat.cols()+"\n";
        for(int i=0;i<mat.rows();i++){
            for(int j=0 ; j <mat.cols(); j++){
                dst+=mat.get(i,j)[0]+" ";
            }
            dst += "\n";
        }
        Log.d("raz in extract",dst);
    }

    private void debugPrintMat(Mat mat){
        if(debug){
            String dst = "get Mat in rows: "+ mat.rows() + ", cols: "+mat.cols()+"\n";
            for(int i=0;i<mat.rows();i++){
                for(int j=0 ; j <mat.cols(); j++){
                    dst+=mat.get(i,j)[0]+" ";
                }
                dst += "\n";
            }
            Log.d("raz in extract",dst);
        }
    }

    private void debug(String str){
        if(debug){
            Log.d("razDebug",str);
        }
    }
}
