package com.youyu.activity;

import static com.youyu.utils.Contants.Net.ACTIVE_DETAIL;
import static com.youyu.utils.Contants.Net.BASE_URL;
import static com.youyu.utils.Contants.Net.RECORD_LIST;
import static com.youyu.utils.Contants.USER_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.youyu.R;
import com.youyu.adapter.RecordListAdapter;
import com.youyu.bean.ActiveBean;
import com.youyu.bean.RecordListBean;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.cusListview.PullToRefreshLayout;
import com.youyu.cusListview.PullToRefreshLayout.OnRefreshListener;
import com.youyu.net.NetInterface.RequestResponse;
import com.youyu.utils.JsonUtils;
import com.youyu.utils.LogUtil;
import com.youyu.utils.SharedPrefsUtil;
import com.youyu.utils.Utils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author zhisiyi
 * @Date 2020.04.22 18:34
 * @Comment
 */
public class ActiveDetailActivity extends BaseActivity {

  private static final String TAG = ActiveDetailActivity.class.getSimpleName();
  @BindView(R.id.fl_back)
  FrameLayout flBack;
  @BindView(R.id.tv_title)
  TextView tvTitle;
  @BindView(R.id.tv_active_name)
  TextView tvActiveName;
  @BindView(R.id.tv_time)
  TextView tvTime;
  @BindView(R.id.tv_require)
  TextView tvRequire;
  @BindView(R.id.tv_join_num)
  TextView tvJoinNum;
  @BindView(R.id.tv_active_count_down)
  TextView tvActiveCountDown;
  @BindView(R.id.tv_active_money)
  TextView tvActiveMoney;
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
  @BindView(R.id.finish_task_view)
  CusRecycleView finishTaskView;
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

  private Intent mIntent;
  private String activeId = "";

  private int mPageNumer = 1;
  private int mRefresh; // =1 代表刷新；=2 代表加载更多
  private int pageSize = 10;

  private ArrayList<RecordListBean> mData = new ArrayList<>();

  private RecordListAdapter mRecordListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_active_detail);
    ButterKnife.bind(this);
    initValue();
    initListener();
  }

  @Override
  protected void onResume() {
    super.onResume();
    refresh();
    activityInfoNet();
  }

  private void activityInfoNet() {
    String url = BASE_URL + ACTIVE_DETAIL;
    String params = "";
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("activityId", activeId);
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      params = jsonObject.toString();
    } catch (Exception e) {
      LogUtil.showELog(TAG, "params e :" + e.getLocalizedMessage());
    }
    post(url, params);
  }

  private void initValue() {
    tvTitle.setText("活动详情");
    mIntent = getIntent();
    if (mIntent != null) {
      activeId = mIntent.getStringExtra("activityId");
    }
    mRecordListAdapter = new RecordListAdapter(this);

    finishTaskView.setLayoutManager(new LinearLayoutManager(this));
    finishTaskView.setAdapter(mRecordListAdapter);
  }

  private void initListener() {
    refreshView.setOnRefreshListener(new OnRefreshListener() {

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
        refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
        LogUtil.showELog(TAG, "failure e : " + e.getStackTrace());
      }

      @Override
      public void success(String data) {
        if (JsonUtils.isJsonObject(data)) {
          try {
            mData.clear();
            JSONObject jsonObject = new JSONObject(data);
            int code = Utils.jsonObjectIntGetValue(jsonObject, "code");
            if (code == 0) {
              if (jsonObject.has("data")) {
                String resultData = jsonObject.getJSONObject("data").toString();
                ActiveBean activeBean = new Gson().fromJson(resultData, ActiveBean.class);
                tvTitle.setText(activeBean.title);
                tvTime.setText(activeBean.beginTime + "-" + activeBean.endTime);
                tvRequire.setText(activeBean.content);
                tvJoinNum.setText(activeBean.haveJoin + "");
                tvActiveCountDown.setText("");
                tvActiveMoney.setText(activeBean.totalMoney + "");

              } else if (jsonObject.has("rows")) {
                JSONArray ja = jsonObject.getJSONArray("rows");
                if (ja != null) {
                  int size = ja.length();
                  for (int i = 0; i < size; i++) {
                    String vpii = ja.getJSONObject(i).toString();
                    RecordListBean activeBean = new Gson()
                        .fromJson(vpii, RecordListBean.class);
                    mData.add(activeBean);
                  }
                }
                if (mRefresh == 1) {
                  mRecordListAdapter.setData(mData);
                  refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else if (mRefresh == 2) {
                  mRecordListAdapter.appendData(mData);
                  refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
              }
            }
          } catch (Exception e) {
            LogUtil.showELog(TAG, "success data e : " + e.getLocalizedMessage());
          }
        }
      }
    });
  }

  private void refresh() {
    LogUtil.showDLog(TAG, "refresh()");
    mRefresh = 1;
    String url = BASE_URL + RECORD_LIST;
    String params = "";
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("activityId", activeId);
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      jsonObject.put("pageNum", mPageNumer);
      jsonObject.put("pageSize", pageSize);
      params = jsonObject.toString();
    } catch (Exception e) {
      LogUtil.showELog(TAG, "params refresh e :" + e.getLocalizedMessage());
    }
    post(url, params);
  }

  private void loadMore(int pageNum) {
    LogUtil.showDLog(TAG, "loadMore(int pageNum) pageNum = " + pageNum);
    mRefresh = 2;
    String url = BASE_URL + RECORD_LIST;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("activityId", activeId);
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      jsonObject.put("pageNum", pageNum);
      jsonObject.put("pageSize", pageSize);
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "loadMore list refresh e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }

  @OnClick({R.id.fl_back, R.id.tv_title})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.fl_back:
        finish();
        break;
      case R.id.tv_title:
        break;
    }
  }
}
