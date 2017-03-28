package com.example.aimin.stegano.stegano;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public SteganoProcess(Context context, String path, String insertMessage) {
        this.context = context;
        this.oriFilePath = path;
        this.inFile = new File(path);
        this.msg = insertMessage;
    }

    public String getResultPath(){
        return outFilePath;
    }

    public String LSBProcess() {
        char[] charArray = msg.toCharArray();
        try {
            in = new FileInputStream(inFile);
            SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            out = context.openFileOutput(str+".bmp",Context.MODE_PRIVATE);
            outFilePath = context.getFilesDir()+"/"+str+".bmp";
            toast(outFilePath);


            int temp=in.read();
            int i,j,k,asc,flag,inserter = 0;
            flag = 0;
            k = 0;

            while(temp!=-1){
                if(flag == 0 && k >= 54)//前53个字节跳过
                //if(flag==0)
                {

                    for(j=0;j<charArray.length;j++)
                    {
                        if(charArray[j]=='$')
                        {
                            flag=1;
                        }
                        for(i=0;i<8;i++)
                        {
                            inserter=(charArray[j]>>(7-i)&0x01);
                            temp=inserter+(temp&0xfe);
                            //Log.d("raz",temp+"");
                            out.write(temp);
                            temp=in.read();
                            k++;
                        }
                    }
                } else
                {
                    out.write(temp);
                    k++;
                    temp=in.read();
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return getResultPath();
    }
}
