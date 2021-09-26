package com.feigle.shopping.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.feigle.shopping.R;
import com.feigle.shopping.activity.BuyActivity;
import com.feigle.shopping.bean.CommodityBean;
import com.feigle.shopping.fragment.ShoppingCartFragment.OnListFragmentInteractionListener;
import com.feigle.shopping.fragment.dummy.DummyContent.DummyItem;

import java.util.List;

import com.feigle.shopping.numberbutton.NumberButton;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyShoppingCartRecyclerViewAdapter extends RecyclerView.Adapter<MyShoppingCartRecyclerViewAdapter.ViewHolder> {

    private final List<CommodityBean> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;

    public MyShoppingCartRecyclerViewAdapter(List<CommodityBean> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shopping_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CommodityBean commodityBean = mValues.get(position);
        Glide.with(holder.mView).load(commodityBean.getImgPath()).error(R.drawable.loading_error).placeholder(R.drawable.loading2).into(holder.imageView);
        holder.commodityTextView.setText(commodityBean.getCommodityName());
        holder.priceTextView.setText("价格：" + String.valueOf(commodityBean.getPrice()));
        holder.idTextView.setText(String.valueOf(commodityBean.getId()));
        holder.inventoryTextView.setText("库存：" + String.valueOf(commodityBean.getQuantity()));
        holder.mNumberButton.setCurrentNumber(1);

        if (commodityBean.getQuantity() < 1) {
            holder.checkBox.setEnabled(false);
            holder.mNumberButton.setInventory(1);
        } else {
            holder.checkBox.setEnabled(true);
            holder.mNumberButton.setInventory(commodityBean.getQuantity()).setOnWarnListener(new NumberButton.OnWarnListener() {
                @Override
                public void onWarningForInventory(int inventory) {
                    Toast.makeText(holder.mView.getContext(), "当前库存:" + inventory, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onWarningForBuyMax(int buyMax) {
                    Toast.makeText(holder.mView.getContext(), "超过最大购买数:" + buyMax, Toast.LENGTH_SHORT).show();
                }
            });
            ;
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.mNumberButton.setEnabled(false);
                    commodityBean.setQuantity(holder.mNumberButton.getNumber());
                    mListener.onCommodityChecked(isChecked, commodityBean);
                } else {
                    holder.mNumberButton.setEnabled(true);
                    mListener.onCommodityChecked(isChecked, commodityBean);
                }
            }
        });

        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDeleteClick(String.valueOf(commodityBean.getId()));
            }
        });
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imageView;
        public final ImageView deleteImageView;
        public final TextView commodityTextView;
        public final TextView priceTextView;
        public final TextView idTextView;
        public final TextView inventoryTextView;
        public final NumberButton mNumberButton;
        public final CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageView = (ImageView) view.findViewById(R.id.imageView);
            deleteImageView = (ImageView) view.findViewById(R.id.deleteImageView);
            commodityTextView = (TextView) view.findViewById(R.id.commodityTextView);
            priceTextView = (TextView) view.findViewById(R.id.priceTextView);
            idTextView = (TextView) view.findViewById(R.id.idTextView);
            inventoryTextView = (TextView) view.findViewById(R.id.contactTextView);
            mNumberButton = (NumberButton) view.findViewById(R.id.number_button);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }
}
