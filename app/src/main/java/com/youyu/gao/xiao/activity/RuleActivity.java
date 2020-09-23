package com.youyu.gao.xiao.activity;

import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;

import android.os.Bundle;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;

/**
 * @Author zhiyukai
 * @Date 2020.09.23 23:15
 * @Comment
 */
public class RuleActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rule);
    initListener();
    netQuest();
  }

  private void netQuest() {
//    String url = BASE_URL +
  }

  private void initListener() {
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {

      }

      @Override
      public void success(String data) {

      }
    });
  }
}
