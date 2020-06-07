package com.youyu.applicatioin;

import android.Manifest;
import android.Manifest.permission;
import android.app.Application;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * @Author zsj
 * @Date 2020.04.08 23:20
 * @Commit
 */
public class MainApplication extends Application {

  public static MainApplication mInstance;

  public static MainApplication getInstance() {
    return mInstance;
  }

  // 要申请的权限
  private String[] permissions = {permission.WRITE_EXTERNAL_STORAGE,
      permission.INTERNET, permission.READ_PHONE_STATE, permission.WRITE_EXTERNAL_STORAGE
      , permission.READ_EXTERNAL_STORAGE};

  @Override
  public void onCreate() {
    super.onCreate();
    CrashReport.initCrashReport(this, "70454defe9", true);
    mInstance = this;
//    grantUriPermission(getCallingPackage(),
//        contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
    // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//      int len = permissions.length;
//      for (int i = 0; i < len; i++) {
//        // 检查该权限是否已经获取
//        int permissionStatus = ContextCompat.checkSelfPermission(this, permissions[i]);
//        // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
//        if (PackageManager.PERMISSION_GRANTED != permissionStatus) {
//          // 如果没有授予该权限，就去提示用户请求
//          showDialogTipUserRequestPermission();
//        }
//      }
//    }

  }

}
