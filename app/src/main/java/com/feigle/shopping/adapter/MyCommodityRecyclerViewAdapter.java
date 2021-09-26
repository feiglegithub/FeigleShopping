package com.feigle.shopping.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.feigle.shopping.R;
import com.feigle.shopping.bean.CommodityBean;
import com.feigle.shopping.fragment.CommodityFragment;
import com.feigle.shopping.fragment.dummy.DummyContent;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyContent.DummyItem} and makes a call to the
 * specified {@link CommodityFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyCommodityRecyclerViewAdapter extends RecyclerView.Adapter<MyCommodityRecyclerViewAdapter.ViewHolder> {

    private final List<CommodityBean> mValues;
    private final CommodityFragment.OnListFragmentInteractionListener mListener;

    public MyCommodityRecyclerViewAdapter(List<CommodityBean> items, CommodityFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_commodity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CommodityBean commodityBean = mValues.get(position);
        holder.mCommodityView.setText(commodityBean.getCommodityName());
        holder.mPriceView.setText("价格：" + String.valueOf(commodityBean.getPrice()));
        holder.inventoryTextView.setText("库存：" + String.valueOf(commodityBean.getQuantity()));
        holder.mIdView.setText(String.valueOf(commodityBean.getId()));

        if (commodityBean.getQuantity() < 1)
            holder.buyButton.setEnabled(false);
        else
            holder.buyButton.setEnabled(true);


        Glide.with(holder.mView).load(commodityBean.getImgPath()).error(R.drawable.loading_error).placeholder(R.drawable.loading2).into(holder.mImageView);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                }
            }
        });

        holder.addCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShoppingCartButtonClick(commodityBean);
            }
        });

        holder.buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBuyButtonClick(commodityBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCommodityView;
        public final TextView mPriceView;
        public final TextView mIdView;
        public final ImageView mImageView;
        public final Button addCartButton;
        public final Button buyButton;
        public final TextView inventoryTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCommodityView = (TextView) view.findViewById(R.id.commodityView);
            mPriceView = (TextView) view.findViewById(R.id.priceView);
            mIdView = (TextView) view.findViewById(R.id.idView);
            inventoryTextView = (TextView) view.findViewById(R.id.contactTextView);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
            addCartButton = (Button) view.findViewById(R.id.addCartButton);
            buyButton = (Button) view.findViewById(R.id.buyButton);
        }
    }

}
