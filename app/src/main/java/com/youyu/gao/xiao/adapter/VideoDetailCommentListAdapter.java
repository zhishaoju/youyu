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
import com.youyu.gao.xiao.bean.CommentBean;
import com.youyu.gao.xiao.view.CircleImageView;
import java.util.ArrayList;

/**
 * @Author zhisiyi
 * @Date 2020.06.07 20:19
 * @Comment
 */
public class VideoDetailCommentListAdapter extends Adapter {

  private Context mCtx;
  private ArrayList<CommentBean> mData = new ArrayList<>();

  //私有属性
  private OnItemClickListener onItemClickListener;

  public VideoDetailCommentListAdapter(Context context) {
    mCtx = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mCtx)
        .inflate(R.layout.adapter_video_detail_comment_item, parent, false);
    CommentListHolder viewHolder = new CommentListHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    final CommentListHolder viewHolder = (CommentListHolder) holder;
    //获取到条目对应的数据
    CommentBean commentBean = mData.get(position);
    Glide.with(mCtx)
        .asBitmap()//只加载静态图片，如果是git图片则只加载第一帧。
        .load(commentBean.logo)
        .placeholder(R.mipmap.ic_launcher_round)
        .error(R.mipmap.ic_launcher_round)
        .into(viewHolder.civHeadPic);
    viewHolder.tvUserName.setText("" + commentBean.fromName);
    viewHolder.tvTime.setText("" + commentBean.addTime);
    viewHolder.tvComment.setText("" + commentBean.comment);

    if (onItemClickListener != null) {
      viewHolder.itemView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          onItemClickListener.onItemClick(commentBean);
        }
      });
    }
  }

  @Override
  public int getItemCount() {
    return mData != null ? mData.size() : 0;
  }

  static class CommentListHolder extends ViewHolder {

    @BindView(R.id.civ_head_pic)
    CircleImageView civHeadPic;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_zan)
    TextView tvZan;
    @BindView(R.id.tv_cai)
    TextView tvCai;
    @BindView(R.id.tv_comment)
    TextView tvComment;

    CommentListHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }

  public void updateData(ArrayList<CommentBean> data) {
    mData.clear();
    mData.addAll(data);
    notifyDataSetChanged();
  }

  public void appendData(ArrayList<CommentBean> data) {
    mData.addAll(data);
    notifyDataSetChanged();
  }

  //setter方法
  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  //回调接口
  public interface OnItemClickListener {

    void onItemClick(CommentBean commentBean);
  }

}
