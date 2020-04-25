package com.youyu.adapter;

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
import com.youyu.R;
import com.youyu.bean.ActiveModel;
import java.util.ArrayList;

/**
 * @Author zhisiyi
 * @Date 2020.04.20 22:29
 * @Comment
 */
public class ActiveAdapter extends Adapter {

  private Context mCtx;
  private ArrayList<ActiveModel> mData = new ArrayList<>();

  public ActiveAdapter(Context context) {
    mCtx = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mCtx)
        .inflate(R.layout.adapter_active_item, parent, false);
    ActiveViewHolder viewHolder = new ActiveViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    final ActiveViewHolder viewHolder = (ActiveViewHolder) holder;
    //获取到条目对应的数据
    ActiveModel activeModel = mData.get(position);
    viewHolder.tvActiveName.setText(activeModel.title);
    viewHolder.tvTimeScope.setText(activeModel.beginTime+"-"+activeModel.endTime);
    viewHolder.tvCanYuRen

  }

  @Override
  public int getItemCount() {
    return mData != null ? mData.size() : 0;
  }

  static class ActiveViewHolder extends ViewHolder {

    @BindView(R.id.tv_join)
    TextView tvJoin;
    @BindView(R.id.tv_active_name)
    TextView tvActiveName;
    @BindView(R.id.tv_active_no_start)
    TextView tvActiveNoStart;
    @BindView(R.id.tv_time_scope)
    TextView tvTimeScope;
    @BindView(R.id.tv_can_yu_ren)
    TextView tvCanYuRen;

    ActiveViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
