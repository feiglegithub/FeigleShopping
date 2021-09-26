package com.feigle.shopping.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.feigle.shopping.R;
import com.feigle.shopping.bean.CommodityBean;

import java.util.List;

public class MyBuyListRecyclerViewAdapter extends RecyclerView.Adapter<MyBuyListRecyclerViewAdapter.ViewHolder> {
    private final List<CommodityBean> mValues;

    public MyBuyListRecyclerViewAdapter(List<CommodityBean> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_buy_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CommodityBean commodityBean = mValues.get(i);
        Glide.with(viewHolder.mView).load(commodityBean.getImgPath()).error(R.drawable.loading_error).placeholder(R.drawable.loading2).into(viewHolder.imageView);
        viewHolder.commodityTextView.setText(commodityBean.getCommodityName());
        viewHolder.quantityTextView.setText("数量：" + commodityBean.getQuantity());
        viewHolder.priceTextView.setText("单价：" + commodityBean.getPrice());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public ImageView imageView;
        public TextView commodityTextView;
        public TextView priceTextView;
        public TextView quantityTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            imageView = mView.findViewById(R.id.imageView);
            commodityTextView = mView.findViewById(R.id.commodityTextView);
            priceTextView = mView.findViewById(R.id.priceTextView);
            quantityTextView = mView.findViewById(R.id.quantityTextView);
        }
    }
}
