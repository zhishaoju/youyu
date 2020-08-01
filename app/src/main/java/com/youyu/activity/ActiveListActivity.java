package com.youyu.activity;

import static com.youyu.utils.Contants.Net.ACTIVITY_LIST;
import static com.youyu.utils.Contants.Net.BASE_URL;
import static com.youyu.utils.Contants.PAGE_SIZE;
import static com.youyu.utils.Contants.USER_ID;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
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
import com.youyu.adapter.ActiveAdapter;
import com.youyu.adapter.ActiveAdapter.OnItemClickListener;
import com.youyu.bean.ActiveModel;
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
 * @Date 2020.04.20 22:22
 * @Comment
 */
public class ActiveListActivity extends BaseActivity {

  private final String TAG = ActiveListActivity.class.getSimpleName();
  @BindView(R.id.fl_back)
  FrameLayout flBack;
  @BindView(R.id.tv_title)
  TextView tvTitle;
  @BindView(R.id.content_view)
  CusRecycleView contentView;
//  @BindView(R.id.pull_icon)
//  ImageView pullIcon;
//  @BindView(R.id.refreshing_icon)
//  ImageView refreshingIcon;
//  @BindView(R.id.state_tv)
//  TextView stateTv;
//  @BindView(R.id.state_iv)
//  ImageView stateIv;
//  @BindView(R.id.head_view)
//  RelativeLayout headView;
//  @BindView(R.id.pullup_icon)
//  ImageView pullupIcon;
//  @BindView(R.id.loading_icon)
//  ImageView loadingIcon;
//  @BindView(R.id.loadstate_tv)
//  TextView loadstateTv;
//  @BindView(R.id.loadstate_iv)
//  ImageView loadstateIv;
//  @BindView(R.id.loadmore_view)
//  RelativeLayout loadmoreView;
//  @BindView(R.id.refresh_view)
//  PullToRefreshLayout refreshView;

  @BindView(R.id.pull_ro_refresh)
  PullToRefreshLayout pullToRefreshLayout;

  private ActiveAdapter mActiveAdapter;
  private ArrayList<ActiveModel> mActiveModelList;
  private Unbinder mUnBinder;

  private int mPageNumer = 1;
  private int mRefresh; // =1 代表刷新；=2 代表加载更多
  private int pageSize = PAGE_SIZE;
  private int mTotal;

  private ArrayList<ActiveModel> mData = new ArrayList<>();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_active);
    mUnBinder = ButterKnife.bind(this);
    initValue();
    initListener();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mUnBinder.unbind();
  }

  private void initValue() {
    tvTitle.setText("活动列表");
    mActiveModelList = new ArrayList<>();
    mActiveAdapter = new ActiveAdapter(this);
    contentView.setLayoutManager(new LinearLayoutManager(this));
    contentView.setAdapter(mActiveAdapter);

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
//    mActiveAdapter.setData(mActiveModelList);

  }

  @Override
  protected void onResume() {
    super.onResume();
    refreshCus();
  }

  private void initListener() {

    mActiveAdapter.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(ActiveModel activeModel) {
        Intent intent = new Intent(ActiveListActivity.this, ActiveDetailActivity.class);
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

    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {
        LogUtil.showELog(TAG, "failure(Exception e) e:" + e.toString());
      }

      @Override
      public void success(String data) {
        parseData(data);
      }
    });
  }

  @OnClick(R.id.fl_back)
  public void onViewClicked() {
    finish();
  }

  private void refreshCus() {
    mRefresh = 1;
    mPageNumer = 1;
    LogUtil.showDLog(TAG, "refreshCus()");
    String url = BASE_URL + ACTIVITY_LIST;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      jsonObject.put("pageNum", mPageNumer);
      jsonObject.put("pageSize", pageSize);
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "active list refreshCus e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }

  private void loadMoreCus(int pageNum) {
    LogUtil.showDLog(TAG, "loadMoreCus(int pageNum) pageNum = " + pageNum);
    mRefresh = 2;
    String url = BASE_URL + ACTIVITY_LIST;
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
            ActiveModel activeBean = new Gson()
                .fromJson(vpii, ActiveModel.class);
            mData.add(activeBean);
          }
        }
        if (mRefresh == 1) {
          mActiveAdapter.setData(mData);
          pullToRefreshLayout.finishRefresh();
//          refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
        } else if (mRefresh == 2) {
          mActiveAdapter.appendData(mData);
          pullToRefreshLayout.finishLoadMore();
//          refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }
      }
    } catch (Exception e) {
      LogUtil.showELog(TAG, "parseData(String data) catch (JSONException e)"
          + " 异常：" + e.toString());
      Utils.show("解析数据失败");
    }
  }

}
