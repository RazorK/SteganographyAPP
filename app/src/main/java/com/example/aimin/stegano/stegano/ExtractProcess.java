package com.example.aimin.stegano.stegano;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.aimin.stegano.util.ComplexSteganoMat;
import com.example.aimin.stegano.util.MatUtils;
import com.example.aimin.stegano.util.PixelHelper;
import com.example.aimin.stegano.util.SteganoMatHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import static com.example.aimin.stegano.util.MatUtils.razDCT;
import static org.opencv.core.Core.split;
import static org.opencv.core.CvType.CV_16SC1;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.imgproc.Imgproc.cvtColor;

/**
 * Created by aimin on 2017/3/27.
 */

public class ExtractProcess extends BaseProcess {
    private Context context;
    private String imageUrl;
    private TextView text;
    public String msg;

    public ExtractProcess(Context context, String path, TextView text) {
        this.context = context;
        this.imageUrl = path;
        this.text = text;
    }

    /**
     *
     * @return
     */
    public String LSBExtract() {
        msg = "";
        final ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.loadImage(imageUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                PixelHelper ph = new PixelHelper(loadedImage.getHeight(),loadedImage.getWidth());
                int key = 0;
                int temp = 0;
                char tempchar;
                msg="";
                boolean flag = true;
                do {
                    for(int i=0;i<16;i++){
                        key=key*2;
                        temp=loadedImage.getPixel(ph.getNowWidth(),ph.getNowHeight());
                        Log.d("raz","get"+temp);
                        temp=temp%2;
                        if(temp==-1)
                            temp = 1;
                        key+=temp;
                        flag = ph.next();
                    }
                    tempchar=(char)key;
                    msg=msg+tempchar;
                    /*if(msg.length()>=50){
                        flag = false;
                    }*/
                    Log.d("raz","nowMsg"+msg);
                } while (tempchar!='$' && flag);
                Log.d("raz",msg);
                text.setText(msg);
            }
        });
        return msg;
    }

    public String JstegExtract() {
        msg = "";
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(imageUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                Mat recRGB = new Mat();
                Mat recYUV = new Mat();
                Utils.bitmapToMat(loadedImage, recRGB);
                cvtColor(recRGB, recYUV, Imgproc.COLOR_BGR2YUV);

                ArrayList<Mat> recChannels = new ArrayList<Mat>();
                split(recYUV,recChannels);

                Mat recY = recChannels.get(0);

                Mat recFY = new Mat(recY.size(),CV_32FC1);
                recY.convertTo(recFY, CV_32FC1);

                Mat recDctY = new Mat(recY.size(),CV_16SC1);
                razDCT(recFY,recDctY);
                msg += extract(recDctY);
                text.setText(msg);
            }
        });
        return msg;
    }

    /**
     * Jsteg 提取过程
     * @param mat
     */

    private String extract(Mat mat) {
        toast("in extract");
        String msg = "";
        PixelHelper ph = new PixelHelper(mat.rows()/8,mat.cols()/8);
        boolean msgFlag = true;
        ArrayList<Integer> a = new ArrayList<>();
        do{
            SteganoMatHelper matHelper = new SteganoMatHelper(mat.submat(ph.getNowHeight()*8, ph.getNowHeight()*8 + 8, ph.getNowWidth()*8, ph.getNowWidth()*8 + 8));
            MatUtils.printMat(mat.submat(ph.getNowHeight()*8, ph.getNowHeight()*8 + 8, ph.getNowWidth()*8, ph.getNowWidth()*8 + 8));
            a.addAll(matHelper.getIntArray());
            while (a.size()>=8) {
                int key = 0;
                int temp = 0;
                for(int i=0;i<8;i++) {
                    key=key*2;
                    temp=a.get(0);
                    a.remove(0);
                    key+=temp;
                }
                char tempChar = (char) key;
                msg+=tempChar;
                toast("getChar"+tempChar);
                if(tempChar=='$'||msg.length()>=50){
                    msgFlag = false;
                }
            }
        } while (ph.next() && msgFlag);

        toast("out extract");
        return msg;
    }

    public String JstegExtract_V1() {
        msg = "";
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(imageUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                Mat recRGB = new Mat();
                Mat recYUV = new Mat();
                Utils.bitmapToMat(loadedImage, recRGB);
                cvtColor(recRGB, recYUV, Imgproc.COLOR_BGR2YUV);

                ArrayList<Mat> recChannels = new ArrayList<Mat>();
                split(recYUV,recChannels);

                Mat recY = recChannels.get(0);

                Mat recFY = new Mat(recY.size(),CV_32FC1);
                recY.convertTo(recFY, CV_32FC1);

                Log.d("raz","inV1");
                MatUtils.printMat(recFY,20,20);
                String recMsg = razExtract(recFY);
                text.setText(recMsg);
            }
        });
        return msg;
    }

    /**
     * 传入
     * @param mat fU
     * @return
     */
    private String razExtract(Mat mat) {
        String msg = "";
        PixelHelper ph = new PixelHelper(mat.rows()/8,mat.cols()/8);
        boolean msgFlag = true;
        ArrayList<Integer> a = new ArrayList<>();
        do{
            boolean flag = false;
            if(ph.getNowHeight()==0&&ph.getNowWidth()*8==1800)
                flag = true;
            ComplexSteganoMat matHelper = new ComplexSteganoMat(mat.submat(ph.getNowHeight()*8, ph.getNowHeight()*8 + 8, ph.getNowWidth()*8, ph.getNowWidth()*8 + 8),flag);
            Log.d("raz in extract","go into "+ph.getNowHeight()*8+","+ph.getNowWidth()*8);
            a.addAll(matHelper.getIntArray());
            while (a.size()>=8) {
                int key = 0;
                int temp = 0;
                for(int i=0;i<8;i++) {
                    key=key*2;
                    temp=a.get(0);
                    a.remove(0);
                    key+=temp;
                }
                char tempChar = (char) key;
                msg+=tempChar;
                toast("getChar"+tempChar);
                toast("nowMsg"+msg);
                if(tempChar=='$'||msg.length()>=50){
                    msgFlag = false;
                    break;
                }
            }
        } while (msgFlag && ph.next());

        toast("out extract");
        return msg;
    }
}
