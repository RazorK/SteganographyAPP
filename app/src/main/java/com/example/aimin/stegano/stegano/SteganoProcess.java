package com.example.aimin.stegano.stegano;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.avos.avoscloud.AVUser;
import com.example.aimin.stegano.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
}
