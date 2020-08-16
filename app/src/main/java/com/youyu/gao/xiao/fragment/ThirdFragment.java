package com.youyu.gao.xiao.fragment;

import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.LOGIN;
import static com.youyu.gao.xiao.utils.Contants.Net.USER_INFO;
import static com.youyu.gao.xiao.utils.Contants.NetStatus.USER_NOT_EXIST;
import static com.youyu.gao.xiao.utils.Contants.USER_ID;
import static com.youyu.gao.xiao.utils.Contants.USER_PASSWORD;
import static com.youyu.gao.xiao.utils.Contants.USER_PHONE;
import static com.youyu.gao.xiao.utils.Utils.fromPropertiesGetValue;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.activity.ActiveListActivity;
import com.youyu.gao.xiao.activity.CollectActivity;
import com.youyu.gao.xiao.activity.InComeActivity;
import com.youyu.gao.xiao.activity.PrivacyPolicyActivity;
import com.youyu.gao.xiao.activity.RegisterActivity;
import com.youyu.gao.xiao.bean.UserInfo;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.Contants.NetStatus;
import com.youyu.gao.xiao.utils.JsonUtils;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;
import com.youyu.gao.xiao.view.CircleImageView;
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
  //  @BindView(R.id.tv_login)
//  TextView tvLogin;
  @BindView(R.id.ll_login)
  LinearLayout llLogin;

  @BindView(R.id.ll_my_view)
  LinearLayout llMyView;


  @BindView(R.id.et_phone)
  EditText etPhone;
  @BindView(R.id.et_password)
  EditText etPassword;
  @BindView(R.id.iv_pass_icon)
  ImageView ivPassIcon;
  @BindView(R.id.fl_pass_icon)
  FrameLayout flPassIcon;
  @BindView(R.id.tv_forget_password)
  TextView tvForgetPassword;
  @BindView(R.id.bt_login)
  Button btLogin;
  @BindView(R.id.fl_chat)
  FrameLayout flChat;
  @BindView(R.id.fl_qq)
  FrameLayout flQq;

  /**
   * =1, 登录；=2, 展示个人页面 =3, 忘记密码；=4, 注册
   */
  private int mNetClickLogin;

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
      String phone = SharedPrefsUtil.get(USER_PHONE, "");
      if (!TextUtils.isEmpty(phone)) {
        userInfo(phone);
      } else {
        llLogin.setVisibility(View.VISIBLE);
        llMyView.setVisibility(View.GONE);
      }
    }
  }

  private void userInfo(String phone) {
    mNetClickLogin = 2;
    LogUtil.showELog(TAG, "userInfo phone = " + phone);
    String url = BASE_URL + USER_INFO;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("imei", Utils.getIMEI());
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "netRequest e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    LogUtil.showDLog(TAG, "onActivityResult requestCode = " + requestCode);
    LogUtil.showDLog(TAG, "onActivityResult resultCode = " + resultCode);
    if (requestCode == 1000) {
      updateInfo();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    LogUtil.showDLog(TAG, "onResume");
    // 第一次进来的时候，会走到这里而不走onHiddenChanged
    // 请求网络展示界面
    updateInfo();
  }

  private void updateInfo() {
    String phone = SharedPrefsUtil.get(USER_PHONE, "");
    if (!TextUtils.isEmpty(phone)) {
      userInfo(phone);
    } else {
      llLogin.setVisibility(View.VISIBLE);
      llMyView.setVisibility(View.GONE);
    }
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
        LogUtil.showELog(TAG, "failure(Exception e) e " + e);
      }

      @Override
      public void success(String data) {
        LogUtil.showELog(TAG, "success(String data) data = " + data);

        if (1 == mNetClickLogin) {
          loginResult(data);

        } else if ((2 == mNetClickLogin)) {
          // 展示个人界面的结果
          showUserInfo(data);
        }
      }
    });
  }

  private void loginResult(String data) {
    SharedPrefsUtil.put(USER_PHONE, etPhone.getText().toString());
    SharedPrefsUtil.put(USER_PASSWORD, etPassword.getText().toString());
    if (JsonUtils.isJsonObject(data)) {
      try {
        JSONObject jsonObject1 = new JSONObject(data);
        int code = Utils.jsonObjectIntGetValue(jsonObject1, "code");
        if (NetStatus.OK == code) {
          JSONObject jsonObject = jsonObject1.getJSONObject("data");
          SharedPrefsUtil.put(USER_ID, jsonObject.getString("id"));
          if (NetStatus.OK == code) {
            showUserInfo(data);
            llLogin.setVisibility(View.GONE);
            llMyView.setVisibility(View.VISIBLE);
          } else if (USER_NOT_EXIST == code) {
            llLogin.setVisibility(View.VISIBLE);
            llMyView.setVisibility(View.GONE);
          }
        }
      } catch (JSONException e) {
        LogUtil.showELog(TAG, "1 == mNetClickLogin e : " + e.getLocalizedMessage());
      }
    }
  }

  private void showUserInfo(String data) {
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
        llLogin.setVisibility(View.GONE);
        llMyView.setVisibility(View.VISIBLE);
      }
    } catch (Exception e) {
      LogUtil.showELog(TAG, "parseData(String data) catch (JSONException e)"
          + " 异常：" + e.toString());
      Utils.show("解析数据失败");
    }
  }

  @OnClick({R.id.ll_incoming_detail, R.id.ll_collect, R.id.ll_active,
      R.id.fl_pass_icon, R.id.tv_forget_password, R.id.bt_login, R.id.fl_chat, R.id.fl_qq,
      R.id.bt_register, R.id.ll_trade_policy})
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
      case R.id.ll_trade_policy:
        startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
        break;
