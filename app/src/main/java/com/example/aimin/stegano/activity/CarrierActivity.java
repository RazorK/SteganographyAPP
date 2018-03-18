package com.example.aimin.stegano.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVUser;
import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.adapter.CarrierAdapter;
import com.example.aimin.stegano.db.DBConsult;
import com.example.aimin.stegano.model.CarrierItem;
import com.example.aimin.stegano.util.CarrierAsyncTast;
import com.example.aimin.stegano.util.MatUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

import static com.example.aimin.stegano.util.Utils.getImagePathFromURI;

/**
 * Created by aimin on 2017/5/14.
 */

public class CarrierActivity extends BaseActivity {

    private static final int REQUEST_IMAGE_PICK = 0;

    @Bind(R.id.carrier_list)
    protected RecyclerView carrierList;

    @Bind(R.id.add_carrier)
    protected Button addBtn;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private boolean mSelect;

    private CarrierAdapter carrierAdapter;

    private List<CarrierItem> mList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrier);

        Intent intent = getIntent();
        mSelect = intent.getBooleanExtra(Constants.CARRIER_SELECT,false);

        toolbar.setTitle("载体管理");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.common_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        carrierList.setHasFixedSize(true);
        carrierList.setLayoutManager(new LinearLayoutManager(this));
        carrierAdapter = new CarrierAdapter(mList,this,mSelect);
        carrierList.setAdapter(carrierAdapter);
        carrierList.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, null);
                photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICK);
            }
        });
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_IMAGE_PICK:
                    final String oriFilePath = getImagePathFromURI(this, data.getData());

                    final double ss = getSimpleSize(oriFilePath);
                    //创建文件 获取信息保存 刷新list

                    SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
                    final Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String time = formatter.format(curDate);

                    final String setFilePath = Constants.getCarrierPath(this, AVUser.getCurrentUser().getObjectId(),time);

                    new CarrierAsyncTast(CarrierActivity.this){
                        @Override
                        protected CarrierItem doInBackground(Void... params) {
                            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                            bitmapOptions.inSampleSize = (int)ss;
                            Bitmap bm = BitmapFactory.decodeFile(oriFilePath,bitmapOptions).copy(Bitmap.Config.ARGB_8888,true);
                            try {
                                FileOutputStream setFileStream = new FileOutputStream(setFilePath);
                                bm.compress(Bitmap.CompressFormat.PNG,100,setFileStream);
                                setFileStream.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            CarrierItem ci = new CarrierItem();
                            ci.filepath = setFilePath;
                            ci.userId = AVUser.getCurrentUser().getObjectId();
                            ci.username = AVUser.getCurrentUser().getUsername();

                            SimpleDateFormat aformatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                            String str =aformatter.format(curDate);
                            ci.inserttime = str;

                            //TODO: storage caculate
                            ci.storage = MatUtils.JstegCount(ci.filepath);

                            try {
                                FileInputStream inputStream = new FileInputStream(setFilePath);
                                ci.size=inputStream.available();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return ci;
                        }

                        @Override
                        protected void onPostExecute(CarrierItem carrierItem) {
                            super.onPostExecute(carrierItem);
                            //刷新
                            Log.d("raz",carrierItem.toString());
                            mList.add(carrierItem);
                            //carrierAdapter.notifyDataSetChanged();
                            carrierAdapter.notifyItemInserted(carrierAdapter.getItemCount());

                            //db
                            new DBConsult(CarrierActivity.this).addCarrier(carrierItem);
                        }
                    }.execute();
                default:
                    break;
            }
        }
    }

    private void initData() {
        Log.d("raz","initData");
        mList.clear();
        DBConsult dBConsolt = new DBConsult(this);
        Cursor result = dBConsolt.getUserCarrier();
        if(result.moveToFirst()){
            do{
                CarrierItem ci = new CarrierItem();
                ci.storage = result.getInt(result.getColumnIndex("storage"));
                ci.filepath = result.getString(result.getColumnIndex("filepath"));
                ci.size = result.getDouble(result.getColumnIndex("size"));
                ci.inserttime = result.getString(result.getColumnIndex("inserttime"));
                ci.datebaseId = result.getInt(result.getColumnIndex("id"));
                mList.add(ci);
            } while (result.moveToNext());
        }
        Log.d("raz","after initData"+mList.size());
    }

    private double getSimpleSize(String oriFilePath){
        double simpleSize;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(oriFilePath, options);
        if(options.outHeight>options.outWidth){
            simpleSize = options.outHeight/960;
        } else
            simpleSize = options.outWidth/960;
        if(simpleSize<1)
            simpleSize = 1;
        return simpleSize;
    }
}
