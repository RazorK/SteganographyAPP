package com.example.aimin.stegano.stegano;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.avos.avoscloud.AVUser;
import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.util.ComplexSteganoMat;
import com.example.aimin.stegano.util.MatUtils;
import com.example.aimin.stegano.util.PixelHelper;
import com.example.aimin.stegano.util.SteganoMatHelper;
import com.example.aimin.stegano.util.StringHelper;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.opencv.core.Core.merge;
import static org.opencv.core.Core.split;
import static org.opencv.core.CvType.CV_16SC1;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.cvtColor;

/**
 * Created by aimin on 2017/3/27.
 */

public class SteganoProcess extends BaseProcess {
    //Context
    private Context context;

    //输入图片地址
    private String oriFilePath;

    //插入信息
    private String msg;

    //输出图片地址
    private String outFilePath;

    //in && out
    private FileInputStream in;
    private FileOutputStream out;

    private File inFile;
    private File outFile;

    private String steganoId;

    private double simpleSize;

    /**
     * 构造函数
     * @param context 用于toast
     * @param path
     * @param insertMessage
     * @param sid
     * @param simplesize
     * @throws FileNotFoundException
     */
    public SteganoProcess(Context context, String path, String insertMessage, String sid, double simplesize) throws FileNotFoundException {
        this.context = context;
        this.oriFilePath = path;
        this.inFile = new File(path);
        this.msg = insertMessage;
        this.steganoId = sid;
        this.simpleSize = simplesize;

        //outfile
        this.outFilePath = Constants.getCachePath(context, AVUser.getCurrentUser().getObjectId(),this.steganoId);
        this.outFile = new File(outFilePath);
        this.out = new FileOutputStream(outFile);
    }

    public String getResultPath(){
        return outFilePath;
    }

    public void LSBProcess() throws IOException {
        msg = msg + "$";
        Log.d("raz","stegano Path"+oriFilePath);
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = (int)simpleSize;
        Bitmap bm = BitmapFactory.decodeFile(oriFilePath,bitmapOptions).copy(Bitmap.Config.ARGB_8888,true);
        PixelHelper ph = new PixelHelper(bm.getHeight(),bm.getWidth());
        Log.d("raz","getFirstPixel"+bm.getPixel(0,0));
        Log.d("raz","setMsg"+msg);
        char [] c = msg.toCharArray();
        Log.d("raz","setMsgLength"+c.length);
        for(int i=0;i<c.length;i++){
            for(int j=0;j<16;j++){
                int getbit=(c[i]>>(15-j)&0x0001);
                int temp = bm.getPixel(ph.getNowWidth(),ph.getNowHeight());
                Log.d("raz","before"+temp);
                temp = (temp & 0xfffffffe) + getbit;
                Log.d("raz","after"+temp);
                bm.setPixel(ph.getNowWidth(),ph.getNowHeight(),temp);
                ph.next();
            }
        }
        bm.compress(Bitmap.CompressFormat.PNG,100,out);
    }

