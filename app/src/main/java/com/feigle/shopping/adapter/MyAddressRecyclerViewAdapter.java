package com.feigle.shopping.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.feigle.shopping.R;
import com.feigle.shopping.activity.EditAddressActivity;
import com.feigle.shopping.bean.AddressBean;
import com.feigle.shopping.fragment.CommodityFragment;
import com.feigle.shopping.fragment.dummy.DummyContent;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyContent.DummyItem} and makes a call to the
 * specified {@link CommodityFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAddressRecyclerViewAdapter extends RecyclerView.Adapter<MyAddressRecyclerViewAdapter.ViewHolder> {

    private final List<AddressBean> mValues;
    private OnItemClickListener onItemClickListener;

    public MyAddressRecyclerViewAdapter(List<AddressBean> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AddressBean addressBean = mValues.get(position);
        holder.idTextView.setText(addressBean.getId());
        holder.contactTextView.setText(addressBean.getContact());
        holder.phoneTextView.setText(addressBean.getPhone());
        holder.addressTextView.setText(addressBean.getAddress());
        holder.defTextView.setVisibility(Integer.parseInt(addressBean.getDef()) == 0 ? View.INVISIBLE : View.VISIBLE);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(addressBean);
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.mView.getContext(), EditAddressActivity.class);
                intent.putExtra("id",addressBean.getId());
                intent.putExtra("contact",addressBean.getContact());
                intent.putExtra("phone",addressBean.getPhone());
                intent.putExtra("address",addressBean.getAddress());
                intent.putExtra("def",addressBean.getDef());
                holder.mView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView defTextView;
        public final TextView contactTextView;
        public final TextView phoneTextView;
        public final TextView addressTextView;
        public final TextView idTextView;
        public final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            defTextView = (TextView) view.findViewById(R.id.defTextView);
            contactTextView = (TextView) view.findViewById(R.id.contactTextView);
            phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
            addressTextView = (TextView) view.findViewById(R.id.addressTextView);
            idTextView = (TextView) view.findViewById(R.id.idTextView);
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(AddressBean addressBean);
    }
}
