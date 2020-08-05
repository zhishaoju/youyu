package com.youyu.activity;

import static com.youyu.utils.Contants.Net.BASE_URL;
import static com.youyu.utils.Contants.Net.COLLECTION_LIST;
import static com.youyu.utils.Contants.Net.POST_UPDATE;
import static com.youyu.utils.Contants.PAGE_SIZE;
import static com.youyu.utils.Contants.USER_ID;

import android.os.Bundle;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.gson.Gson;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.youyu.R;
import com.youyu.adapter.VideoPlayListAdatper;
import com.youyu.adapter.VideoPlayListAdatper.OnClickListener;
import com.youyu.bean.VideoPlayerItemInfo;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.net.NetInterface.RequestResponse;
import com.youyu.utils.LogUtil;
import com.youyu.utils.SharedPrefsUtil;
import com.youyu.utils.Utils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

  @BindView(R.id.pull_to_refresh)
  PullToRefreshLayout pullToRefresh;
//  @BindView(R.id.loadstate_iv)
//  ImageView loadstateIv;
//  @BindView(R.id.loadmore_view)
//  RelativeLayout loadmoreView;
//  @BindView(R.id.refresh_view)
//  PullToRefreshLayout refreshView;


  private Unbinder mUnBinder;

  private VideoPlayListAdatper mIndexShowAdapter;
  private LinearLayoutManager lm;
  private int mPageNumer = 1;
  private int mRefresh; // =1 代表刷新；=2 代表加载更多
  private int pageSize = PAGE_SIZE;
  private ArrayList<VideoPlayerItemInfo> mData = new ArrayList<>();
  private VideoPlayerItemInfo mVideoPlayerItemInfo;
  private int mPosition = -1;
  private int mNetRequestFlag = -1;

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
    mIndexShowAdapter.setOnViewClick(new OnClickListener() {

      @Override
      public void onViewClick(int flag, VideoPlayerItemInfo v, int position) {
        mVideoPlayerItemInfo = v;
        mPosition = position;

        mNetRequestFlag = 2;
        String url = BASE_URL + POST_UPDATE;
        JSONObject jsonObject = new JSONObject();
        try {
          jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
          jsonObject.put("id", v.id);
          if (flag == 1) {
            jsonObject.put("agreeTotal", 1);
            jsonObject.put("footTotal", 0);
          } else {
            jsonObject.put("agreeTotal", 0);
            jsonObject.put("footTotal", 1);
          }
        } catch (JSONException e) {
          LogUtil.showELog(TAG, "indexFragment refresh e :" + e.getLocalizedMessage());
        }
        String param = jsonObject.toString();
        post(url, param);
      }
    });
//    refreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
//
//      @Override
//      public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
//        refresh();
//      }
//
//      @Override
//      public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
//        mPageNumer += 1;
//        loadMore(mPageNumer);
//      }
//    });

    pullToRefresh.setRefreshListener(new BaseRefreshListener() {
      @Override
      public void refresh() {
        refreshCus();
      }

      @Override
      public void loadMore() {
        mPageNumer += 1;
        loadMoreCus(mPageNumer);
      }
    });
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {
        LogUtil.showELog(TAG, "failure(Exception e) e:" + e.toString());
      }

      @Override
      public void success(String data) {
        if (mNetRequestFlag == 1) {
          parseData(data);
        }
        if (mNetRequestFlag == 2) {
          // 点赞和踩的逻辑
          LogUtil.showDLog(TAG, "parseData(String data) 点赞和踩解析数据data：" + data);
          try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject da = jsonObject.getJSONObject("data");
            int footTotal = da.getInt("footTotal");
            int agreeTotal = da.getInt("agreeTotal");
            int commentTotal = da.getInt("commentTotal");
            if (mVideoPlayerItemInfo != null) {
              mVideoPlayerItemInfo.footTotal = footTotal;
              mVideoPlayerItemInfo.agreeTotal = agreeTotal;
              mVideoPlayerItemInfo.commentTotal = commentTotal;
            }
            if (mPosition != -1) {
              mIndexShowAdapter.updateOneItem(mVideoPlayerItemInfo, mPosition);
            }
          } catch (Exception e) {
            LogUtil.showELog(TAG, "parseData(String data) 点赞和踩e：" + e);
          }
        }

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
  protected void onResume() {
    super.onResume();
    refreshCus();
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
    mData.clear();
    try {
      JSONObject jsonObject = new JSONObject(data);
      int code = Utils.jsonObjectIntGetValue(jsonObject, "code");
      if (code == 0) {
        JSONArray ja = jsonObject.getJSONArray("rows");
        if (ja != null) {
          int size = ja.length();
          for (int i = 0; i < size; i++) {
            String vpii = ja.getJSONObject(i).toString();
            VideoPlayerItemInfo videoPlayerItemInfo = new Gson()
                .fromJson(vpii, VideoPlayerItemInfo.class);
            mData.add(videoPlayerItemInfo);
          }
        }
        if (mRefresh == 1) {
          mIndexShowAdapter.updateData(mData);
          pullToRefresh.finishRefresh();
        } else if (mRefresh == 2) {
          mIndexShowAdapter.appendData(mData);
          pullToRefresh.finishLoadMore();
        }
      }
    } catch (Exception e) {
      LogUtil.showELog(TAG, "parseData(String data) catch (JSONException e)"
          + " 异常：" + e.toString());
      Utils.show("解析数据失败");
    }
  }

  private void refreshCus() {
    mRefresh = 1;
    mNetRequestFlag = 1;
    mPageNumer = 1;
    LogUtil.showDLog(TAG, "refresh()");
    String url = BASE_URL + COLLECTION_LIST;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      jsonObject.put("pageNum", mPageNumer);
      jsonObject.put("pageSize", pageSize);
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "active list refresh e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }

  private void loadMoreCus(int pageNum) {
    mNetRequestFlag = 1;
    LogUtil.showDLog(TAG, "loadMoreCus(int pageNum) pageNum = " + pageNum);
    mRefresh = 2;
    String url = BASE_URL + COLLECTION_LIST;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      jsonObject.put("pageNum", pageNum);
      jsonObject.put("pageSize", pageSize);
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "active list refresh e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }
}
