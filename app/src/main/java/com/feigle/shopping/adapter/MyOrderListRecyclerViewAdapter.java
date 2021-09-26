package com.feigle.shopping.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feigle.shopping.R;
import com.feigle.shopping.bean.OrderBean;

import java.util.List;

public class MyOrderListRecyclerViewAdapter extends RecyclerView.Adapter<MyOrderListRecyclerViewAdapter.ViewHolder> {
    private final List<OrderBean> mValues;
    private OnItemClickListener onItemClickListener;
    public static String[] status = new String[]{"待付款", "待发货", "已发货", "已退货", "已取消"};

    public MyOrderListRecyclerViewAdapter(List<OrderBean> list, OnItemClickListener onItemClickListener) {
        mValues = list;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_order, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final OrderBean orderBean = mValues.get(i);
        viewHolder.commodityTextView.setText(orderBean.getCommodity());
        viewHolder.priceTextView.setText("金额：" + orderBean.getPayment());
        viewHolder.quantityTextView.setText("数量：" + orderBean.getQuantity());
        viewHolder.orderNumberTextView.setText("订单号：" + orderBean.getOrderNumber());
        viewHolder.createTextView.setText("下单时间：" + orderBean.getCreateTime());
        viewHolder.expressTextView.setText("快递：" + orderBean.getExpressCompany());
        viewHolder.expressNumberTextView.setText("快递号：" + orderBean.getExpressNmber());
        viewHolder.statusTextView.setText("状态：" + orderBean.getStatus());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(orderBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;

        public TextView commodityTextView;
        public TextView priceTextView;
        public TextView quantityTextView;
        public TextView orderNumberTextView;
        public TextView createTextView;
        public TextView expressTextView;
        public TextView expressNumberTextView;
        public TextView statusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            commodityTextView = itemView.findViewById(R.id.commodityTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            orderNumberTextView = itemView.findViewById(R.id.orderNumberTextView);
            createTextView = itemView.findViewById(R.id.createTextView);
            expressTextView = itemView.findViewById(R.id.expressTextView);
            expressNumberTextView = itemView.findViewById(R.id.expressNumberTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(OrderBean orderBean);
    }
}
