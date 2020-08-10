package com.youyu.gao.xiao.fragment;


import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.POST_COMMENT_LIST;
import static com.youyu.gao.xiao.utils.Contants.Net.POST_UPDATE;
import static com.youyu.gao.xiao.utils.Contants.PAGE_SIZE;
import static com.youyu.gao.xiao.utils.Contants.USER_ID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.gson.Gson;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.adapter.VideoPlayListAdatper;
import com.youyu.gao.xiao.adapter.VideoPlayListAdatper.OnClickListener;
import com.youyu.gao.xiao.bean.VideoPlayerItemInfo;
import com.youyu.gao.xiao.cusListview.CusRecycleView;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhishaoju
 */
public class IndexFragment extends BaseFragment {

  private static final String TAG = IndexFragment.class.getSimpleName();

  @BindView(R.id.content_view)
  CusRecycleView contentView;
  @BindView(R.id.pull_ro_refresh)
  PullToRefreshLayout pullToRefreshLayout;

  private int mPageNumer = 1;
  private int mRefresh; // =1 代表刷新；=2 代表加载更多
  private int pageSize = PAGE_SIZE;

  private int mTotal;

  private Unbinder mUnbinder;

  private VideoPlayListAdatper mIndexShowAdapter;
  private LinearLayoutManager lm;
  private ArrayList<VideoPlayerItemInfo> mData = new ArrayList<>();
  private VideoPlayerItemInfo mVideoPlayerItemInfo;
  private int mPosition = -1;

  private int mNetRequestFlag = -1;

