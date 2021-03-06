package com.youyu.gao.xiao.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.activity.VideoDetailActivity;
import com.youyu.gao.xiao.bean.VideoPlayerItemInfo;
import com.youyu.gao.xiao.utils.Utils;
import com.youyu.gao.xiao.view.CircleImageView;
import com.youyu.gao.xiao.view.VideoPlayer;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================ Copyright：JackChan和他的朋友们有限公司版权所有 (c)
 * 2017 Author：   JackChan Email：    815712739@qq.com GitHub：   https://github.com/JackChan1999
 * GitBook：  https://www.gitbook.com/@alleniverson CSDN博客： http://blog.csdn.net/axi295309066 个人博客：
 * https://jackchan1999.github.io/ 微博：     AndroidDeveloper
 * <p>
 * Project_Name：VideoPlayer Package_Name：com.jackchan.videoplayer Version：1.0 time：2017/5/24 18:05
 * des ：adapter gitVersion：2.12.0.windows.1 updateAuthor：AllenIverson updateDate：2017/5/24 18:05
 * updateDes：${TODO} ============================================================
 */

public class VideoPlayListAdatper extends Adapter {

  private Context context;
  private List<VideoPlayerItemInfo> videoPlayerItemInfoList = new ArrayList<>();

  //记录之前播放的条目下标
  public int currentPosition = -1;

  public VideoPlayListAdatper(Context context, List<VideoPlayerItemInfo> videoPlayerItemInfoList) {
    this.context = context;
    this.videoPlayerItemInfoList = videoPlayerItemInfoList;
  }

  public VideoPlayListAdatper(Context context) {
    this.context = context;
  }

  private OnClickListener mOnClickListener;

  public void setOnViewClick(OnClickListener l) {
    mOnClickListener = l;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context)
        .inflate(R.layout.adapter_view_item_info, parent, false);
    ListViewHolder viewHolder = new ListViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final ListViewHolder viewHolder = (ListViewHolder) holder;
    //获取到条目对应的数据
    VideoPlayerItemInfo info = videoPlayerItemInfoList.get(position);
    //传递给条目里面的MyVideoPlayer
    viewHolder.videoPlayer.setPlayData(info);
    //把条目的下标传递给MyVideoMediaController对象
    viewHolder.videoPlayer.mediaController.setPosition(position);
    //把Adapter对象传递给MyVideoMediaController对象
    viewHolder.videoPlayer.mediaController.setAdapter(this);
    if (position != currentPosition) {
      //设置为初始化状态
      viewHolder.videoPlayer.initViewDisplay(info.duration);
    }
    viewHolder.tvZan.setText(info.agreeTotal + "");
    viewHolder.tvComment.setText(info.commentTotal + "");
    viewHolder.tvCai.setText(info.footTotal + "");
    viewHolder.tvTitle.setText(info.title);
    Glide.with(context)
        .load(info.coverImage)
        .into(viewHolder.ivBg);

    Glide.with(context)
        .load(info.avatarUrl)
        .placeholder(R.drawable.qq_allshare_normal)
        .into(viewHolder.ivAuthor);

    viewHolder.tvAuthorName.setText(info.userName + "");

    viewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        // 把Video id 传过去
        intent.putExtra("postId", info.id);
        context.startActivity(intent);
      }
    });

    viewHolder.flItem.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        // 把Video id 传过去
        intent.putExtra("postId", info.id);
        context.startActivity(intent);
        Utils.show("点击item.");
        return true;
      }
    });

//    viewHolder.flItem.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        Intent intent = new Intent(context, VideoDetailActivity.class);
//        // 把Video id 传过去
//        intent.putExtra("postId", info.id);
//        context.startActivity(intent);
//      }
//    });

    viewHolder.ivZan.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mOnClickListener.onViewClick(1, info, position);
      }
    });

    viewHolder.ivCai.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mOnClickListener.onViewClick(2, info, position);
      }
    });

  }


  @Override
  public int getItemCount() {
    return videoPlayerItemInfoList != null ? videoPlayerItemInfoList.size() : 0;
  }

  public void setPlayPosition(int position) {
    currentPosition = position;
  }


  static class ListViewHolder extends ViewHolder {

    @BindView(R.id.civ_author)
    CircleImageView ivAuthor;
    @BindView(R.id.tv_author_name)
    TextView tvAuthorName;
    @BindView(R.id.iv_comment_more)
    ImageView ivCommentMore;
    @BindView(R.id.fl_item)
    FrameLayout flItem;
    @BindView(R.id.iv_bg)
    ImageView ivBg;
    @BindView(R.id.videoPlayer)
    VideoPlayer videoPlayer;
    @BindView(R.id.iv_zan)
    ImageView ivZan;
    @BindView(R.id.tv_zan)
    TextView tvZan;
    @BindView(R.id.iv_cai)
    ImageView ivCai;
    @BindView(R.id.tv_cai)
    TextView tvCai;
    @BindView(R.id.iv_comment)
    ImageView ivComment;
    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.tv_fenxiang)
    TextView tvFenxiang;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    ListViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }

  public Context getContext() {
    return context;
  }

  public void updateOneItem(VideoPlayerItemInfo videoPlayerItemInfo, int position) {
    VideoPlayerItemInfo v = videoPlayerItemInfoList.get(position);
    v = videoPlayerItemInfo;
    notifyItemChanged(position);
  }

  public void updateData(ArrayList<VideoPlayerItemInfo> data) {
    videoPlayerItemInfoList.clear();
    videoPlayerItemInfoList.addAll(data);
    notifyDataSetChanged();
  }

  public void appendData(ArrayList<VideoPlayerItemInfo> data) {
    videoPlayerItemInfoList.addAll(data);
    notifyDataSetChanged();
  }

  public int getSize() {
    return videoPlayerItemInfoList.size();
  }

  //回调接口
  public interface OnClickListener {

    // flag = 1 赞； flag = 2 踩
    void onViewClick(int flag, VideoPlayerItemInfo videoPlayerItemInfo, int position);
  }
}
