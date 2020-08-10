package com.youyu.gao.xiao.activity;

import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.LOGIN;
import static com.youyu.gao.xiao.utils.Contants.USER_ID;
import static com.youyu.gao.xiao.utils.Contants.USER_PASSWORD;
import static com.youyu.gao.xiao.utils.Contants.USER_PHONE;
import static com.youyu.gao.xiao.utils.Utils.fromPropertiesGetValue;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.net.NetInterface;
import com.youyu.gao.xiao.utils.Contants.NetStatus;
import com.youyu.gao.xiao.utils.JsonUtils;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author zhisiyi
 * @Date 2020.04.11 17:09
 * @Comment
 */
public class LoginActivity extends BaseActivity {

  private static final String TAG = LoginActivity.class.getSimpleName();
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
   * =1, 登录； =2, 忘记密码；=3, 注册
   */
  private int mNetClick;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
    initListener();
  }

  private void initListener() {
    setNetListener(new NetInterface.RequestResponse() {
      @Override
      public void failure(Exception e) {

      }

      @Override
      public void success(String data) {

        SharedPrefsUtil.put(USER_PHONE, etPhone.getText().toString());
        SharedPrefsUtil.put(USER_PASSWORD, etPassword.getText().toString());

        if (1 == mNetClick) {
          if (JsonUtils.isJsonObject(data)) {
            try {
              JSONObject jsonObject1 = new JSONObject(data);
              int code = Utils.jsonObjectIntGetValue(jsonObject1, "code");
              if (NetStatus.OK == code) {
                JSONObject jsonObject = jsonObject1.getJSONObject("data");
                SharedPrefsUtil.put(USER_ID, jsonObject.getString("id"));
                finish();
              }
            } catch (JSONException e) {
              LogUtil.showELog(TAG, "1 == mNetClick e : " + e.getLocalizedMessage());
            }
          }

        } else if ((2 == mNetClick)) {
        }
      }
    });
  }

  @OnClick({R.id.fl_pass_icon, R.id.tv_forget_password, R.id.bt_login, R.id.fl_chat, R.id.fl_qq})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.fl_pass_icon:
        hidePassword();
        break;
      case R.id.tv_forget_password:
        LogUtil.showDLog(TAG, "tv_forget_password()");
        break;
      case R.id.bt_login:
        mNetClick = 1;
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
        startActivity(new Intent(this, RegisterActivity.class));
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
