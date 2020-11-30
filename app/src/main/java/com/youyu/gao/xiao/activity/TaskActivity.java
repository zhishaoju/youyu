package com.youyu.gao.xiao.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.google.gson.Gson;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.applicatioin.TTAdManagerHolder;
import com.youyu.gao.xiao.bean.AdsBean;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.youyu.gao.xiao.utils.Contants.AD_CHUAN_SHA_JIA_FULL_TASK;
import static com.youyu.gao.xiao.utils.Contants.AD_CHUAN_SHA_JIA_REWARD_TASK;
import static com.youyu.gao.xiao.utils.Contants.AD_CLICK_CSJ;
import static com.youyu.gao.xiao.utils.Contants.AD_CLICK_TX;
import static com.youyu.gao.xiao.utils.Contants.AD_TENCENT_CHA_PING;
import static com.youyu.gao.xiao.utils.Contants.AD_TYPE_KEY;
import static com.youyu.gao.xiao.utils.Contants.CHANNEL_ID;
import static com.youyu.gao.xiao.utils.Contants.Net.ADSRECORD_ADD;
import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.NOTICE_ADS;
import static com.youyu.gao.xiao.utils.Contants.USER_ID;

/**
 * @Author zhiyukai
 * @Date 2020.09.19 14:47
 * @Comment
 */
public class TaskActivity extends BaseActivity {

  private static final String TAG = "TaskActivity";

  private TTAdNative mTTAdNative;
  private FrameLayout mSplashContainer;

  private TTRewardVideoAd mttRewardVideoAd;
  private boolean mIsLoaded;
  private boolean mHasShowDownloadActive = false;

  // 腾讯激励广告开始
//  private RewardVideoAD rewardVideoAD;
//  private boolean adLoaded;//广告加载成功标志
//  private boolean videoCached;//视频素材文件下载完成标志
  // 腾讯激励广告结束

  // 腾讯插屏广告开始
  private UnifiedInterstitialAD iad;

//  private int mClickAds;

  private int netType; // 1:notices 2:add

//  private CountDownTimer mCountDownTimer;

//  private Long mTotalTime = 6 * 1000L;
//  private Long mInterval = 1000L;

  // csj full video start
  private TTFullScreenVideoAd mttFullVideoAd;
  // csj full video end

  private AdsBean adsBean;

  String type = "";


