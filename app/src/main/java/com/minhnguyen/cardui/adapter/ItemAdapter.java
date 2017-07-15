package com.minhnguyen.cardui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minhnguyen.cardui.R;
import com.minhnguyen.cardui.model.Item;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by minhnguyen on 2/21/17.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> mItem;
    private Context mContext;

    public ItemAdapter(Context context, List<Item> models) {
        mContext = context;
        mItem = models;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_information, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mItem.get(position);

        TextView tvKey = holder.tvKey;
        TextView tvValue = holder.tvValue;

        tvKey.setText(item.getKey());
        tvValue.setText(item.getValue());
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvKey)
        TextView tvKey;
        @BindView(R.id.tvValue)
        TextView tvValue;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
