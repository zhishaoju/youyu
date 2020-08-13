package com.youyu.gao.xiao.activity;

import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.REGISTER;
import static com.youyu.gao.xiao.utils.Contants.NetStatus.USER_EXIST;
import static com.youyu.gao.xiao.utils.Contants.USER_ID;
import static com.youyu.gao.xiao.utils.Contants.USER_PASSWORD;
import static com.youyu.gao.xiao.utils.Contants.USER_PHONE;
import static com.youyu.gao.xiao.utils.Utils.jsonObjectIntGetValue;
import static com.youyu.gao.xiao.utils.Utils.jsonObjectStringGetValue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.Contants;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author zhisiyi
 * @Date 2020.04.28 12:01
 * @Comment
 */
public class RegisterSetPassActivity extends BaseActivity {

  private String TAG = RegisterSetPassActivity.class.getSimpleName();

  @BindView(R.id.et_code)
  EditText etCode;
  @BindView(R.id.et_password)
  EditText etPassword;
  @BindView(R.id.bt_login)
  Button btLogin;

  private Intent mIntent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register_set_pass);
    ButterKnife.bind(this);
    mIntent = getIntent();
    initListener();
  }

  @OnClick(R.id.bt_login)
  public void onViewClicked() {
    String url = BASE_URL + REGISTER;
    JSONObject jsonObject = new JSONObject();
    try {
      if (mIntent != null) {
        LogUtil.showELog(TAG, "R.id.bt_login mobile :" + mIntent.getStringExtra("mobile"));
        jsonObject.put("mobile", mIntent.getStringExtra("mobile"));
      }
      LogUtil.showELog(TAG, "R.id.bt_login Utils.getIMEI() :" + Utils.getIMEI());
      jsonObject.put("imei", Utils.getIMEI());
      jsonObject.put("type", "0");
      jsonObject.put("code", etCode.getText().toString());
      jsonObject.put("password", etPassword.getText().toString());
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "R.id.bt_login e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }

  private void initListener() {
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {
        LogUtil.showELog(TAG, "setNetListener check code e :" + e.getLocalizedMessage());
      }

      @Override
      public void success(String data) {
        JSONObject jsonObject = null;
        try {
          jsonObject = new JSONObject(data);
          int state = jsonObjectIntGetValue(jsonObject, "code");
          JSONObject dataJson = jsonObject.getJSONObject("data");
          String userId = jsonObjectStringGetValue(dataJson, "id");
          LogUtil.showDLog(TAG, "userId = " + userId);
          if (Contants.NetStatus.OK == state) {
            if (mIntent != null) {
              SharedPrefsUtil.put(USER_PHONE, mIntent.getStringExtra("mobile"));
            }
            SharedPrefsUtil.put(USER_PASSWORD, etPassword.getText().toString());
            SharedPrefsUtil.put(USER_ID, userId);
            setResult(-1);
            finish();
          } else if (USER_EXIST == state) {
            startActivity(new Intent(RegisterSetPassActivity.this, LoginActivity.class));
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }

      }
    });
  }
}
