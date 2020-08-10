package com.youyu.gao.xiao;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.VERSION_INFO;
import static com.youyu.gao.xiao.utils.Contants.REQUEST_PERMISSION_CODE;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.youyu.gao.xiao.activity.BaseActivity;
import com.youyu.gao.xiao.bean.UpdateVersion;
import com.youyu.gao.xiao.dialog.XieyiDialog;
import com.youyu.gao.xiao.fragment.IndexFragment;
import com.youyu.gao.xiao.fragment.SecondFragment;
import com.youyu.gao.xiao.fragment.ThirdFragment;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.service.PlayReqService;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;
import java.util.ArrayList;
import org.json.JSONObject;
import update.UpdateAppUtils;

public class MainActivity extends BaseActivity {

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

    initService();
    initListener();
//    sendCheckoutVersion();
    if (!"agree".equals(SharedPrefsUtil.get("agree", ""))) {
      XieyiDialog xieyiDialog = new XieyiDialog(this);
      xieyiDialog.show();
    }
  }

  private void sendCheckoutVersion() {
    String url = BASE_URL + VERSION_INFO;
    JSONObject jsonObject = new JSONObject();
    String param = jsonObject.toString();
    post(url, param);
  }

  private void initListener() {
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {
        LogUtil.showELog(TAG, "failure(Exception e) e:" + e.toString());
      }

      @Override
      public void success(String data) {
        try {
          JSONObject jsonObject = new JSONObject(data);
          int code = Utils.jsonObjectIntGetValue(jsonObject, "code");
          if (code == 0) {
            long currentCode = Utils.getVersionCode();
            if (jsonObject.has("data")) {
              String resultData = jsonObject.getJSONObject("data").toString();
              UpdateVersion updateVersion = new Gson().fromJson(resultData, UpdateVersion.class);

              LogUtil.showDLog(TAG, "updateVersion = " + updateVersion);
              LogUtil.showDLog(TAG, "currentCode = " + currentCode);

              if (currentCode < updateVersion.appVersion) {
                UpdateAppUtils
                    .getInstance()
                    .apkUrl(updateVersion.downUrl)
                    .updateTitle(updateVersion.updateTitle)
                    .updateContent(updateVersion.appDesc)
                    .update();
              }
            }
          }
        } catch (
            Exception e) {
          LogUtil.showELog(TAG, "success(Exception e) e:" + e.toString());
        }
      }
    });
  }

  private void initService() {
    Intent playReqService = new Intent(this, PlayReqService.class);
    startService(playReqService);
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