  // 腾讯插屏广告结束
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task);
    initListener();
    initValue();
    Intent i = getIntent();
    if (i != null) {
      // 0:广点通 1:穿山甲 2:百度 3:adView
//      int adKey = i.getIntExtra(AD_KEY, -1);
//      if (TX == adKey) {
//        loadTxChaPingAD();
//      } else if (CSJ == adKey) {
//
//      }
      type = i.getStringExtra(AD_TYPE_KEY);
    }
    getAddType();
  }

  private void loadTxChaPingAD() {
    iad = getIAD();
    setVideoOption();
    iad.loadAD();
  }

  private void initValue() {
    mSplashContainer = (FrameLayout) findViewById(R.id.splash_container);
    // 穿山甲激励广开始
    //step3:创建TTAdNative对象,用于调用广告请求接口
    mTTAdNative = TTAdManagerHolder.get().createAdNative(this);

    // 穿山甲激励广告结束

    // 腾讯激励广告开始
    // 1. 初始化激励视频广告

    // 腾讯激励广告结束

    // 倒计时
//    mCountDownTimer = new CountDownTimer(mTotalTime, mInterval) {
//      public void onTick(long millisUntilFinished) {
//        long time = millisUntilFinished / 1000;
//        if (time == 0) {
//          iad.close();
//          Log.d(TAG, "iad.close()");
////          loadAd(AD_CHUAN_SHA_JIA_REWARD_TASK, TTAdConstant.VERTICAL);
//        } else {
//
//        }
//      }
//
//      public void onFinish() {
//      }
//    };
  }


  private void initListener() {
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {
        LogUtil.showDLog(TAG, "e = " + e.getLocalizedMessage());
      }

      @Override
      public void success(String data) {
        if (netType == 1) {
          try {
            adsBean = new Gson().fromJson(data, AdsBean.class);
            if (adsBean.code == 0) {
              // 根据规则界面来显示是否是全屏或者是奖励广告
              if ("full".equals(type)) {
                // 此时改成加载穿山甲全屏广告
                loadAdFull(AD_CHUAN_SHA_JIA_FULL_TASK, TTAdConstant.VERTICAL);
              }
              if ("reward".equals(type)) {
                //加载穿山甲激励广告
                loadAd(AD_CHUAN_SHA_JIA_REWARD_TASK, TTAdConstant.VERTICAL);
              }
            }
          } catch (Exception e) {
            LogUtil.showELog(TAG, "initListener: e = " + e.getLocalizedMessage());
          }
        } else if (netType == 2) {
          LogUtil.showELog(TAG, "add record success");
        }
      }
    });
  }

  private void setVideoOption() {
    VideoOption.Builder builder = new VideoOption.Builder();
    VideoOption option = builder.build();
//    if(!btnNoOption.isChecked()){
//      option = builder.setAutoPlayMuted(btnMute.isChecked())
//          .setAutoPlayPolicy(networkSpinner.getSelectedItemPosition())
//          .setDetailPageMuted(btnDetailMute.isChecked())
//          .build();
//    }
//    iad.setVideoOption(option);
//    iad.setMinVideoDuration(getMinVideoDuration());
//    iad.setMaxVideoDuration(getMaxVideoDuration());

    /**
     * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前调用setVideoPlayPolicy，有助于提高视频广告的eCPM值 <br/>
     * 如果广告位仅支持图文广告，则无需调用
     */

    /**
     * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
     *
     * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
     *
     * 如自动播放策略为AutoPlayPolicy.WIFI，但此时用户网络为4G环境，在用户看来就是手工播放的
     */
    iad.setVideoPlayPolicy(getVideoPlayPolicy(option.getAutoPlayPolicy(), this));
  }

  public static int getVideoPlayPolicy(int autoPlayPolicy, Context context) {
    if (autoPlayPolicy == VideoOption.AutoPlayPolicy.ALWAYS) {
      return VideoOption.VideoPlayPolicy.AUTO;
    } else if (autoPlayPolicy == VideoOption.AutoPlayPolicy.WIFI) {
      ConnectivityManager cm = (ConnectivityManager) context
              .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      return wifiNetworkInfo != null && wifiNetworkInfo.isConnected()
              ? VideoOption.VideoPlayPolicy.AUTO
              : VideoOption.VideoPlayPolicy.MANUAL;
    } else if (autoPlayPolicy == VideoOption.AutoPlayPolicy.NEVER) {
      return VideoOption.VideoPlayPolicy.MANUAL;
    }
    return VideoOption.VideoPlayPolicy.UNKNOWN;
  }

  // 加载腾讯插屏广告开始
  private UnifiedInterstitialAD getIAD() {
    if (this.iad != null) {
      iad.close();
      iad.destroy();
      iad = null;
    }
    iad = new UnifiedInterstitialAD(this, AD_TENCENT_CHA_PING,
            new UnifiedInterstitialADListener() {
              @Override
              public void onADClicked() {
                LogUtil.showDLog(TAG,
                        "onADClicked : " + (iad.getExt() != null ? iad.getExt().get("clickUrl") : ""));
//                if (AD_CLICK_TX == mClickAds) {
                  Map<String, String> map = new HashMap<>();
                  map.put("adsName", "0"); // 0:广点通 1:穿山甲 2:百度 3:adView
                  map.put("adsType", "2"); //0：代表全屏；1：代表奖励
                  map.put("clickAds", AD_CLICK_TX + "");
                  postAdsRecordAdd(map);
//                }
              }

              @Override
              public void onADClosed() {
                LogUtil.showDLog(TAG, "onADClosed");

//          if (adsBean.data.taskOne == 1) {
//            //加载穿山甲激励广告
//            loadAd(AD_CHUAN_SHA_JIA_REWARD_TASK, TTAdConstant.VERTICAL);
//          } else if (adsBean.data.taskTwo == 2) {
//            // 加载腾讯插屏广告
//            loadTxChaPingAD();
//          } else {
//
//          }

//          if (txTotal < 1) {
//            finish();
//          } else {
//            loadTxChaPingAD();
////            loadAd(AD_CHUAN_SHA_JIA_REWARD_TASK, TTAdConstant.VERTICAL);
//          }
              }

              @Override
              public void onADExposure() {
                LogUtil.showDLog(TAG, "onADExposure");
              }

              @Override
              public void onADLeftApplication() {
                LogUtil.showDLog(TAG, "onADLeftApplication");
              }

              @Override
              public void onADOpened() {
                LogUtil.showDLog(TAG, "广点通 onADOpened");
              }

              @Override
              public void onADReceive() {
                // onADReceive之后才能调用getAdPatternType()
                if (iad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                  iad.setMediaListener(new UnifiedInterstitialMediaListener() {

                    @Override
                    public void onVideoComplete() {
                      LogUtil.showDLog(TAG, "广点通 onVideoComplete");
                    }

                    @Override
                    public void onVideoError(AdError adError) {
                      LogUtil.showDLog(TAG,
                              "广点通 onVideoError, code = " + adError.getErrorCode() + ", msg = " + adError
                                      .getErrorMsg());
                    }

                    @Override
                    public void onVideoInit() {
                      LogUtil.showDLog(TAG, "广点通 onVideoInit");
                    }

                    @Override
                    public void onVideoLoading() {
                      LogUtil.showDLog(TAG, "广点通 onVideoLoading");
                    }

                    @Override
                    public void onVideoPageClose() {
                      LogUtil.showDLog(TAG, "广点通 onVideoPageClose");
                    }

                    @Override
                    public void onVideoPageOpen() {
                      LogUtil.showDLog(TAG, "广点通 onVideoPageOpen");
                    }

                    @Override
                    public void onVideoPause() {
                      LogUtil.showDLog(TAG, "广点通 onVideoPause");
                    }

                    @Override
                    public void onVideoReady(long l) {
                      LogUtil.showDLog(TAG, "广点通 onVideoReady, duration = " + l);
                    }

                    @Override
                    public void onVideoStart() {
                      LogUtil.showDLog(TAG, "广点通 onVideoStart");
                    }
                  });
                }
                showAD();
                // onADReceive之后才可调用getECPM()
                LogUtil.showDLog(TAG, "广点通广告加载成功~");
                LogUtil.showDLog(TAG, "广点通 eCPMLevel tx = " + iad.getECPMLevel());
              }

              @Override
              public void onNoAD(AdError adError) {
                String msg = String.format(Locale.getDefault(), "广点通 onNoAD, error code: %d, error msg: %s",
                        adError.getErrorCode(), adError.getErrorMsg());
                LogUtil.showDLog(TAG, msg);
              }

              @Override
              public void onVideoCached() {
                LogUtil.showDLog(TAG, "广点通 onVideoCached");
              }
            });
    return iad;
  }

  private void showAD() {
    if (iad != null) {
      if (adsBean.data.clickAds == adsBean.data.adType) {
        Utils.show("点击这个广告有奖励~");
        LogUtil.showDLog(TAG, "点击tx有奖励");
      }
      iad.show();

//      mCountDownTimer.start();
    } else {
      Toast.makeText(this, "请加载广告后再进行展示 ！ ", Toast.LENGTH_LONG).show();
    }
  }
  // 加载腾讯插屏广告结束


  private void loadAd(final String codeId, int orientation) {
    //step4:创建广告请求参数AdSlot,具体参数含义参考文档
    AdSlot adSlot;
//    if (mIsExpress) {
    //个性化模板广告需要传入期望广告view的宽、高，单位dp，
    adSlot = new AdSlot.Builder()
            .setCodeId(codeId)
            .setSupportDeepLink(true)
            .setRewardName("金币") //奖励的名称
            .setRewardAmount(3)  //奖励的数量
            //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
            .setExpressViewAcceptedSize(500, 500)
            .setUserID("user123")//用户id,必传参数
            .setMediaExtra("media_extra") //附加参数，可选
            .setOrientation(
                    orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
            .build();
//    } else {
//      //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
//      adSlot = new AdSlot.Builder()
//          .setCodeId(codeId)
//          .setSupportDeepLink(true)
//          .setRewardName("金币") //奖励的名称
//          .setRewardAmount(3)  //奖励的数量
//          .setUserID("user123")//用户id,必传参数
//          .setMediaExtra("media_extra") //附加参数，可选
//          .setOrientation(
//              orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
//          .build();
//    }
    //step5:请求广告
    mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
      @Override
      public void onError(int code, String message) {
        LogUtil.showELog(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message));
        //TToast.show(RewardVideoActivity.this, message);
      }

      //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
      @Override
      public void onRewardVideoCached() {
        LogUtil.showELog(TAG, "Callback --> onRewardVideoCached");
        mIsLoaded = true;
        //TToast.show(RewardVideoActivity.this, "Callback --> rewardVideoAd video cached");

        if (mttRewardVideoAd != null && mIsLoaded) {
          //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
          //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

          //展示广告，并传入广告展示的场景
          if (adsBean.data.clickAds == 1) {
            Utils.show("点击这个广告有奖励~");
            LogUtil.showDLog(TAG, "点击这个穿山甲广告有奖励");
          }
          mttRewardVideoAd.showRewardVideoAd(TaskActivity.this,
                  TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
          mttRewardVideoAd = null;
        } else {
          Utils.show("请先加载广告");
        }
      }

      //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
      @Override
      public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
        LogUtil.showELog(TAG, "Callback --> onRewardVideoAdLoad");

        //TToast.show(RewardVideoActivity.this, "rewardVideoAd loaded 广告类型：" + getAdType(ad.getRewardVideoAdType()));
        mIsLoaded = false;
        mttRewardVideoAd = ad;
        mttRewardVideoAd
                .setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                  @Override
                  public void onAdShow() {
                    LogUtil.showELog(TAG, "Callback --> rewardVideoAd show");
                    //TToast.show(RewardVideoActivity.this, "rewardVideoAd show");
                  }

                  @Override
                  public void onAdVideoBarClick() {
                    LogUtil.showELog(TAG, "Callback --> rewardVideoAd bar click");
                    //TToast.show(RewardVideoActivity.this, "rewardVideoAd bar click");
//                    if (AD_CLICK_CSJ == mClickAds) {
                      Map<String, String> map = new HashMap<>();
                      map.put("adsName", "1"); // 0:广点通 1:穿山甲 2:百度 3:adView
                      map.put("adsType", "1"); // 0：代表全屏；1：代表奖励
                      map.put("clickAds", "1"); // clickAds的意义是：0：代表全屏；1：代表奖励
                      map.put("activityType", "click");
                      postAdsRecordAdd(map);
//                    }
                  }

                  @Override
                  public void onAdClose() {
                    LogUtil.showELog(TAG, "Callback --> rewardVideoAd close");
                    TaskActivity.this.setResult(RESULT_OK);
                    TaskActivity.this.finish();
//                videoPlayer.mediaController.clickPlay();
//              if (csjTotal < 1) {
//                TaskActivity.this.finish();
//              } else {
//                loadAd(AD_CHUAN_SHA_JIA_REWARD_TASK, TTAdConstant.VERTICAL);
//              }
                  }

                  //视频播放完成回调
                  @Override
                  public void onVideoComplete() {
                    LogUtil.showELog(TAG, "Callback --> rewardVideoAd complete");
                    //TToast.show(RewardVideoActivity.this, "rewardVideoAd complete");
//                rewardVideoAD.loadAD();

//                    if (AD_CLICK_CSJ == mClickAds) {
                      Map<String, String> map = new HashMap<>();
                      map.put("adsName", "1"); // 0:广点通 1:穿山甲 2:百度 3:adView
                      map.put("adsType", "1"); // 0：代表全屏；1：代表奖励
                      map.put("clickAds", "1"); // clickAds的意义是：0：代表全屏；1：代表奖励
                      map.put("activityType", "read");
                      postAdsRecordAdd(map);
//                    }

//                    if (adsBean != null) {
//                if (adsBean.data.adsConfig.tx && txTotal >= 1) {
//                  // 加载腾讯插屏广告
//                  loadTxChaPingAD();
//                } else if (adsBean.data.adsConfig.csj && csjTotal >= 1) {
//                  //加载穿山甲激励广告
//                  loadAd(AD_CHUAN_SHA_JIA_REWARD_TASK, TTAdConstant.VERTICAL);
//                } else {
//
//                }
//                    }
                  }

                  @Override
                  public void onVideoError() {
                    LogUtil.showELog(TAG, "Callback --> rewardVideoAd error");
                    //TToast.show(RewardVideoActivity.this, "rewardVideoAd error");
                  }

                  //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                  @Override
                  public void onRewardVerify(boolean rewardVerify, int rewardAmount,
                                             String rewardName) {
                    String logString = "verify:" + rewardVerify + " amount:" + rewardAmount +
                            " name:" + rewardName;
                    LogUtil.showELog(TAG, "Callback --> " + logString);
                    //TToast.show(RewardVideoActivity.this, logString);
                  }

                  @Override
                  public void onSkippedVideo() {
                    LogUtil.showELog(TAG, "Callback --> rewardVideoAd has onSkippedVideo");
                    //TToast.show(RewardVideoActivity.this, "rewardVideoAd has onSkippedVideo");
                  }
                });
        mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
          @Override
          public void onIdle() {
            mHasShowDownloadActive = false;
          }

          @Override
          public void onDownloadActive(long totalBytes, long currBytes, String fileName,
                                       String appName) {
            LogUtil.showDLog(TAG,
                    "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes
                            + ",fileName=" + fileName + ",appName=" + appName);

            if (!mHasShowDownloadActive) {
              mHasShowDownloadActive = true;
              //TToast.show(RewardVideoActivity.this, "下载中，点击下载区域暂停", Toast.LENGTH_LONG);
            }
          }

          @Override
          public void onDownloadPaused(long totalBytes, long currBytes, String fileName,
                                       String appName) {
            LogUtil.showDLog(TAG,
                    "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes
                            + ",fileName=" + fileName + ",appName=" + appName);
            //TToast.show(RewardVideoActivity.this, "下载暂停，点击下载区域继续", Toast.LENGTH_LONG);
          }

          @Override
          public void onDownloadFailed(long totalBytes, long currBytes, String fileName,
                                       String appName) {
            LogUtil.showDLog(TAG,
                    "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes
                            + ",fileName=" + fileName + ",appName=" + appName);
            //TToast.show(RewardVideoActivity.this, "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG);
          }

          @Override
          public void onDownloadFinished(long totalBytes, String fileName, String appName) {
            LogUtil.showDLog(TAG,
                    "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName
                            + ",appName=" + appName);
            //TToast.show(RewardVideoActivity.this, "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG);
          }

          @Override
          public void onInstalled(String fileName, String appName) {
            LogUtil
                    .showDLog(TAG, "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
            //TToast.show(RewardVideoActivity.this, "安装完成，点击下载区域打开", Toast.LENGTH_LONG);
          }
        });
      }
    });
  }

  /**
   * 为了获取是否点击
   */
  private void getAddType() {
    netType = 1;
    String url = BASE_URL + NOTICE_ADS;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "getAddType e = " + e.getLocalizedMessage());
    }
    post(url, jsonObject.toString());
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (iad != null) {
      iad.destroy();
    }