  private boolean mIsFirst = true;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_index, container, false);
    mUnbinder = ButterKnife.bind(this, view);
    initRecyclerView();
    initValue();
    initListener();
    return view;
  }

  private void initRecyclerView() {
    //初始化RecyclerView
    lm = new LinearLayoutManager(getActivity());
    contentView.setLayoutManager(lm);

    // 添加分割线
    // rv.addItemDecoration(new RecycleViewDivider(this,LinearLayoutManager.HORIZONTAL,1, Color.BLACK));

//    adapter = new VideoPlayListAdatper(this, videoPlayerItemInfoList);
//    rv.setAdapter(adapter);
//    //设置滑动监听
//    rv.addOnScrollListener(onScrollListener);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private void initValue() {
//    mIndexShowAdapter = new IndexShowAdapter(getActivity());

    //网络视频路径
//    String url = "http://ips.ifeng.com/video19.ifeng.com/video09/2017/05/24/4664192-102-008-1012.mp4";
//
//    //数据的初始化
//    ArrayList data = new ArrayList<VideoPlayerItemInfo>();
//    for (int i = 0; i < 20; i++) {
//      data.add(new VideoPlayerItemInfo(i, url));
//    }

//    mIndexShowAdapter.setData(data);
    mIndexShowAdapter = new VideoPlayListAdatper(getActivity());
    contentView.setAdapter(mIndexShowAdapter);
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    LogUtil.showELog(TAG, "show isVisibleToUser = " + isVisibleToUser);
    if (isVisibleToUser) {
      // 请求网络
      if (mIsFirst) {
        refreshCus();
        mIsFirst = false;
      }
    }
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    LogUtil.showELog(TAG, "show hidden = " + hidden);
    if (!hidden) {
      // 请求网络展示界面
      if (mIsFirst) {
        refreshCus();
        mIsFirst = false;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    LogUtil.showELog(TAG, "show onResume");
    // 第一次进来的时候，会走到这里而不走onHiddenChanged
    // 请求网络展示界面
    if (mIsFirst) {
      refreshCus();
      mIsFirst = false;
    }
  }

  private void refreshCus() {
    mNetRequestFlag = 1;
    mRefresh = 1;
    mPageNumer = 1;
    LogUtil.showDLog(TAG, "refreshCus()");
    LogUtil.showDLog(TAG, "refreshCus() mPageNumer = " + mPageNumer);
    String url = BASE_URL + POST_COMMENT_LIST;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      jsonObject.put("pageNum", mPageNumer);
      jsonObject.put("pageSize", pageSize);
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "indexFragment refresh e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }

  private void loadMoreCus(int pageNum) {
    mNetRequestFlag = 1;
    LogUtil.showDLog(TAG, "loadMoreCus(int pageNum) pageNum = " + pageNum);
    mRefresh = 2;
    String url = BASE_URL + POST_COMMENT_LIST;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      jsonObject.put("pageNum", pageNum);
      jsonObject.put("pageSize", pageSize);
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "indexFragment refresh e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }


  private void initListener() {
    // flag = 1 赞； flag = 2 踩
    mIndexShowAdapter.setOnViewClick(new OnClickListener() {
      @Override
      public void onViewClick(int flag, VideoPlayerItemInfo videoPlayerItemInfo, int position) {
        mVideoPlayerItemInfo = videoPlayerItemInfo;
        mPosition = position;
        mNetRequestFlag = 2;
        String url = BASE_URL + POST_UPDATE;
        JSONObject jsonObject = new JSONObject();
        try {
          jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
          jsonObject.put("id", videoPlayerItemInfo.id);
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

    pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
      @Override
      public void refresh() {
        refreshCus();
      }

      @Override
      public void loadMore() {
        if (mTotal < mPageNumer * pageSize) {
          Utils.show("没有更多数据啦");
          pullToRefreshLayout.finishLoadMore();
        } else {
          mPageNumer += 1;
          loadMoreCus(mPageNumer);
        }
      }
    });
    setNetLisenter(new RequestResponse() {
      @Override
      public void failure(Exception e) {
        LogUtil.showELog(TAG, "failure(Exception e) e:" + e.toString());
        pullToRefreshLayout.finishRefresh();
        pullToRefreshLayout.finishLoadMore();
      }

      @Override
      public void success(String data) {
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
        } else {
          parseData(data);
        }
      }
    });
  }

  private void parseData(String data) {
    LogUtil.showELog(TAG, "parseData(String data) 解析数据data：" + data);
    mData.clear();
    try {
      JSONObject jsonObject = new JSONObject(data);
      int code = Utils.jsonObjectIntGetValue(jsonObject, "code");
      int total = Utils.jsonObjectIntGetValue(jsonObject, "total");
      mTotal = total;
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
//          pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
          pullToRefreshLayout.finishRefresh();
        } else if (mRefresh == 2) {
          int size = mIndexShowAdapter.getSize();

          mIndexShowAdapter.appendData(mData);
//          pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
          pullToRefreshLayout.finishLoadMore();

          contentView.smoothScrollToPosition(size);
        }
      } else {
        pullToRefreshLayout.finishRefresh();
        pullToRefreshLayout.finishLoadMore();
      }
    } catch (Exception e) {
      LogUtil.showELog(TAG, "parseData(String data) catch (JSONException e)"
          + " 异常：" + e.toString());
      Utils.show("解析数据失败");
    }

//    mPayTaskModels.clear();
//    try {
//      JSONObject jsonObject = new JSONObject(data);
//      if (jsonObject == null) {
//        return;
//      }
//      String url = Utils.jsonObjectStringGetValue(jsonObject, "url");
//      LogUtil.showDLog(TAG, "parseData url = " + url);
//      mViewPagerData.add(url);
//      mIndexViewPagerAdapter.setData(mViewPagerData);
//
//      // 解析垫付任务
//      JSONArray jsonArrayPayTask = jsonObject.getJSONArray("taskVoList");
//      int sizePay = jsonArrayPayTask.length();
//      for (int i = 0; i < sizePay; i++) {
//        /**
//         * public String picPath;
//         *     public String taskType;
//         *     public float capital;
//         *     public float commission;
//         *     public String type;
//         *     public String taskId;
//         */
//        JSONObject jo = jsonArrayPayTask.getJSONObject(i);
//        PayTaskModel payTaskModel = new PayTaskModel();
//        payTaskModel.picPath = jo.getString("picPath");
//        payTaskModel.taskType = jo.getString("taskType");
//        payTaskModel.capital = jo.getString("capital");
//        payTaskModel.commission = jo.getString("commission");
////        payTaskModel.type = jo.getString("type");
//        payTaskModel.taskId = jo.getInt("taskId");
//        mPayTaskModels.add(payTaskModel);
//      }
//
//      if (mRefresh == 1) {
//        // 开始显示垫付任务
//        mIndexAdvanceTaskAdapter.setData(mPayTaskModels);
//        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//      } else if (mRefresh == 2) {
//        mIndexAdvanceTaskAdapter.appendMoreData(mPayTaskModels);
//        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//      }
//    } catch (JSONException e) {
//      LogUtil.showELog(TAG, "parseData(String data) catch (JSONException e)"
//          + " 异常：" + e.toString());
//      Utils.show("解析数据失败");
//    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }

}
