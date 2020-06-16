package com.youyu.fragment;


import static com.youyu.utils.Contants.Net.BASE_URL;
import static com.youyu.utils.Contants.Net.POST_COMMENT_LIST;
import static com.youyu.utils.Contants.Net.POST_UPDATE;
import static com.youyu.utils.Contants.USER_ID;

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
import com.google.gson.Gson;
import com.youyu.R;
import com.youyu.adapter.VideoPlayListAdatper;
import com.youyu.adapter.VideoPlayListAdatper.OnClickListener;
import com.youyu.bean.VideoPlayerItemInfo;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.cusListview.PullToRefreshLayout;
import com.youyu.net.NetInterface.RequestResponse;
import com.youyu.utils.LogUtil;
import com.youyu.utils.SharedPrefsUtil;
import com.youyu.utils.Utils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhishaoju
 */
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
  private int pageSize = 10;

  private Unbinder mUnbinder;

  private VideoPlayListAdatper mIndexShowAdapter;
  private LinearLayoutManager lm;
  private ArrayList<VideoPlayerItemInfo> mData = new ArrayList<>();

  private int mNetRequestFlag = -1;

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
    mNetRequestFlag = 1;
    mRefresh = 1;
    LogUtil.showDLog(TAG, "refresh()");
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

  private void loadMore(int pageNum) {
    mNetRequestFlag = 1;
    LogUtil.showDLog(TAG, "loadMore(int pageNum) pageNum = " + pageNum);
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

    mIndexShowAdapter.setOnViewClick(new OnClickListener() {
      @Override
      public void onViewClick(int flag, String postId) {
        mNetRequestFlag = 2;
        String url = BASE_URL + POST_UPDATE;
        JSONObject jsonObject = new JSONObject();
        try {
          jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
          jsonObject.put("id", postId);
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
        if (mNetRequestFlag == 2) {

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
          refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
        } else if (mRefresh == 2) {
          mIndexShowAdapter.appendData(mData);
          refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }
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