//    mCountDownTimer.cancel();
  }

  private void postAdsRecordAdd(Map params) {
    netType = 2;
    LogUtil.showDLog(TAG, "postAdsRecordAdd");
    if (params == null) {
      return;
    }
    String url = BASE_URL + ADSRECORD_ADD;
    params.put("userId", SharedPrefsUtil.get(USER_ID, ""));
    params.put("channel", SharedPrefsUtil.get(CHANNEL_ID, ""));
    params.put("adsCode", "4");
    post(url, Utils.paramsConvertString(params));
  }

  //加载全屏广告
  private void loadAdFull(String codeId, int orientation) {
    //step4:创建广告请求参数AdSlot,具体参数含义参考文档
    AdSlot adSlot;
//    if (mIsExpress) {
    adSlot = new AdSlot.Builder()
            .setCodeId(codeId)
            //模板广告需要设置期望个性化模板广告的大小,单位dp,全屏视频场景，只要设置的值大于0即可
            .setExpressViewAcceptedSize(500, 500)
            .build();
//
//    } else {
//    adSlot = new AdSlot.Builder()
//            .setCodeId(codeId)
//            .build();
//    }
    //step5:请求广告
    mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
      @Override
      public void onError(int code, String message) {
        Log.e(TAG, "full Callback --> onError: " + code + ", " + String.valueOf(message));
//        TToast.show(FullScreenVideoActivity.this, message);
      }

      @Override
      public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
        Log.e(TAG, "full Callback --> onFullScreenVideoAdLoad");

//        TToast.show(FullScreenVideoActivity.this, "FullVideoAd loaded  广告类型：" + getAdType(ad.getFullVideoAdType()));
        mttFullVideoAd = ad;
        mIsLoaded = false;
        //展示广告，并传入广告展示的场景
        mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

          @Override
          public void onAdShow() {
            Log.d(TAG, "full Callback --> FullVideoAd show");
//            TToast.show(FullScreenVideoActivity.this, "FullVideoAd show");
          }

          @Override
          public void onAdVideoBarClick() {
            Log.d(TAG, "full Callback --> FullVideoAd bar click");
//            TToast.show(FullScreenVideoActivity.this, "FullVideoAd bar click");

//            if (AD_CLICK_CSJ == mClickAds) {
              Map<String, String> map = new HashMap<>();
              map.put("adsName", "1"); // 0:广点通 1:穿山甲 2:百度 3:adView
              map.put("adsType", "0"); // 0：代表全屏；1：代表奖励
              map.put("clickAds", "0"); // clickAds的意义是：0：代表全屏；1：代表奖励
              map.put("activityType", "click"); // clickAds的意义是：0：代表全屏；1：代表奖励
              postAdsRecordAdd(map);
//            }
          }

          @Override
          public void onAdClose() {
            Log.d(TAG, "full Callback --> FullVideoAd close");
//            TToast.show(FullScreenVideoActivity.this, "FullVideoAd close");
            TaskActivity.this.setResult(RESULT_OK);
            TaskActivity.this.finish();
          }

          @Override
          public void onVideoComplete() {
            Log.d(TAG, "full Callback --> FullVideoAd complete");
//            TToast.show(FullScreenVideoActivity.this, "FullVideoAd complete");
//            if (AD_CLICK_CSJ == mClickAds) {
              Map<String, String> map = new HashMap<>();
              map.put("adsName", "1"); // 0:广点通 1:穿山甲 2:百度 3:adView
              map.put("adsType", "1"); // 0：代表全屏；1：代表奖励
              map.put("clickAds", "1"); // clickAds的意义是：0：代表全屏；1：代表奖励
              map.put("activityType", "read"); // clickAds的意义是：0：代表全屏；1：代表奖励
              postAdsRecordAdd(map);
//            }
          }

          @Override
          public void onSkippedVideo() {
            Log.d(TAG, "full Callback --> FullVideoAd skipped");
//            TToast.show(FullScreenVideoActivity.this, "FullVideoAd skipped");

          }

        });


        ad.setDownloadListener(new TTAppDownloadListener() {
          @Override
          public void onIdle() {
            mHasShowDownloadActive = false;
          }

          @Override
          public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
            Log.d("DML", "full onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);

            if (!mHasShowDownloadActive) {
              mHasShowDownloadActive = true;
//              TToast.show(FullScreenVideoActivity.this, "下载中，点击下载区域暂停", Toast.LENGTH_LONG);
            }
          }

          @Override
          public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
            Log.d("DML", "full onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
//            TToast.show(FullScreenVideoActivity.this, "下载暂停，点击下载区域继续", Toast.LENGTH_LONG);
          }

          @Override
          public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
            Log.d("DML", "full onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
//            TToast.show(FullScreenVideoActivity.this, "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG);
          }

          @Override
          public void onDownloadFinished(long totalBytes, String fileName, String appName) {
            Log.d("DML", "full onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
//            TToast.show(FullScreenVideoActivity.this, "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG);
          }

          @Override
          public void onInstalled(String fileName, String appName) {
            Log.d("DML", "full onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
//            TToast.show(FullScreenVideoActivity.this, "安装完成，点击下载区域打开", Toast.LENGTH_LONG);
          }
        });


        mttFullVideoAd.showFullScreenVideoAd(TaskActivity.this, TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
        mttFullVideoAd = null;
      }

      @Override
      public void onFullScreenVideoCached() {
        Log.e(TAG, "full Callback --> onFullScreenVideoCached");
        mIsLoaded = true;
//        TToast.show(FullScreenVideoActivity.this, "FullVideoAd video cached");
      }
    });
  }

}