//      case R.id.tv_login:
//        startActivityForResult(new Intent(getActivity(), LoginActivity.class), 100);
//        break;

      case R.id.fl_pass_icon:
        hidePassword();
        break;
      case R.id.tv_forget_password:
        LogUtil.showDLog(TAG, "tv_forget_password()");
        break;
      case R.id.bt_login:
        String phone = etPhone.getText().toString();
        String pass = etPassword.getText().toString();
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(pass)) {
          Utils.show("手机号或者密码为空");
          break;
        }
        mNetClickLogin = 1;
        String url = BASE_URL + LOGIN;
        JSONObject jsonObject = new JSONObject();
        try {
          jsonObject.put("mobile", etPhone.getText().toString());
          jsonObject.put("imei", Utils.getIMEI());
          jsonObject.put("channelId", fromPropertiesGetValue("channelId"));
          jsonObject.put("password", etPassword.getText().toString());
        } catch (JSONException e) {
          LogUtil.showELog(TAG, "R.id.bt_login e :" + e.getLocalizedMessage());
        }
        String param = jsonObject.toString();
        post(url, param);
        LogUtil.showDLog(TAG, "bt_login()");
        break;
      case R.id.bt_register:
        startActivityForResult(new Intent(getActivity(), RegisterActivity.class), 1000);
        break;
      case R.id.fl_chat:
        break;
      case R.id.fl_qq:
        break;

      default:
        break;
    }
  }

  private void hidePassword() {
    LogUtil.showDLog(TAG, "hidePassword()");

    //记住光标开始的位置
    int pos = etPassword.getSelectionStart();
    if (ivPassIcon.getDrawable().getConstantState()
        .equals(getResources().getDrawable(R.mipmap.pass_show).getConstantState())) {
      etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
      ivPassIcon.setImageDrawable(getResources().getDrawable(R.mipmap.pass_hide));
    } else {
      etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
      ivPassIcon.setImageDrawable(getResources().getDrawable(R.mipmap.pass_show));
    }
    etPassword.setSelection(pos);
  }


}
