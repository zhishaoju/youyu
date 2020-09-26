package com.youyu.gao.xiao;

import static com.youyu.gao.xiao.utils.Contants.AD_BANNER;
import static com.youyu.gao.xiao.utils.Contants.AD_TENCENT_DISPLAY;
import static com.youyu.gao.xiao.utils.Contants.AD_TIME_OUT;
import static com.youyu.gao.xiao.utils.Contants.CHANNEL_ID;
import static com.youyu.gao.xiao.utils.Contants.Net.ADSRECORD_ADD;
import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.NOTICE_ADS;
import static com.youyu.gao.xiao.utils.Contants.USER_ID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.MainThread;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.google.gson.Gson;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.constants.LoadAdParams;
import com.youyu.gao.xiao.activity.BaseActivity;
import com.youyu.gao.xiao.activity.SplashTencentActivity;
import com.youyu.gao.xiao.applicatioin.TTAdManagerHolder;
import com.youyu.gao.xiao.bean.AdsBean;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.Contants;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 开屏广告Activity示例
 */
public class SplashActivity extends BaseActivity {

  private static final String TAG = "SplashActivity";
  private TTAdNative mTTAdNative;
  private FrameLayout mSplashContainer;
  //是否强制跳转到主页面
  private boolean mForceGoMain;

  private boolean mIsExpress = false; //是否请求模板广告

  // 腾讯广告开始
  /**
   * 记录拉取广告的时间
   //   */
//  private long fetchSplashADTime = 0;
//  private SplashAD splashAD;
//  private ViewGroup containerTencent;
//  private TextView skipView;
//  private static final String SKIP_TEXT = "点击跳过 %d";

  /**
   * 为防止无广告时造成视觉上类似于"闪退"的情况，设定无广告时页面跳转根据需要延迟一定时间，demo 给出的延时逻辑是从拉取广告开始算开屏最少持续多久，
   * 仅供参考，开发者可自定义延时逻辑，如果开发者采用demo 中给出的延时逻辑，也建议开发者考虑自定义minSplashTimeWhenNoAD的值（单位ms）
   **/
//  private int minSplashTimeWhenNoAD = 2000;
//  private Handler handler = new Handler(Looper.getMainLooper());
//  public boolean canJump = false;
//  private boolean needStartDemoList = true;
  // 腾讯广告结束
  @SuppressWarnings("RedundantCast")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // 设置锁屏下可展示，此配置仅限测试调试使用，正式代码慎用
//        UIUtils.setShowOnLocked(this);
    setContentView(R.layout.activity_splash);
    // 腾讯广告开始
//    containerTencent = (ViewGroup) this.findViewById(R.id.splash_container_tencent);
//    skipView = (TextView) findViewById(R.id.skip_view);
//    skipView.setVisibility(View.VISIBLE);
    // 腾讯广告结束
    mSplashContainer = (FrameLayout) findViewById(R.id.splash_container);
    //step2:创建TTAdNative对象
    mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
//    getExtraInfo();
    //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
    //在开屏时候申请不太合适，因为该页面倒计时结束或者请求超时会跳转，在该页面申请权限，体验不好
    // TTAdManagerHolder.getInstance(this).requestPermissionIfNecessary(this);
    //加载穿山甲开屏广告
//    loadSplashAd();

