package com.youyu.gao.xiao.activity;

import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.FEEDBACK;
import static com.youyu.gao.xiao.utils.Contants.USER_PHONE;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;
import org.json.JSONObject;

/**
 * @Author zhiyukai
 * @Date 2020.05.03 19:05
 * @Comment
 */
public class FeedBackActivity extends BaseActivity {

  @BindView(R.id.fl_back)
  FrameLayout flBack;
  @BindView(R.id.et_help_feedback)
  EditText etHelpFeedback;
  @BindView(R.id.tv_commit)
  TextView tvCommit;
  @BindView(R.id.et_name)
  EditText etName;
  @BindView(R.id.et_phone)
  EditText etPhone;

  @BindView(R.id.et_mail)
  EditText etMail;
  private String TAG = "FeedBackActivity";

  @BindView(R.id.tv_title)
  TextView tvTitle;

  @BindView(R.id.rl_title)
  RelativeLayout rlTitle;

  private Unbinder mUnBinder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feed_back);
    mUnBinder = ButterKnife.bind(this);

    initValue();
    initListener();
  }

  private void initListener() {
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {

      }

      @Override
      public void success(String data) {
        try {
          JSONObject jsonObject = new JSONObject(data);
          int code = jsonObject.getInt("code");
          if (code == 0) {
            Utils.show("提交成功~");
            finish();
          } else {
            Utils.show("提交失败！！");
          }
        } catch (Exception e) {
          LogUtil.showELog(TAG, "e = " + e.getLocalizedMessage());
        }
      }
    });
  }

  private void initValue() {
    tvTitle.setText("问题反馈");
    rlTitle.setBackgroundColor(getResources().getColor(R.color.yellow));
    String mobile = SharedPrefsUtil.get(USER_PHONE, "");
    String userName = SharedPrefsUtil.get("userName", "");
    etName.setText(userName);
    etPhone.setText(mobile);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mUnBinder.unbind();
  }

  @OnClick({R.id.fl_back, R.id.tv_commit})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.fl_back:
        finish();
        break;
      case R.id.tv_commit:
        String userId = SharedPrefsUtil.get("userId", "");
        String mobile = etPhone.getText().toString();
        String userName = etName.getText().toString();
        String content = etHelpFeedback.getText().toString();
        String email = etMail.getText().toString();

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(userName)) {
          Utils.show("昵称或者电话为空~");
        } else {
          String url = BASE_URL + FEEDBACK;

          if (!TextUtils.isEmpty(content)) {
            String params = "";
            try {
              JSONObject jsonObject = new JSONObject();
              jsonObject.put("userId", userId);
              jsonObject.put("userName", userName);
              jsonObject.put("content", content);
              jsonObject.put("mobile", mobile);
              jsonObject.put("email", email);
              params = jsonObject.toString();
            } catch (Exception e) {
              LogUtil.showELog(TAG, "params e :" + e.getLocalizedMessage());
            }
            post(url, params);
          } else {
            Utils.show("内容不能为空！");
          }
        }
        break;
    }
  }
}
