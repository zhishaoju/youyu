package com.youyu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.youyu.R;
import com.youyu.bean.ActiveItemUi;
import com.youyu.bean.ActiveModel;
import com.youyu.utils.Utils;
import java.util.ArrayList;

/**
 * @Author zhisiyi
 * @Date 2020.04.20 22:29
 * @Comment
 */
public class ActiveAdapter extends Adapter {

  private Context mCtx;
  private ArrayList<ActiveModel> mData = new ArrayList<>();

  //私有属性
  private OnItemClickListener onItemClickListener;

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
    viewHolder.tvTimeScope.setText(activeModel.beginTime + "-" + activeModel.endTime);
    viewHolder.tvCanYuRen.setText(activeModel.count);
    ActiveItemUi activeItemUi = Utils.transform(activeModel.status);
    viewHolder.tvActiveNoStart.setText(activeItemUi.stateName);
    viewHolder.tvActiveNoStart.setBackgroundResource(activeItemUi.bgValue);
    viewHolder.tvJoin.setText(activeModel.endTime);
    if (onItemClickListener != null) {
      viewHolder.itemView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          onItemClickListener.onItemClick(activeModel);
        }
      });
    }
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

  public void setData(ArrayList<ActiveModel> data) {
    mData.clear();
    mData.addAll(data);
    notifyDataSetChanged();
  }

  public void appendData(ArrayList<ActiveModel> data) {
    mData.addAll(data);
    notifyDataSetChanged();
  }

  //setter方法
  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  //回调接口
  public interface OnItemClickListener {

    void onItemClick(ActiveModel activeModel);
  }
}
