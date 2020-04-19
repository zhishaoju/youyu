package com.youyu.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.youyu.R;

/**
 * @Author zhisiyi
 * @Date 2020.04.11 14:39
 * @Comment
 */
public class RegisterActivity extends BaseActivity {

  @BindView(R.id.et_phone)
  EditText etPhone;
  @BindView(R.id.bt_login)
  Button btLogin;
  @BindView(R.id.fl_chat)
  FrameLayout flChat;
  @BindView(R.id.fl_qq)
  FrameLayout flQq;
  @BindView(R.id.cb_protocol)
  CheckBox cbProtocol;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    ButterKnife.bind(this);
  }

  @OnClick({R.id.bt_login, R.id.cb_protocol})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.bt_login:
        break;
      case R.id.cb_protocol:
        break;
      default:
        break;
    }
  }
}
