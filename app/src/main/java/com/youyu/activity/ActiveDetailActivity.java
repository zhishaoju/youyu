package com.youyu.activity;

import static com.youyu.utils.Contants.Net.ACTIVE_DETAIL;
import static com.youyu.utils.Contants.Net.BASE_URL;
import static com.youyu.utils.Contants.USER_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.youyu.R;
import com.youyu.bean.ActiveBean;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.cusListview.PullToRefreshLayout;
import com.youyu.cusListview.PullToRefreshLayout.OnRefreshListener;
import com.youyu.net.NetInterface.RequestResponse;
import com.youyu.utils.JsonUtils;
import com.youyu.utils.LogUtil;
import com.youyu.utils.SharedPrefsUtil;
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
  }

  private void initListener() {
    refreshView.setOnRefreshListener(new OnRefreshListener() {

      @Override
      public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        refresh();
      }

      @Override
      public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
//                mPageNumer += 1;
//                loadMore(mPageNumer);
      }
    });
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {
        LogUtil.showELog(TAG, "failure e : " + e.getStackTrace());
      }

      @Override
      public void success(String data) {
        if (JsonUtils.isJsonObject(data)) {
          try {
            String resultData = new JSONObject(data).getJSONObject("data").toString();
            ActiveBean activeBean = new Gson().fromJson(resultData, ActiveBean.class);
            tvTitle.setText(activeBean.title);
            tvTime.setText(activeBean.beginTime + "-" + activeBean.endTime);
            tvRequire.setText(activeBean.content);
            tvJoinNum.setText(activeBean.haveJoin + "");
            tvActiveCountDown.setText("");
            tvActiveMoney.setText(activeBean.totalMoney + "");
          } catch (JSONException e) {
            LogUtil.showELog(TAG, "success e : " + e.getLocalizedMessage());
          }
        }
      }
    });
  }


  private void refresh() {
    LogUtil.showDLog(TAG, "refresh()");
//    post(url, jsonObject.toString());
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