    public void JstegProcess() {
        msg += "$";
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = (int)simpleSize;
        Bitmap bm = BitmapFactory.decodeFile(oriFilePath,bitmapOptions).copy(Bitmap.Config.ARGB_8888,true);

        Mat rgbMat = new Mat();
        Mat yuvMat = new Mat();
        Utils.bitmapToMat(bm, rgbMat);
        cvtColor(rgbMat, yuvMat, Imgproc.COLOR_BGR2YUV);

        //YUV split
        ArrayList<Mat> channels = new ArrayList<Mat>();
        split(yuvMat,channels);

        Mat Y = channels.get(0);

        //get FY
        Mat fY = new Mat(Y.size(),CV_32FC1);
        Y.convertTo(fY,CV_32FC1);

        Mat DCTY = new Mat(Y.size(), CV_16SC1);
        MatUtils.razDCT(fY,DCTY);

        //steganography
        stegano(DCTY,msg);

        Mat dstY = new Mat(Y.size(),CV_32FC1);
        MatUtils.razIDCT(DCTY,dstY);

        Mat intDstY = new Mat(dstY.size(),CV_8UC1);
        dstY.convertTo(intDstY,CV_8UC1);

        channels.set(0,intDstY);
        Mat finalYUV = new Mat();
        merge(channels,finalYUV);

        Mat afterRGBMat = new Mat();
        cvtColor(finalYUV, afterRGBMat, Imgproc.COLOR_YUV2BGR);

        Bitmap dstYBM = Bitmap.createBitmap(dstY.cols(),dstY.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(afterRGBMat,dstYBM);

        dstYBM.compress(Bitmap.CompressFormat.PNG,100,out);
    }

    /**
     * Jsteg隐写过程
     * @param mat 16S
     * @param msg String
     */
    private void stegano(Mat mat, String msg) {
        toast("in stegano");
        msg += "$";
        PixelHelper ph = new PixelHelper(mat.rows()/8,mat.cols()/8);
        StringHelper str = new StringHelper(msg);
        SteganoMatHelper matHelper = new SteganoMatHelper(mat.submat(ph.getNowHeight()*8, ph.getNowHeight()*8 + 8, ph.getNowWidth()*8, ph.getNowWidth()*8 + 8));
        do{
            Log.d("raz","in split Mst"+str.getNow());
            int[] c = StringHelper.CharToIntArray(str.getNow());
            for(int temp : c){
                while(!matHelper.insertInt(temp)){
                    if(!ph.next())
                        throw new java.lang.ArrayIndexOutOfBoundsException();
                    else {
                        matHelper = new SteganoMatHelper(mat.submat(ph.getNowHeight()*8, ph.getNowHeight()*8 + 8, ph.getNowWidth()*8, ph.getNowWidth()*8 + 8));
                    }
                }
                matHelper.getPh().next();
            }
        } while (str.next());
        toast("out stegano");
    }

    public void JstegProcess_V1() {
        msg += "$";
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = (int)simpleSize;
        Bitmap bm = BitmapFactory.decodeFile(oriFilePath,bitmapOptions).copy(Bitmap.Config.ARGB_8888,true);

        Mat rgbMat = new Mat();
        Mat yuvMat = new Mat();
        Utils.bitmapToMat(bm, rgbMat);
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

        razStegano(fY, intDstY, msg);

        Log.d("raz","inV1");
        Log.d("raz",intDstY.toString());
        channels.set(0, intDstY);

        Mat yuv = new Mat();
        merge(channels, yuv);

        Mat rgb = new Mat();
        cvtColor(yuv, rgb, Imgproc.COLOR_YUV2BGR);

        Bitmap dstYBM = Bitmap.createBitmap(Y.cols(), Y.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgb, dstYBM);

        dstYBM.compress(Bitmap.CompressFormat.PNG, 100, out);

        /*new SteganoAsyncTask(context) {

            @Override
            protected Bitmap doInBackground(Void... params) {
                razStegano(fY, intDstY, msg);
                channels.set(0, intDstY);

                Mat yuv = new Mat();
                merge(channels, yuv);

                Mat rgb = new Mat();
                cvtColor(yuv, rgb, Imgproc.COLOR_YUV2BGR);

                Bitmap dstYBM = Bitmap.createBitmap(Y.cols(), Y.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(rgb, dstYBM);
                return dstYBM;
            }

            @Override
            protected void onSucceed(Bitmap bm) {
                Log.d("raz","in OnSucceed");
                bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
        };*/
    }

    /**
     * 传入FY，进行DCT、量化 嵌入
     * @param mat
     * @param msg
     */
    private void razStegano(Mat mat,Mat intDstMat, String msg){
        //msg += "$";
        toast("in razStegano"+msg);
        PixelHelper ph = new PixelHelper(mat.rows()/8,mat.cols()/8);
        StringHelper str = new StringHelper(msg);
        ComplexSteganoMat matHelper = new ComplexSteganoMat(mat.submat(ph.getNowHeight()*8, ph.getNowHeight()*8 + 8, ph.getNowWidth()*8, ph.getNowWidth()*8 + 8));
        do{
            Log.d("raz","in split Mst"+str.getNow());
            int[] c = StringHelper.CharToIntArray(str.getNow());
            for(int temp : c){
                while(!matHelper.insertInt(temp)){
                    if(!ph.next())
                        throw new java.lang.ArrayIndexOutOfBoundsException();
                    else {
                        //从matHelper还原8UC1
                        matHelper.recoverMat();
                        matHelper.dstMat.convertTo(intDstMat.submat(ph.getAheadHeight()*8, ph.getAheadHeight()*8+8, ph.getAheadWidth()*8, ph.getAheadWidth()*8+8),CV_8UC1);

                        if(ph.getNowHeight()==0&&ph.getNowWidth()*8==1800){
                            Log.d("raz","in Debug");
                            matHelper = new ComplexSteganoMat(mat.submat(ph.getNowHeight()*8, ph.getNowHeight()*8 + 8, ph.getNowWidth()*8, ph.getNowWidth()*8 + 8),true);
                        }
                        else
                            matHelper = new ComplexSteganoMat(mat.submat(ph.getNowHeight()*8, ph.getNowHeight()*8 + 8, ph.getNowWidth()*8, ph.getNowWidth()*8 + 8));
                        Log.d("raz in stegano submat", ph.getNowHeight()*8+","+ph.getNowWidth()*8);
                    }
                }
                matHelper.getPh().next();
            }
        } while (str.next());
        matHelper.recoverMat();
        matHelper.dstMat.convertTo(intDstMat.submat(ph.getNowHeight()*8, ph.getNowHeight()*8+8, ph.getNowWidth()*8, ph.getNowWidth()*8+8),CV_8UC1);
    }
}
