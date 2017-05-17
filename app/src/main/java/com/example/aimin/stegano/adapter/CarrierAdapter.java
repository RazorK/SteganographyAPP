package com.example.aimin.stegano.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.aimin.stegano.Constants;
import com.example.aimin.stegano.R;
import com.example.aimin.stegano.db.DBConsult;
import com.example.aimin.stegano.model.CarrierItem;
import com.example.aimin.stegano.viewholder.CarrierViewHolder;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by aimin on 2017/5/15.
 */

public class CarrierAdapter extends RecyclerView.Adapter<CarrierViewHolder> {
    private Activity mContext;
    private List<CarrierItem> mList;
    private boolean mSelect;

    public CarrierAdapter(List<CarrierItem> list, Activity context, boolean select) {
        this.mContext = context;
        this.mList = list;
        mSelect = select;
    }

    @Override
    public CarrierViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CarrierViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_carrier_item, parent, false), mContext, mSelect, new CarrierViewHolder.CarrierViewHolderClicks() {
            @Override
            public void onClickDeleteBtn(CarrierItem ci, int position) {
                mList.remove(position);
                CarrierAdapter.this.notifyItemRemoved(position);

                new DBConsult(mContext).deleteCarrier(ci);
            }

            @Override
            public void onSelectCarrier(CarrierItem ci) {
                Intent intent = new Intent();
                intent.putExtra(Constants.CARRIER_SELECT_ITEM, ci.filepath);
                mContext.setResult(RESULT_OK, intent);
                mContext.finish();
            }
        });
    }

    @Override
    public void onBindViewHolder(CarrierViewHolder holder, final int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
