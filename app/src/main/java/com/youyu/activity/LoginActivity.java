package com.youyu.activity;

import static com.youyu.utils.Contants.USER_ID;

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
import com.youyu.R;
import com.youyu.net.NetInterface;
import com.youyu.utils.LogUtil;
import com.youyu.utils.SharedPrefsUtil;

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
   * =1, 登录； =2, 忘记密码；=3
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
        // 登录成功之后，把userId保存起来
        String userId = "";
        SharedPrefsUtil.put(USER_ID, userId);
        if (1 == mNetClick) {
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
        String phone = etPhone.getText().toString();
        String pass = etPassword.getText().toString();
        LogUtil.showDLog(TAG, "bt_login()");
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
