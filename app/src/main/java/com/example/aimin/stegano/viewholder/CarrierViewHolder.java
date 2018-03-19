package com.example.aimin.stegano.viewholder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aimin.stegano.R;
import com.example.aimin.stegano.model.CarrierItem;
import com.example.aimin.stegano.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by aimin on 2017/5/15.
 */

public class CarrierViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    private LinearLayout item_container;
    private ImageView image;
    private TextView storage;
    private TextView size;
    private TextView insertTime;
    private Button deleteBtn;
    private boolean mSelect;

    private CarrierViewHolderClicks mlistener;

    public CarrierViewHolder(View view, Context context, boolean select, CarrierViewHolderClicks listener){
        super(view);
        this.context = context;
        image = (ImageView)view.findViewById(R.id.carrier_avatar);
        storage = (TextView)view.findViewById(R.id.carrier_storage);
        size = (TextView)view.findViewById(R.id.carrier_size);
        insertTime = (TextView)view.findViewById(R.id.carrier_time);
        deleteBtn = (Button)view.findViewById(R.id.carrier_delete_btn);
        item_container = (LinearLayout)view.findViewById(R.id.carrier_item_layout);
        mlistener = listener;

        mSelect = select;
    }

    public void bindData(final CarrierItem carrierItem) {
        Log.d("raz",carrierItem.filepath);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage("file://"+carrierItem.filepath, image);
        storage.setText(Utils.bitsFormat(carrierItem.storage));
        size.setText(Utils.bytesFormat(carrierItem.size));
        insertTime.setText(carrierItem.inserttime);
        deleteBtn.setOnClickListener(this);
        deleteBtn.setTag(carrierItem);
        item_container.setOnClickListener(this);
    }

    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        // TODO: adapt Chinese
        //normalDialog.setTitle("删除载体");
        normalDialog.setTitle("Delete Carrier");
        normalDialog.setMessage("Are you sure to delete the carrier?");
        normalDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mlistener.onClickDeleteBtn((CarrierItem) deleteBtn.getTag(),getLayoutPosition());
                }
                });
        normalDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }

    @Override
    public void onClick(View v) {
        Log.d("raz",v.getId()+"in view onClick"+mSelect);
        switch (v.getId()){
            case R.id.carrier_delete_btn:
                showNormalDialog();
                break;
            case R.id.carrier_item_layout:
                Log.d("raz","in clicking carrier_item"+mSelect);
                if(mSelect)
                    mlistener.onSelectCarrier((CarrierItem) deleteBtn.getTag());
                break;
        }
    }

    public interface CarrierViewHolderClicks{
        public void onClickDeleteBtn(CarrierItem ci, int position);
        public void onSelectCarrier(CarrierItem ci);
    }
}
