package com.youyu.gao.xiao.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.utils.LogUtil;

/**
 * @Author zhisiyi
 * @Date 2020.08.07 23:38
 * @Comment
 */
public class XieyiDialog extends Dialog {

  private static final String TAG = XieyiDialog.class.getSimpleName();
  private boolean mCancelable;

  private OnClickEvent mOnClickEvent;

  public XieyiDialog(@NonNull Context context) {
    this(context, R.style.XieyiDialog, false);
  }

  public XieyiDialog(@NonNull Context context, int themeResId, boolean cancelable) {
    super(context, themeResId);
    mCancelable = cancelable;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogUtil.showDLog(TAG, "onCreate: ");
    initView();
  }

  public void setOnClickEvent(OnClickEvent e) {
    mOnClickEvent = e;
  }

  private void initView() {
    setContentView(R.layout.window_tips_layout);
    // 设置窗口大小
    WindowManager windowManager = getWindow().getWindowManager();
    int screenWidth = windowManager.getDefaultDisplay().getWidth();
    int screenHeight = windowManager.getDefaultDisplay().getHeight();
    WindowManager.LayoutParams attributes = getWindow().getAttributes();
    attributes.alpha = 1f;
//    attributes.width = screenWidth / 3;
    attributes.width = screenWidth;
//    attributes.height = attributes.width;
    attributes.height = screenHeight;
    getWindow().setAttributes(attributes);
    setCancelable(mCancelable);

    TextView tvContent = findViewById(R.id.tv_content);

    final SpannableStringBuilder style = new SpannableStringBuilder();

    //设置文字
    style.append(
        "请你务必审慎阅读、充分理解'服务协议'和'隐私政策'个条款，包括但不限于我们需要收集你的设备信息、操作日志等个人信息。你可阅读《服务协议》和《隐私政策》了解详细信息。如你同意、请点击“同意”开始接受我们的服务。");

    //设置部分文字点击事件
    ClickableSpan clickableSpan = new ClickableSpan() {
      @Override
      public void onClick(View widget) {
        LogUtil.showDLog(TAG, "出发点击事件");
        mOnClickEvent.privacyPolicy();
      }

      @Override
      public void updateDrawState(TextPaint ds) {
        /**set textColor**/
        ds.setColor(ds.linkColor);
        /**Remove the underline**/
        ds.setUnderlineText(false);
      }
    };
    style.setSpan(clickableSpan, 63, 76, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvContent.setText(style);

    //设置部分文字颜色
    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#0000FF"));
    style.setSpan(foregroundColorSpan, 63, 76, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    //配置给TextView
    tvContent.setMovementMethod(LinkMovementMethod.getInstance());
    tvContent.setText(style);

    findViewById(R.id.tv_agree).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isShowing()) {
          mOnClickEvent.agree();
        }
      }
    });

    findViewById(R.id.tv_no_use).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mOnClickEvent.noUse();
      }
    });

  }

  @Override
  public void dismiss() {
//    mRotateAnimation.cancel();
    super.dismiss();
  }

  @Override
  public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      // 屏蔽返回键
      return mCancelable;
    }
    return super.onKeyDown(keyCode, event);
  }

  public interface OnClickEvent {

    void agree();

    void noUse();

    void privacyPolicy();
  }
}
