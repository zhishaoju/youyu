//package com.youyu.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//import com.youyu.R;
//import com.youyu.bean.VideoPlayerItemInfo;
//import com.youyu.view.VideoPlayer;
//import java.util.ArrayList;
//
///**
// * @Author zhisiyi
// * @Date 2020.04.18 14:13
// * @Comment
// */
//public class IndexShowAdapter extends BaseAdapter {
//
//  private final String TAG = IndexShowAdapter.class.getSimpleName();
//
//  private ArrayList<VideoPlayerItemInfo> mData = new ArrayList<>();
//  private Context mContext;
//
//  //记录之前播放的条目下标
//  public int currentPosition = -1;
//
//  public IndexShowAdapter(Context context) {
//    mContext = context;
//  }
//
//  @Override
//  public int getCount() {
//    return mData.size();
//  }
//
//  @Override
//  public Object getItem(int position) {
//    return mData.get(position);
//  }
//
//  @Override
//  public long getItemId(int position) {
//    return position;
//  }
//
//  @Override
//  public View getView(int position, View convertView, ViewGroup parent) {
//    Holder holder = null;
//    if (convertView == null) {
//
//      holder = new Holder();
//      convertView = LayoutInflater.from(mContext)
//          .inflate(R.layout.adapter_view_item_info, parent, false);
//      holder.iv_bg = (ImageView) convertView.findViewById(R.id.iv_bg);
//      holder.videoPlayer = (VideoPlayer) convertView.findViewById(R.id.videoPlayer);
//      holder.iv_author = (ImageView) convertView.findViewById(R.id.iv_author);
//      holder.tv_author_name = (TextView) convertView.findViewById(R.id.tv_author_name);
//      holder.tv_play_count = (TextView) convertView.findViewById(R.id.tv_play_count);
//      holder.iv_comment = (ImageView) convertView.findViewById(R.id.iv_comment);
//      holder.tv_comment_count = (TextView) convertView.findViewById(R.id.tv_comment_count);
//      holder.iv_comment_more = (ImageView) convertView.findViewById(R.id.iv_comment_more);
//
//      convertView.setTag(holder);
//    } else {
//      holder = (Holder) convertView.getTag();
//    }
//
//    //获取到条目对应的数据
//    VideoPlayerItemInfo info = mData.get(position);
//    //传递给条目里面的MyVideoPlayer
//    holder.videoPlayer.setPlayData(info);
//    //把条目的下标传递给MyVideoMediaController对象
//    holder.videoPlayer.mediaController.setPosition(position);
//    //把Adapter对象传递给MyVideoMediaController对象
//    holder.videoPlayer.mediaController.setAdapter(this);
//    if(position != currentPosition){
//      //设置为初始化状态
//      holder.videoPlayer.initViewDisplay();
//    }
//
//    return convertView;
//  }
//
//  private static class Holder {
//
//    ImageView iv_bg;
//    VideoPlayer videoPlayer;
//    ImageView iv_author;
//    TextView tv_author_name;
//    TextView tv_play_count;
//    ImageView iv_comment;
//    TextView tv_comment_count;
//    ImageView iv_comment_more;
//  }
//
//  public void setData(ArrayList<VideoPlayerItemInfo> data) {
//    mData.clear();
//    mData.addAll(data);
//    notifyDataSetChanged();
//  }
//
//  public void appendMoreData(ArrayList<VideoPlayerItemInfo> data) {
//    mData.addAll(data);
//    notifyDataSetChanged();
//  }
//
//  public void setPlayPosition(int position) {
//    currentPosition = position;
//  }
//}
