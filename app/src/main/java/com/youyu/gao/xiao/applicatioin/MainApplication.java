package com.youyu.gao.xiao.applicatioin;

import android.Manifest.permission;
import android.app.Application;
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.AndroidMediaPlayerFactory;
import com.dueeeke.videoplayer.player.VideoViewConfig;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.qq.e.comm.managers.GDTADManager;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * @Author zsj
 * @Date 2020.04.08 23:20
 * @Commit
 */
public class MainApplication extends Application {

  private static MainApplication mInstance;

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
//    Bugly.init(getApplicationContext(), "70454defe9", false);
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

    //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
    TTAdManagerHolder.init(this);

    GDTADManager.getInstance().initWith(this, "1110952598");

    VideoViewManager.setConfig(VideoViewConfig.newBuilder()
        //使用使用IjkPlayer解码
        .setPlayerFactory(IjkPlayerFactory.create())
        //使用ExoPlayer解码
        .setPlayerFactory(ExoMediaPlayerFactory.create())
        //使用MediaPlayer解码
        .setPlayerFactory(AndroidMediaPlayerFactory.create())
        .build());
  }

}