    initListener();
    getAddType();
//    startActivity(getSplashActivityIntent());
  }

  @Override
  protected void onPause() {
    super.onPause();
//    canJump = false;
  }

  private Intent getSplashActivityIntent() {
    Intent intent = new Intent(this, SplashTencentActivity.class);
    intent.putExtra("pos_id", AD_TENCENT_DISPLAY);
    intent.putExtra("need_logo", true);
    intent.putExtra("need_start_demo_list", false);
    intent.putExtra("custom_skip_btn", false);
    return intent;
  }

  private void initListener() {
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {

      }

      @Override
      public void success(String data) {
        try {
//          JSONObject jsonObject = new JSONObject(data);
//          int code = jsonObject.getInt("code");
          AdsBean adsBean = new Gson().fromJson(data, AdsBean.class);
          if (adsBean.code == 0) {
            SharedPrefsUtil.put(Contants.CSJ, adsBean.data.adsConfig.csj);
            SharedPrefsUtil.put(Contants.TX, adsBean.data.adsConfig.tx);

            if (Contants.CSJ.equals(adsBean.data.screen)) {
              //加载穿山甲开屏广告
              loadSplashAd();
            } else if (Contants.TX.equals(adsBean.data.screen)) {
              //加载腾讯开屏广告开始
              SplashAD splashAD = new SplashAD(SplashActivity.this, AD_TENCENT_DISPLAY, null);
              LoadAdParams params = new LoadAdParams();
              params.setLoginAppId("testAppId");
              params.setLoginOpenid("testOpenId");
              params.setUin("testUin");
              splashAD.setLoadAdParams(params);
              splashAD.preLoad();
              //加载腾讯开屏广告结束
              startActivity(getSplashActivityIntent());
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void getAddType() {
    String url = BASE_URL + NOTICE_ADS;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "getAddType e = " + e.getLocalizedMessage());
    }
    post(url, jsonObject.toString());
  }

  /**
   * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
   *
   * @param activity 展示广告的activity
   * @param adContainer 展示广告的大容器
   * @param skipContainer 自定义的跳过按钮：传入该view给SDK后，SDK会自动给它绑定点击跳过事件。SkipView的样式可以由开发者自由定制，其尺寸限制请参考activity_splash.xml或者接入文档中的说明。
   * @param posId 广告位ID
   * @param adListener 广告状态监听器
   * @param fetchDelay 拉取广告的超时时长：取值范围[3000, 5000]，设为0表示使用广点通SDK默认的超时时长。
   */
  private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
      String posId, SplashADListener adListener, int fetchDelay) {
//    fetchSplashADTime = System.currentTimeMillis();
//    splashAD = new SplashAD(activity, skipContainer, posId, adListener, fetchDelay);
//    splashAD.fetchAndShowIn(adContainer);
  }

//  private void getExtraInfo() {
//    Intent intent = getIntent();
//    if (intent == null) {
//      return;
//    }
//    String codeId = intent.getStringExtra("splash_rit");
//    if (!TextUtils.isEmpty(codeId)) {
//      mCodeId = codeId;
//    }
//    mIsExpress = intent.getBooleanExtra("is_express", false);
//  }

  @Override
  protected void onResume() {
    //判断是否该跳转到主页面
    if (mForceGoMain) {
      goToMainActivity();
    }
    super.onResume();
//    if (canJump) {
//      next();
//    }
//    canJump = true;
  }

  @Override
  protected void onStop() {
    super.onStop();
    mForceGoMain = true;
  }

  /**
   * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
   * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
   */
//  private void next() {
//    if (canJump) {
//      if (needStartDemoList) {
//        this.startActivity(new Intent(this, DemoListActivity.class));
//      }
//      this.finish();
//    } else {
//      canJump = true;
//    }
//  }

  /**
   * 加载开屏广告
   */
  private void loadSplashAd() {
    //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
    AdSlot adSlot = null;
    if (mIsExpress) {
      //个性化模板广告需要传入期望广告view的宽、高，单位dp，请传入实际需要的大小，
      //比如：广告下方拼接logo、适配刘海屏等，需要考虑实际广告大小
      float expressViewWidth = Utils.getScreenWidthDp(this);
      float expressViewHeight = Utils.getHeight(this);
      adSlot = new AdSlot.Builder()
          .setCodeId(AD_BANNER)
          .setSupportDeepLink(true)
          .setImageAcceptedSize(1080, 1920)
          //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
          .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
          .build();
    } else {
      adSlot = new AdSlot.Builder()
          .setCodeId(AD_BANNER)
          .setSupportDeepLink(true)
          .setImageAcceptedSize(1080, 1920)
          .build();
    }

    //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
    mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
      @Override
      @MainThread
      public void onError(int code, String message) {
        Log.d(TAG, String.valueOf(message));
        // showToast(message);
        goToMainActivity();
      }

      @Override
      @MainThread
      public void onTimeout() {
        // showToast("开屏广告加载超时");
        goToMainActivity();
      }

      @Override
      @MainThread
      public void onSplashAdLoad(TTSplashAd ad) {
        Log.d(TAG, "开屏广告请求成功");
        if (ad == null) {
          return;
        }
        //获取SplashView
        View view = ad.getSplashView();
        if (view != null && mSplashContainer != null && !SplashActivity.this.isFinishing()) {
          mSplashContainer.removeAllViews();
          //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
          mSplashContainer.addView(view);
          //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
          //ad.setNotAllowSdkCountdown();
        } else {
          goToMainActivity();
        }

        //设置SplashView的交互监听器
        ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
          @Override
          public void onAdClicked(View view, int type) {
            Log.d(TAG, "onAdClicked");
//            // showToast("开屏广告点击");
            Map<String, String> map = new HashMap<>();
            map.put("adsName", "1"); // 0:广点通 1:穿山甲 2:百度 3:adView
            map.put("adsType", "0"); // 0:开屏广告 1:视频激励广告 2：图文广告
            postAdsRecordAdd(map);
          }

          @Override
          public void onAdShow(View view, int type) {
            Log.d(TAG, "onAdShow");
//            // showToast("开屏广告展示");
          }

          @Override
          public void onAdSkip() {
            Log.d(TAG, "onAdSkip");
//            // showToast("开屏广告跳过");
            goToMainActivity();

          }

          @Override
          public void onAdTimeOver() {
            Log.d(TAG, "onAdTimeOver");
//            // showToast("开屏广告倒计时结束");
            goToMainActivity();
          }
        });
        if (ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
          ad.setDownloadListener(new TTAppDownloadListener() {
            boolean hasShow = false;

            @Override
            public void onIdle() {
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName,
                String appName) {
              if (!hasShow) {
//                // showToast("下载中...");
                hasShow = true;
              }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName,
                String appName) {
              // showToast("下载暂停...");

            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName,
                String appName) {
              // showToast("下载失败...");

            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
              // showToast("下载完成...");

            }

            @Override
            public void onInstalled(String fileName, String appName) {
              // showToast("安装完成...");

            }
          });
        }
      }
    }, AD_TIME_OUT);

  }

  /**
   * 跳转到主页面
   */
  private void goToMainActivity() {
    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
    startActivity(intent);
    mSplashContainer.removeAllViews();
    this.finish();
  }


  private void postAdsRecordAdd(Map params) {
    LogUtil.showDLog(TAG, "postAdsRecordAdd");
    if (params == null) {
      return;
    }
    String url = BASE_URL + ADSRECORD_ADD;
    params.put("userId", SharedPrefsUtil.get(USER_ID, ""));
    params.put("channel", SharedPrefsUtil.get(CHANNEL_ID, ""));
    params.put("adsCode", "4");
    params.put("activityType", "click");
    post(url, Utils.paramsConvertString(params));
  }

}
