package com.youyu.fragment;

import static com.youyu.utils.Contants.Net.BASE_URL;
import static com.youyu.utils.Contants.Net.LOGIN;
import static com.youyu.utils.Contants.Net.USER_INFO;
import static com.youyu.utils.Contants.NetStatus.USER_NOT_EXIST;
import static com.youyu.utils.Contants.USER_ID;
import static com.youyu.utils.Contants.USER_PASSWORD;
import static com.youyu.utils.Contants.USER_PHONE;
import static com.youyu.utils.Utils.fromPropertiesGetValue;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.youyu.R;
import com.youyu.activity.ActiveListActivity;
import com.youyu.activity.CollectActivity;
import com.youyu.activity.InComeActivity;
import com.youyu.activity.LoginActivity;
import com.youyu.bean.UserInfo;
import com.youyu.net.NetInterface.RequestResponse;
import com.youyu.utils.Contants.NetStatus;
import com.youyu.utils.LogUtil;
import com.youyu.utils.SharedPrefsUtil;
import com.youyu.utils.Utils;
import com.youyu.view.CircleImageView;
import org.json.JSONException;
import org.json.JSONObject;

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

  private int flag = -1;


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
//    boolean isLogin = !TextUtils.isEmpty(SharedPrefsUtil.get(USER_PHONE, ""));
//    if (isLogin) {
//      tvLogin.setVisibility(View.GONE);
//      llMyView.setVisibility(View.VISIBLE);
//    } else {
//      tvLogin.setVisibility(View.VISIBLE);
//      llMyView.setVisibility(View.GONE);
//    }
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
    String phone = SharedPrefsUtil.get(USER_PHONE, "");
    if (TextUtils.isEmpty(phone)) {
      tvLogin.setVisibility(View.VISIBLE);
      llMyView.setVisibility(View.GONE);
//      startActivityForResult(new Intent(getActivity(), LoginActivity.class), 100);
    } else {
      flag = 1;
      String url = BASE_URL + LOGIN;
      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("mobile", phone);
        jsonObject.put("imei", Utils.getIMEI());
        jsonObject.put("channelId", fromPropertiesGetValue("channelId"));
        jsonObject.put("password", SharedPrefsUtil.get(USER_PASSWORD, ""));
      } catch (JSONException e) {
        LogUtil.showELog(TAG, "netRequest e :" + e.getLocalizedMessage());
      }
      String param = jsonObject.toString();
      post(url, param);
    }

  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 100) {
//      tvLogin.setVisibility(View.GONE);
//      llMyView.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    LogUtil.showELog(TAG, "onResume");
    // 第一次进来的时候，会走到这里而不走onHiddenChanged
    // 请求网络展示界面
//    if (!TextUtils.isEmpty(SharedPrefsUtil.get(USER_PHONE, ""))) {
    netRequest();
//    }
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
        if (flag == 1) {
          try {
            JSONObject jsonObject1 = new JSONObject(data);
            int code = Utils.jsonObjectIntGetValue(jsonObject1, "code");
            if (NetStatus.OK == code) {
              // 登录的请求结果
              flag = 2;
              String url = BASE_URL + USER_INFO;
              JSONObject jsonObject = new JSONObject();
              try {
                jsonObject.put("imei", Utils.getIMEI());
                jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
              } catch (JSONException e) {
                LogUtil.showELog(TAG, "USER_INFO e :" + e.getLocalizedMessage());
              }
              String param = jsonObject.toString();
              post(url, param);
            } else if (USER_NOT_EXIST == code) {
              tvLogin.setVisibility(View.VISIBLE);
              llMyView.setVisibility(View.GONE);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else if (flag == 2) {
          // 展示个人界面的结果
          try {
            JSONObject jsonObject = new JSONObject(data);
            int code = Utils.jsonObjectIntGetValue(jsonObject, "code");
            if (code == 0) {
              JSONObject jo = jsonObject.getJSONObject("data");
              if (jo != null) {
                UserInfo userInfo = new Gson()
                    .fromJson(jo.toString(), UserInfo.class);

                if (getActivity() != null && !TextUtils.isEmpty(userInfo.avatarUrl)) {
                  Glide.with(getActivity())
                      .asBitmap()//只加载静态图片，如果是git图片则只加载第一帧。
                      .load(userInfo.avatarUrl)
                      .placeholder(R.mipmap.ic_launcher_round)
                      .error(R.mipmap.ic_launcher_round)
                      .into(civHeadPic);
                }

                if (!TextUtils.isEmpty(userInfo.nickName)) {
                  tvHeadName.setText(userInfo.nickName + "");
                }

                tvGuanzhuNum.setText(userInfo.postTotal + "");
                tvZan.setText(userInfo.agreeTotal + "");
                tvFensi.setText(userInfo.fansTotal + "");
                tvPinglun.setText(userInfo.comTotal + "");
                tvIncoming.setText(userInfo.totalMoney + "");
              }
              tvLogin.setVisibility(View.GONE);
              llMyView.setVisibility(View.VISIBLE);
            }
          } catch (Exception e) {
            LogUtil.showELog(TAG, "parseData(String data) catch (JSONException e)"
                + " 异常：" + e.toString());
            Utils.show("解析数据失败");
          }
        }
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
        startActivityForResult(new Intent(getActivity(), LoginActivity.class), 100);
        break;
      default:
        break;
    }
  }


}
