package com.youyu.activity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.youyu.R;
import com.youyu.adapter.ActiveAdapter;
import com.youyu.bean.ActiveModel;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.cusListview.PullToRefreshLayout;
import java.util.ArrayList;

/**
 * @Author zhisiyi
 * @Date 2020.04.20 22:22
 * @Comment
 */
public class ActiveListActivity extends BaseActivity {

  @BindView(R.id.fl_back)
  FrameLayout flBack;
  @BindView(R.id.tv_title)
  TextView tvTitle;
  @BindView(R.id.content_view)
  CusRecycleView contentView;
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

  private ActiveAdapter mActiveAdapter;
  private ArrayList<ActiveModel> mActiveModelList;
  private Unbinder mUnBinder;

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
    mActiveModelList = new ArrayList<>();
    mActiveAdapter = new ActiveAdapter(this);
    contentView.setLayoutManager(new LinearLayoutManager(this));
    contentView.setAdapter(mActiveAdapter);

    ActiveModel activeModel = new ActiveModel();
    activeModel.title = "test1";
    activeModel.beginTime = "20200425";
    activeModel.endTime = "20200609";
    activeModel.count = "2131";
    activeModel.status = 1;

    ActiveModel activeModel1 = new ActiveModel();
    activeModel1.title = "test2";
    activeModel1.beginTime = "20200426";
    activeModel1.endTime = "20200612";
    activeModel1.count = "213166";
    activeModel1.status = 2;

    mActiveModelList.add(activeModel);
    mActiveModelList.add(activeModel1);

    mActiveAdapter.setData(mActiveModelList);

  }

  private void initListener() {
  }

  @OnClick(R.id.fl_back)
  public void onViewClicked() {
    finish();
  }
}
