package com.youyu.gao.xiao.adapter;

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
import com.bumptech.glide.Glide;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.bean.RecordListBean;
import com.youyu.gao.xiao.view.CircleImageView;
import java.util.ArrayList;

/**
 * @Author zhisiyi
 * @Date 2020.06.21 14:17
 * @Comment
 */
public class RecordListAdapter extends Adapter {

  private Context mCtx;
  private ArrayList<RecordListBean> mData = new ArrayList<>();

  //私有属性
  private OnItemClickListener onItemClickListener;

  public RecordListAdapter(Context context) {
    mCtx = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mCtx)
        .inflate(R.layout.adapter_active_detail_item, parent, false);
    RecordListHolder viewHolder = new RecordListHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    final RecordListHolder viewHolder = (RecordListHolder) holder;
    //获取到条目对应的数据
    RecordListBean activeModel = mData.get(position);
    viewHolder.tvUserName.setText(activeModel.nickName);
    viewHolder.tvTime.setText(activeModel.finisherEndTime);
    viewHolder.tvMoney.setText(activeModel.finisherIncome + "");
    Glide.with(mCtx)
        .asBitmap()//只加载静态图片，如果是git图片则只加载第一帧。
        .load(activeModel.imgUrl)
        .placeholder(R.mipmap.ic_launcher_round)
        .error(R.mipmap.ic_launcher_round)
        .into(viewHolder.civHeadPic);

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

  static class RecordListHolder extends ViewHolder {

    @BindView(R.id.civ_head_pic)
    CircleImageView civHeadPic;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_money)
    TextView tvMoney;

    RecordListHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }

  public void setData(ArrayList<RecordListBean> data) {
    mData.clear();
    mData.addAll(data);
    notifyDataSetChanged();
  }

  public void appendData(ArrayList<RecordListBean> data) {
    mData.addAll(data);
    notifyDataSetChanged();
  }

  //setter方法
  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  //回调接口
  public interface OnItemClickListener {

    void onItemClick(RecordListBean recordListBean);
  }
}
