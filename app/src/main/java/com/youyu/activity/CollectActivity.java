package com.youyu.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.youyu.R;
import com.youyu.adapter.VideoPlayListAdatper;
import com.youyu.bean.VideoPlayerItemInfo;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.cusListview.PullToRefreshLayout;
import com.youyu.net.NetInterface.RequestResponse;
import com.youyu.utils.LogUtil;
import java.util.ArrayList;

/**
 * @Author zhisiyi
 * @Date 2020.05.03 19:05
 * @Comment
 */
public class CollectActivity extends BaseActivity {

  private String TAG = "CollectActivity";

  @BindView(R.id.tv_title)
  TextView tvTitle;
  @BindView(R.id.content_view)
  CusRecycleView contentView;
  @BindView(R.id.loadstate_iv)
  ImageView loadstateIv;
  @BindView(R.id.loadmore_view)
  RelativeLayout loadmoreView;
  @BindView(R.id.refresh_view)
  PullToRefreshLayout refreshView;
  private Unbinder mUnBinder;

  private VideoPlayListAdatper mIndexShowAdapter;
  private LinearLayoutManager lm;
  private int mPageNumer = 1;
  private int mRefresh; // =1 代表刷新；=2 代表加载更多

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_collect);
    mUnBinder = ButterKnife.bind(this);

    initRecyclerView();
    initValue();
    initListener();
  }

  private void initListener() {
    refreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {

      @Override
      public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        refresh();
      }

      @Override
      public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPageNumer += 1;
        loadMore(mPageNumer);
      }
    });
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {
        LogUtil.showELog(TAG, "failure(Exception e) e:" + e.toString());
//        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
      }

      @Override
      public void success(String data) {
        parseData(data);
      }
    });
  }

  private void initValue() {
    tvTitle.setText("我的收藏");
//网络视频路径
    String url = "http://ips.ifeng.com/video19.ifeng.com/video09/2017/05/24/4664192-102-008-1012.mp4";

    //数据的初始化
//    ArrayList data = new ArrayList<VideoPlayerItemInfo>();
//    for (int i = 0; i < 20; i++) {
//      data.add(new VideoPlayerItemInfo(i, url));
//    }

//    mIndexShowAdapter.setData(data);
    mIndexShowAdapter = new VideoPlayListAdatper(this);
    contentView.setAdapter(mIndexShowAdapter);
  }

  private void initRecyclerView() {
    lm = new LinearLayoutManager(this);
    contentView.setLayoutManager(lm);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mUnBinder.unbind();
  }

  @OnClick(R.id.fl_back)
  public void onViewClicked() {
    finish();
  }

  private void parseData(String data) {
    LogUtil.showELog(TAG, "parseData(String data) 解析数据data：" + data);
  }

  private void refresh() {
    LogUtil.showDLog(TAG, "refresh()");
//    post(url, jsonObject.toString());
  }

  private void loadMore(int pageNum) {
    LogUtil.showDLog(TAG, "loadMore(int pageNum) pageNum = " + pageNum);
  }
}
