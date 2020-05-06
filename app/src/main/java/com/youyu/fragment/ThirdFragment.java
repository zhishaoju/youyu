package com.youyu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.youyu.R;
import com.youyu.activity.ActiveListActivity;
import com.youyu.activity.CollectActivity;
import com.youyu.activity.InComeActivity;
import com.youyu.activity.RegisterActivity;
import com.youyu.net.NetInterface.RequestResponse;
import com.youyu.utils.LogUtil;
import com.youyu.utils.SharedPrefsUtil;
import com.youyu.view.CircleImageView;

public class ThirdFragment extends BaseFragment {

  private final String TAG = ThirdFragment.class.getSimpleName();
  @BindView(R.id.civ_head_pic)
  CircleImageView civHeadPic;
  @BindView(R.id.tv_head_name)
  TextView tvHeadName;
  @BindView(R.id.tv_guanzhu_num)
  TextView tvGuanzhuNum;
  @BindView(R.id.tv_fensi)
  TextView tvFensi;
  @BindView(R.id.tv_zan)
  TextView tvZan;
  @BindView(R.id.tv_pinglun)
  TextView tvPinglun;
  @BindView(R.id.ll_incoming_detail)
  LinearLayout llIncomingDetail;
  @BindView(R.id.tv_incoming)
  TextView tvIncoming;
  @BindView(R.id.ll_collect)
  LinearLayout llCollect;
  @BindView(R.id.ll_active)
  LinearLayout llActive;
  @BindView(R.id.tv_login)
  TextView tvLogin;
  @BindView(R.id.ll_my_view)
  LinearLayout llMyView;

  private Unbinder mUnbinder;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_my, container, false);
    mUnbinder = ButterKnife.bind(this, view);
    initValue();
    initListener();
    return view;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private void initValue() {
    boolean isLogin = SharedPrefsUtil.get(SharedPrefsUtil.LOGIN, false);
    if (isLogin) {
      tvLogin.setVisibility(View.GONE);
      llMyView.setVisibility(View.VISIBLE);
    } else {
      tvLogin.setVisibility(View.VISIBLE);
      llMyView.setVisibility(View.GONE);
    }
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {// 不知道为啥，这个不起作用
    super.setUserVisibleHint(isVisibleToUser);
    LogUtil.showELog(TAG, "isVisibleToUser = " + isVisibleToUser);
    if (isVisibleToUser) {
      // 请求网络
    }
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    LogUtil.showELog(TAG, "hidden = " + hidden);
    if (!hidden) {
      // 请求网络展示界面
      netRequest();
    }
  }

  private void netRequest() {
    LogUtil.showELog(TAG, "netRequest()");

//    String url = BASE_URL + PERSON_INFO;
//    JSONObject jsonObject = new JSONObject();
//    try {
//      jsonObject.put("buyerCode", SharedPrefsUtil.get(Contants.PHONE, ""));
//      jsonObject.put("buyerId", SharedPrefsUtil.get(Contants.BUYER_ID, ""));
//      jsonObject.put("token", SharedPrefsUtil.get(Contants.TOKEN, ""));
//    } catch (JSONException e) {
//      e.printStackTrace();
//      LogUtil.showELog(TAG, "netRequest() JSONException e = " + e.toString());
//    }
//    post(url, jsonObject.toString());
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
  }

  @Override
  public void onResume() {
    super.onResume();
    LogUtil.showELog(TAG, "onResume");
    // 第一次进来的时候，会走到这里而不走onHiddenChanged
    // 请求网络展示界面
    netRequest();
  }

  @Override
  public void onPause() {
    super.onPause();
    LogUtil.showELog(TAG, "onPause");
  }

  private void initListener() {
    setNetLisenter(new RequestResponse() {
      @Override
      public void failure(Exception e) {

      }

      @Override
      public void success(String data) {
        LogUtil.showELog(TAG, "success(String data) data = " + data);
      }
    });
  }

  @OnClick({R.id.ll_incoming_detail, R.id.ll_collect, R.id.ll_active, R.id.tv_login})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.ll_incoming_detail:
        startActivity(new Intent(getActivity(), InComeActivity.class));
        break;
      case R.id.ll_collect:
        startActivity(new Intent(getActivity(), CollectActivity.class));
        break;
      case R.id.ll_active:
        startActivity(new Intent(getActivity(), ActiveListActivity.class));
        break;
      case R.id.tv_login:
        startActivity(new Intent(getActivity(), RegisterActivity.class));
        break;
      default:
        break;
    }
  }
}
