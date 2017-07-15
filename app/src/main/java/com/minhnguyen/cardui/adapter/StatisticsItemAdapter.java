package com.minhnguyen.cardui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minhnguyen.cardui.R;
import com.minhnguyen.cardui.model.StatisticItem;
import com.minhnguyen.cardui.utilities.Utilities;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by minhnguyen on 2/24/17.
 */

public class StatisticsItemAdapter extends RecyclerView.Adapter<StatisticsItemAdapter.ViewHolder> {

    private List<StatisticItem> statisticItems;
    private Context mContext;

    public StatisticsItemAdapter(Context context, List<StatisticItem> statisticItems) {
        mContext = context;
        this.statisticItems = statisticItems;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_statistic, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StatisticItem statisticItem = statisticItems.get(position);

        TextView tvFullName = holder.tvFullName;
        TextView tvOrganization = holder.tvOrganization;
        TextView tvDateTime = holder.tvDateTime;

        tvFullName.setText(statisticItem.getName());
        tvOrganization.setText(statisticItem.getOrganization());
        tvDateTime.setText(Utilities.getInstance().convertDateToStringLongFormat(statisticItem.getDate()));
    }

    @Override
    public int getItemCount() {
        return statisticItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvFullName)
        TextView tvFullName;
        @BindView(R.id.tvOrganization)
        TextView tvOrganization;
        @BindView(R.id.tvDateTime)
        TextView tvDateTime;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
