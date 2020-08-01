package com.youyu.fragment;

import static com.youyu.utils.Contants.Net.ACTIVITY_LIST;
import static com.youyu.utils.Contants.Net.BASE_URL;
import static com.youyu.utils.Contants.PAGE_SIZE;
import static com.youyu.utils.Contants.USER_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.gson.Gson;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.youyu.R;
import com.youyu.activity.ActiveDetailActivity;
import com.youyu.adapter.SecondFragmentAdapter;
import com.youyu.adapter.SecondFragmentAdapter.ItemClickListener;
import com.youyu.adapter.SecondPagerAdapter;
import com.youyu.bean.ActiveModel;
import com.youyu.bean.SecondViewPagerModel;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.net.NetInterface.RequestResponse;
import com.youyu.utils.LogUtil;
import com.youyu.utils.SharedPrefsUtil;
import com.youyu.utils.Utils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecondFragment extends BaseFragment {

  private final String TAG = SecondFragment.class.getSimpleName();
  @BindView(R.id.viewPager)
  ViewPager viewPager;
  @BindView(R.id.pull_to_refresh)
  PullToRefreshLayout pullToRefreshLayout;
  @BindView(R.id.content_view)
  CusRecycleView contentView;


  private SecondPagerAdapter mSecondPagerAdapter;

  private SecondFragmentAdapter mActiveAdapter;

  private ArrayList<SecondViewPagerModel> mData;
  private ArrayList<ActiveModel> mActiveModelList;
  private Unbinder mUnBinder;

  private int mPageNumer = 1;
  private int mRefresh; // =1 代表刷新；=2 代表加载更多
  private int pageSize = PAGE_SIZE;
  private int mTotal;

  private boolean mIsFirst = true;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_second, container, false);
    mUnBinder = ButterKnife.bind(this, view);
    initValue();
    initListener();
    return view;
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
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
    LogUtil.showELog(TAG, "hidden = " + hidden);
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
    LogUtil.showELog(TAG, "onResume");
    LogUtil.showELog(TAG, "onResume");
    // 第一次进来的时候，会走到这里而不走onHiddenChanged
    // 请求网络展示界面
    if (mIsFirst) {
      refreshCus();
      mIsFirst = false;
    }
  }

  private void initValue() {
    mData = new ArrayList<>();
    mActiveModelList = new ArrayList<>();
    mSecondPagerAdapter = new SecondPagerAdapter(getActivity());
    mActiveAdapter = new SecondFragmentAdapter(getActivity());
    contentView.setLayoutManager(new LinearLayoutManager(getActivity()));
    contentView.setAdapter(mActiveAdapter);
    viewPager.setAdapter(mSecondPagerAdapter);

    SecondViewPagerModel secondViewPagerModel1 = new SecondViewPagerModel();
    secondViewPagerModel1.picUrl =
        "http://youimg1.c-ctrip.com/target/tg/618/480/977/c8339992a8ef4df7b9520ef28ec5c856.jpg";

//    SecondViewPagerModel secondViewPagerModel2 = new SecondViewPagerModel();
//    secondViewPagerModel2.picUrl = "http://img1.imgtn.bdimg.com/it/u=3961931988,142045105&fm=26&gp=0.jpg";
    mData.add(secondViewPagerModel1);
//    mData.add(secondViewPagerModel2);
    mSecondPagerAdapter.setData(mData);
//    ActiveModel activeModel = new ActiveModel();
//    activeModel.title = "test1";
//    activeModel.beginTime = "20200425";
//    activeModel.endTime = "20200609";
//    activeModel.count = "2131";
//    activeModel.status = 1;
//
//    ActiveModel activeModel1 = new ActiveModel();
//    activeModel1.title = "test2";
//    activeModel1.beginTime = "20200426";
//    activeModel1.endTime = "20200612";
//    activeModel1.count = "213166";
//    activeModel1.status = 2;
//
//    mActiveModelList.add(activeModel);
//    mActiveModelList.add(activeModel1);
//
//    mActiveAdapter.updateData(mActiveModelList);

  }

  private void initListener() {
    mActiveAdapter.setOnClickListener(new ItemClickListener() {
      @Override
      public void OnItemClickListener(ActiveModel activeModel) {
        Intent intent = new Intent(getActivity(), ActiveDetailActivity.class);
        intent.putExtra("activityId", activeModel.id);
        startActivity(intent);
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
        parseData(data);
      }
    });

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnBinder.unbind();
  }

  private void parseData(String data) {
    LogUtil.showELog(TAG, "parseData(String data) 解析数据data：" + data);
    mActiveModelList.clear();
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
            ActiveModel activeModel = new Gson()
                .fromJson(vpii, ActiveModel.class);
            mActiveModelList.add(activeModel);
          }
        }

        LogUtil.showELog(TAG,
            "parseData(String data) mActiveModelList.size：" + mActiveModelList.size());
        if (mRefresh == 1) {
          mActiveAdapter.updateData(mActiveModelList);
          pullToRefreshLayout.finishRefresh();
        } else if (mRefresh == 2) {
          int size = mActiveAdapter.getSize();

          mActiveAdapter.appendData(mActiveModelList);
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
  }

  private void refreshCus() {
    mRefresh = 1;
    mPageNumer = 1;
    LogUtil.showDLog(TAG, "refresh()");
    String url = BASE_URL + ACTIVITY_LIST;
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
    LogUtil.showDLog(TAG, "loadMore(int pageNum) pageNum = " + pageNum);
    mRefresh = 2;
    String url = BASE_URL + ACTIVITY_LIST;
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


}
