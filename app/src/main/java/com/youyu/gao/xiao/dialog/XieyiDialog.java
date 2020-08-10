package com.youyu.gao.xiao.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;

/**
 * @Author zhisiyi
 * @Date 2020.08.07 23:38
 * @Comment
 */
public class XieyiDialog extends Dialog {

  private static final String TAG = XieyiDialog.class.getSimpleName();
  private boolean mCancelable;

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

    findViewById(R.id.tv_agree).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isShowing()) {
          dismiss();
          SharedPrefsUtil.put("agree", "agree");
        }
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
}
