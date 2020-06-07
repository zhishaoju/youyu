package com.youyu;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static com.youyu.utils.Contants.REQUEST_PERMISSION_CODE;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tencent.bugly.crashreport.CrashReport;
import com.youyu.fragment.IndexFragment;
import com.youyu.fragment.SecondFragment;
import com.youyu.fragment.ThirdFragment;
import com.youyu.utils.LogUtil;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

  private String TAG = MainActivity.class.getSimpleName();

  @BindView(R.id.iv_first)
  ImageView ivFirst;
  @BindView(R.id.ll_first)
  LinearLayout llFirst;
  @BindView(R.id.iv_second)
  ImageView ivSecond;
  @BindView(R.id.ll_second)
  LinearLayout llSecond;
  @BindView(R.id.iv_third)
  ImageView ivThird;
  @BindView(R.id.ll_third)
  LinearLayout llThird;

  @BindView(R.id.tv_first)
  TextView tvFirst;

  @BindView(R.id.tv_second)
  TextView tvSecond;

  @BindView(R.id.tv_third)
  TextView tvThird;

  private Fragment mIndexFragment;
  private Fragment mSecondFragment;
  private Fragment mThirdFragment;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!this.isTaskRoot()) {
      Intent intent = getIntent();
      if (intent != null) {
        String action = intent.getAction();
        if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
          finish();
          return;
        }
      }
    }
    initPermission();
    //无title
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    //全屏
    getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
        LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    setTabSelect(0);

  }

  @OnClick({R.id.ll_first, R.id.ll_second, R.id.ll_third})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.ll_first:
        setTabSelect(0);
        break;
      case R.id.ll_second:
        setTabSelect(1);
        break;
      case R.id.ll_third:
        setTabSelect(2);
        break;
    }
  }

  private void setTabSelect(int index) {
    reset();
    FragmentManager manager = getSupportFragmentManager();

    FragmentTransaction transaction = manager.beginTransaction();

    hide(transaction);

    switch (index) {
      case 0:
        mIndexFragment = manager.findFragmentByTag("tag1");
        if (mIndexFragment == null) {
          mIndexFragment = new IndexFragment();
          transaction.add(R.id.fragment_main, mIndexFragment, "tag1");
        } else {
          transaction.show(mIndexFragment);
        }

        ivFirst.setImageDrawable(getResources().getDrawable(R.mipmap.shouye2));
        tvFirst.setTextColor(getResources().getColor(R.color.main_color));
        break;
      case 1:
        mSecondFragment = manager.findFragmentByTag("tag2");

        if (mSecondFragment == null) {
          mSecondFragment = new SecondFragment();
          transaction.add(R.id.fragment_main, mSecondFragment, "tag2");
        } else {
          transaction.show(mSecondFragment);
        }
        ivSecond.setImageDrawable(getResources().getDrawable(R.mipmap.shouye4));
        tvSecond.setTextColor(getResources().getColor(R.color.main_color));
        break;
      case 2:
        mThirdFragment = manager.findFragmentByTag("tag3");
        if (mThirdFragment == null) {
          mThirdFragment = new ThirdFragment();
          transaction.add(R.id.fragment_main, mThirdFragment, "tag3");
        } else {
          transaction.show(mThirdFragment);
        }
        ivThird.setImageDrawable(getResources().getDrawable(R.mipmap.shouye6));
        tvThird.setTextColor(getResources().getColor(R.color.main_color));
        break;

      default:
        break;
    }
    transaction.commitAllowingStateLoss();
  }

  private void reset() {
    ivFirst.setImageDrawable(getResources().getDrawable(R.mipmap.shouye1));
    tvFirst.setTextColor(getResources().getColor(R.color.main_color2));

    ivSecond.setImageDrawable(getResources().getDrawable(R.mipmap.shouye3));
    tvSecond.setTextColor(getResources().getColor(R.color.main_color2));

    ivThird.setImageDrawable(getResources().getDrawable(R.mipmap.shouye5));
    tvThird.setTextColor(getResources().getColor(R.color.main_color2));
  }

  private void hide(FragmentTransaction tran) {

    if (mIndexFragment != null) {
      tran.hide(mIndexFragment);
    }

    if (mSecondFragment != null) {
      tran.hide(mSecondFragment);
    }

    if (mThirdFragment != null) {
      tran.hide(mThirdFragment);
    }
  }

  private void initPermission() {
    String permissions[] = {
        permission.INTERNET,
        ACCESS_NETWORK_STATE,
        permission.WRITE_SETTINGS,
        permission.ACCESS_WIFI_STATE,
        permission.CHANGE_WIFI_STATE,
        permission.SYSTEM_ALERT_WINDOW,
        permission.CAMERA,
        permission.MODIFY_AUDIO_SETTINGS,
        permission.RECORD_AUDIO,
        permission.WAKE_LOCK,
        permission.WRITE_EXTERNAL_STORAGE,
        permission.READ_PHONE_STATE,
        permission.KILL_BACKGROUND_PROCESSES,
        permission.ACCESS_COARSE_LOCATION,
        permission.ACCESS_FINE_LOCATION,
    };

    ArrayList<String> toApplyList = new ArrayList<String>();

    for (String perm : permissions) {
      if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
        toApplyList.add(perm);
        //进入到这里代表没有权限.
      }
    }
    String tmpList[] = new String[toApplyList.size()];
    if (!toApplyList.isEmpty()) {
      ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
    }

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    // 此处为android 6.0以上动态授权的回调，用户自行实现。
    LogUtil.showDLog(TAG, "onRequestPermissionsResult");
    switch (requestCode) {
      case REQUEST_PERMISSION_CODE: {
        boolean allPermissionGranted = true;
        for (int i = 0; i < grantResults.length; i++) {
          if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
            allPermissionGranted = false;
            LogUtil.showELog(TAG, "permission_denied");
          }
        }
        if (!allPermissionGranted) {
          Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
          intent.setData(Uri.parse("package:" + getPackageName()));
          startActivity(intent);
        }
      }
      break;
      default:
        break;
    }
  }
}
