package com.example.aimin.stegano.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by aimin on 2017/3/25.
 */

public abstract class CommonViewHolder<T> extends RecyclerView.ViewHolder {
    public CommonViewHolder(Context context, ViewGroup root, int layoutRes) {
        super(LayoutInflater.from(context).inflate(layoutRes, root, false));
        ButterKnife.bind(this, itemView);
    }

    public Context getContext() {
        return itemView.getContext();
    }

    /**
     * 用给定的 data 对 holder 的 view 进行赋值
     */
    public abstract void bindData(T t);

    public void setData(T t) {
        bindData(t);
    }
}