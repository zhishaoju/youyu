package com.youyu.gao.xiao.activity;

import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.CODE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.Contants.NetStatus;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author zhisiyi
 * @Date 2020.04.11 14:39
 * @Comment
 */
public class RegisterActivity extends BaseActivity {

  private String TAG = RegisterActivity.class.getSimpleName();

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

  private Intent mIntent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    ButterKnife.bind(this);
    mIntent = getIntent();
    initListener();
  }

  private void initListener() {
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {
        LogUtil.showELog(TAG, "setNetListener failure check code e :" + e.getLocalizedMessage());
      }

      @Override
      public void success(String data) {
        LogUtil.showELog(TAG, "setNetListener success data :" + data);
        try {
          JSONObject jsonObject1 = new JSONObject(data);
          int code = Utils.jsonObjectIntGetValue(jsonObject1, "code");
          if (NetStatus.OK == code) {
//            JSONObject jsonObject = jsonObject1.getJSONObject("data");
//            SharedPrefsUtil.put(USER_ID, jsonObject.getString("id"));
            Intent intent = new Intent(RegisterActivity.this, RegisterSetPassActivity.class);
            intent.putExtra("mobile", etPhone.getText().toString());
            startActivityForResult(intent, 100);
          }
        } catch (Exception e) {
          LogUtil.showELog(TAG, "success e : " + e.getLocalizedMessage());
        }
//        Intent intent = new Intent(RegisterActivity.this, RegisterSetPassActivity.class);
//        intent.putExtra("mobile", etPhone.getText().toString());
//        startActivity(intent);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    LogUtil.showDLog(TAG, "onActivityResult requestCode :" + requestCode);
    LogUtil.showDLog(TAG, "onActivityResult resultCode :" + resultCode);
    if (requestCode == 100) {
      if (mIntent != null) {
        setResult(Activity.RESULT_OK, mIntent);
        finish();
      }
    }
  }

  @OnClick({R.id.bt_login, R.id.cb_protocol})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.bt_login:
        if (cbProtocol.isChecked()) {
          String url = BASE_URL + CODE;
          JSONObject jsonObject = new JSONObject();
          try {
            LogUtil.showDLog(TAG, "R.id.bt_login code :" + Utils.getIMEI());
            jsonObject.put("mobile", etPhone.getText().toString());
            jsonObject.put("imei", Utils.getIMEI());
          } catch (JSONException e) {
            LogUtil.showELog(TAG, "R.id.bt_login e :" + e.getLocalizedMessage());
          }
          String param = jsonObject.toString();
          post(url, param);
        } else {
          Utils.show("请同意相关的协议~");
        }
        break;
      case R.id.cb_protocol:
        break;
      default:
        break;
    }
  }
}
