package com.youyu.gao.xiao.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.youyu.gao.xiao.R;

/**
 * @Author zhisiyi
 * @Date 2020.08.10 15:54
 * @Comment
 */
public class PrivacyPolicyActivity extends BaseActivity {

  @BindView(R.id.tv_title)
  TextView tvTitle;

  private Unbinder mUnBinder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_privacy_policy);
    mUnBinder = ButterKnife.bind(this);
    tvTitle.setText("隐私政策与协议");
    WebView webview = (WebView) findViewById(R.id.web_view);
    webview.loadUrl("http://youyu.qiaobahuyu.com/private.html");
//    file:///android_asset/web/index.html

    webview.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
      }
    });
  }

  @OnClick(R.id.fl_back)
  public void onViewClicked() {
    finish();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mUnBinder.unbind();
  }
}
