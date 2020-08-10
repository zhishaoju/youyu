package com.youyu.gao.xiao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.bean.IncomeModel;
import java.util.ArrayList;

/**
 * @Author zhisiyi
 * @Date 2020.04.21 12:31
 * @Comment
 */
public class InComeAdapter extends Adapter {

    private Context mCtx;

    private ArrayList mData = new ArrayList<IncomeModel>();

    public InComeAdapter(Context context) {
        mCtx = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx)
            .inflate(R.layout.adapter_income_detail_item, parent, false);
        InComeViewHolder viewHolder = new InComeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final InComeViewHolder viewHolder = (InComeViewHolder) holder;
        final IncomeModel incomeModel = (IncomeModel) mData.get(position);
        viewHolder.tvActiveName.setText(incomeModel.title);
        viewHolder.tvTimeScope.setText(incomeModel.beginTime + " " + incomeModel.endTime);
        viewHolder.tvSource.setText(incomeModel.source);
        viewHolder.tvMoney.setText(incomeModel.money);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    static class InComeViewHolder extends ViewHolder {

        @BindView(R.id.tv_active_name)
        TextView tvActiveName;
        @BindView(R.id.tv_time_scope)
        TextView tvTimeScope;
        @BindView(R.id.tv_source)
        TextView tvSource;
        @BindView(R.id.tv_money)
        TextView tvMoney;

        public InComeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setData(ArrayList<IncomeModel> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void appendData(ArrayList<IncomeModel> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }
}

