package com.youyu.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.youyu.R;
import com.youyu.adapter.VideoPlayListAdatper;
import com.youyu.bean.VideoPlayerItemInfo;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.cusListview.PullToRefreshLayout;
import com.youyu.net.NetInterface.RequestResponse;
import com.youyu.utils.LogUtil;
import java.util.ArrayList;

public class IndexFragment extends BaseFragment {

  private static final String TAG = IndexFragment.class.getSimpleName();
  @BindView(R.id.pull_icon)
  ImageView pullIcon;
  @BindView(R.id.refreshing_icon)
  ImageView refreshingIcon;
  @BindView(R.id.state_tv)
  TextView stateTv;
  @BindView(R.id.state_iv)
  ImageView stateIv;
  @BindView(R.id.head_view)
  RelativeLayout headView;
  @BindView(R.id.content_view)
  CusRecycleView contentView;
  //  @BindView(R.id.content_view)
//  PullableGridView contentView;
  @BindView(R.id.pullup_icon)
  ImageView pullupIcon;
  @BindView(R.id.loading_icon)
  ImageView loadingIcon;
  @BindView(R.id.loadstate_tv)
  TextView loadstateTv;
  @BindView(R.id.loadstate_iv)
  ImageView loadstateIv;
  @BindView(R.id.loadmore_view)
  RelativeLayout loadmoreView;
  @BindView(R.id.refresh_view)
  PullToRefreshLayout refreshView;

  private int mPageNumer = 1;
  private int mRefresh; // =1 代表刷新；=2 代表加载更多

  private Unbinder mUnbinder;

  private VideoPlayListAdatper mIndexShowAdapter;
  private LinearLayoutManager lm;

//  private GridView gridView;
//  private CusRecycleView cusRecycleView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_index, container, false);
    mUnbinder = ButterKnife.bind(this, view);
//    gridView = (GridView) view.findViewById(R.id.content_view);
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
    String url = "http://ips.ifeng.com/video19.ifeng.com/video09/2017/05/24/4664192-102-008-1012.mp4";

    //数据的初始化
    ArrayList data = new ArrayList<VideoPlayerItemInfo>();
    for (int i = 0; i < 20; i++) {
      data.add(new VideoPlayerItemInfo(i, url));
    }

//    mIndexShowAdapter.setData(data);
    mIndexShowAdapter = new VideoPlayListAdatper(getActivity(), data);
    contentView.setAdapter(mIndexShowAdapter);
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser) {
      // 请求网络
      refresh();
    }
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    LogUtil.showELog(TAG, "hidden = " + hidden);
    if (!hidden) {
      // 请求网络展示界面
      refresh();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    LogUtil.showELog(TAG, "onResume");
    // 第一次进来的时候，会走到这里而不走onHiddenChanged
    // 请求网络展示界面
    refresh();
  }

  private void refresh() {
    LogUtil.showDLog(TAG, "refresh()");
//    post(url, jsonObject.toString());
  }

  private void loadMore(int pageNum) {
    LogUtil.showDLog(TAG, "loadMore(int pageNum) pageNum = " + pageNum);
//    mRefresh = 2;
//    String buyerCode = SharedPrefsUtil.get(Contants.PHONE, "");
//    String buyerId = SharedPrefsUtil.get(Contants.BUYER_ID, "");
//    String token = SharedPrefsUtil.get(Contants.TOKEN, "");
//    JSONObject jsonObject = new JSONObject();
//    try {
//      jsonObject.put("buyerCode", buyerCode);
//      jsonObject.put("buyerId", buyerId);
//      jsonObject.put("token", token);
//      jsonObject.put("pageNumber", pageNum);
//      jsonObject.put("pageSize", PER_PAGE_NUM);
//
//    } catch (Exception e) {
//      LogUtil.showELog(TAG, "loadMore e:" + e.toString());
//    }
//
//    String url = Contants.Net.BASE_URL + BUYER;
//    post(url, jsonObject.toString());
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
    setNetLisenter(new RequestResponse() {
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

  private void parseData(String data) {
    LogUtil.showELog(TAG, "parseData(String data) 解析数据data：" + data);
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
