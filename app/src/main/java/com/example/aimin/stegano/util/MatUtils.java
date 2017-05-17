package com.example.aimin.stegano.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import static com.example.aimin.stegano.util.Utils.toast;
import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.core.Core.dct;
import static org.opencv.core.Core.idct;
import static org.opencv.core.Core.split;
import static org.opencv.core.CvType.CV_16SC1;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.cvtColor;

/**
 * Created by aimin on 2017/5/1.
 * Mat Utils
 */

public class MatUtils {

    private static boolean logSwitch  = true;

    //Jsteg
    private static double[][] quant = {
            {16,11,10,16,24,40,51,61},
            {12,12,14,19,26,58,60,55},
            {14,13,16,25,40,57,69,56},
            {14,17,22,29,51,87,80,62},
            {18,22,37,56,68,109,103,77},
            {24,35,55,64,81,104,113,92},
            {49,64,78,87,103,121,120,101},
            {72,92,95,98,112,100,103,99}
    };

    private static void initQuantMat(Mat quantMat) {
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++)
                quantMat.put(i,j,quant[i][j]);
        }
    }

    /**
     * 需要传入CV_F32参数
     * @param ori CV_32F
     * @param dst CV_16S
     * middleMat 32F 用于计算dct 然后转换为16S
     * fixMat 32F 用于将原像素矩阵平移到0附近
     */
    public static void razDCT(Mat ori, Mat dst) {
        //toast("in razDCT");
        Mat fixMat = new Mat();
        initfix(ori,fixMat);

        Mat middleMat = new Mat();
        fixMat.copyTo(middleMat);
        middleMat.convertTo(dst,CV_16SC1);

        for(int i=0;i<ori.rows()/8;i++) {
            for(int j=0;j<ori.cols()/8;j++) {
                Mat subFix = fixMat.submat(i*8,i*8+8,j*8,j*8+8);
                Mat subMiddle = middleMat.submat(i*8,i*8+8,j*8,j*8+8);
                Mat subDst = dst.submat(i*8,i*8+8,j*8,j*8+8);
                dct(subFix,subMiddle);
                razQuan(subMiddle,subDst);
            }
        }
        //toast("out razDCT");
    }

    /**
     * 用于将ori 平移到0附近
     * @param ori 32F
     * @param fixMat 32F
     */
    public static void initfix(Mat ori, Mat fixMat) {
        ori.copyTo(fixMat);
        for(int i=0;i<ori.rows();i++) {
            for (int j = 0; j < ori.cols(); j++) {
                fixMat.put(i,j,ori.get(i,j)[0]-128);
            }
        }
    }

    /**
     *
     * @param ori 8U
     * @param dst 32F
     */
    public static void razIDCT(Mat ori, Mat dst) {
        toast("in razIDCT");

        Mat middleMat = new Mat();
        ori.convertTo(middleMat,CV_32FC1);
        Mat fixedMat = new Mat();
        middleMat.copyTo(fixedMat);

        for(int i=0;i<ori.rows()/8;i++) {
            for(int j=0;j<ori.cols()/8;j++) {
                Mat subMiddle = middleMat.submat(i*8,i*8+8,j*8,j*8+8);
                Mat subFixed = fixedMat.submat(i*8,i*8+8,j*8,j*8+8);
                Mat iQuanMat = new Mat();
                razIQuan(subMiddle,iQuanMat);
                idct(iQuanMat,subFixed);
            }
        }
        recoverFix(fixedMat,dst);
        toast("out razIDCT");
    }

    /**
     *
     * @param fix 32F
     * @param recover 32F
     */
    public static void recoverFix(Mat fix, Mat recover) {
        fix.copyTo(recover);
        for(int i=0;i<fix.rows();i++) {
            for (int j = 0; j < fix.cols(); j++) {
                recover.put(i,j,fix.get(i,j)[0]+128);
            }
        }
    }

    /**
     * 量化
     * @param ori
     * 32FC1
     * @param dst
     * 16SC1
     * Mat ori, Mat dst
     */
    public static void razQuan(Mat ori, Mat dst) {
        Mat quantMat = new Mat(8,8,CV_32FC1);
        initQuantMat(quantMat);

        Mat dstF = new Mat();
        Core.divide(ori,quantMat,dstF);

        dstF.convertTo(dst,CV_16SC1);
    }

    /**
     * 量化还原
     * @param subOri 32F
     * @param subMiddle 32F
     */
    public static void razIQuan(Mat subOri, Mat subMiddle) {
        Mat quantMat = new Mat(8,8,CV_32FC1);
        initQuantMat(quantMat);

        Core.multiply(subOri,quantMat,subMiddle);
    }
    /**
     * 以下均为输出相关
     * @param mat
     */
    public static void printMat(Mat mat) {
        if(logSwitch) {
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

    /**
     * 以下均为输出相关
     * @param mat
     */
    public static void SprintMat(Mat mat) {
            String dst = "get Mat in rows: "+ mat.rows() + ", cols: "+mat.cols()+"\n";
            for(int i=0;i<mat.rows();i++){
                for(int j=0 ; j <mat.cols(); j++){
                    dst+=mat.get(i,j)[0]+" ";
                }
                dst += "\n";
            }
            Log.d("raz",dst);
    }

    public static void printMat(Mat mat, int row, int col) {
        if(logSwitch) {
            String dst = "get Mat in rows: "+ row + ", cols: "+col+"\n";
            for(int i=0;i<=row;i++){
                for(int j=0 ; j <=col; j++){
                    dst+=mat.get(i,j)[0]+" ";
                }
                dst += "\n";
            }
            Log.d("raz",dst);
        }
    }

    public static void printMat(Mat mat,int startRow,int endRow, int startCol, int endCol) {
        if(logSwitch) {
            String dst = "get Mat in rows: "+ startRow+" to " + endRow + ", cols: "+startCol+" to " +endCol+"\n";
            for(int i=startRow;i<=endRow;i++){
                for(int j=startCol ; j <=endCol; j++) {
                    dst+=mat.get(i,j)[0]+" ";
                }
                dst += "\n";
            }
            Log.d("raz",dst);
        }
    }

    public static void log(String str){
        if(logSwitch)
            Log.d("raz",str);
    }

    public static int JstegCount(String oriPath) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(oriPath,bitmapOptions).copy(Bitmap.Config.ARGB_8888,true);

        Mat rgbMat = new Mat();
        Mat yuvMat = new Mat();
        bitmapToMat(bm, rgbMat);
        cvtColor(rgbMat, yuvMat, Imgproc.COLOR_BGR2YUV);

        //YUV split
        final ArrayList<Mat> channels = new ArrayList<Mat>();
        split(yuvMat,channels);

        final Mat Y = channels.get(0);

        //get FY
        final Mat fY = new Mat(Y.size(),CV_32FC1);
        Y.convertTo(fY,CV_32FC1);

        final Mat intDstY = new Mat(Y.size(),CV_8UC1);
        fY.convertTo(intDstY,CV_8UC1);

        Mat DCTY = new Mat(Y.size(), CV_16SC1);
        razDCT(fY,DCTY);

        return jsCount(DCTY);
    }

    private static int jsCount(Mat DCTY) {
        int count = 0;
        for(int i =0;i<DCTY.rows();i++){
            for(int j=0;j<DCTY.cols();j++){
                double get = DCTY.get(i,j)[0];
                if(get!=0&&get!=1&&get!=-1)
                    count++;
            }
        }
        return count;
    }
}
