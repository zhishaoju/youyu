package com.youyu.gao.xiao.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.google.gson.Gson;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.applicatioin.TTAdManagerHolder;
import com.youyu.gao.xiao.bean.AdsBean;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.Contants;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.youyu.gao.xiao.utils.Contants.AD_CHUAN_SHA_JIA_REWARD_TASK;
import static com.youyu.gao.xiao.utils.Contants.AD_CLICK_CSJ;
import static com.youyu.gao.xiao.utils.Contants.AD_CLICK_TX;
import static com.youyu.gao.xiao.utils.Contants.AD_TENCENT_REWARD_TASK;
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
public class TaskActivity1 extends BaseActivity {

  private static final String TAG = "TaskActivity";

  private TTAdNative mTTAdNative;
  private FrameLayout mSplashContainer;

  private TTRewardVideoAd mttRewardVideoAd;
  private boolean mIsLoaded;
  private boolean mHasShowDownloadActive = false;

  // 腾讯激励广告开始
  private RewardVideoAD rewardVideoAD;
  private boolean adLoaded;//广告加载成功标志
  private boolean videoCached;//视频素材文件下载完成标志
  // 腾讯激励广告结束

  // 腾讯插屏广告开始
  private UnifiedInterstitialAD iad;

  private int csjTotal;
  private int txTotal;
  private int mClickAds;

  private int netType; // 1:notices 2:add

  // 腾讯插屏广告结束
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task);
    initListener();
    getAddType();
    initValue();
//    loadTxChaPingAD();
  }

  private void loadTxChaPingAD() {
    txTotal--;
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
    rewardVideoAD = new RewardVideoAD(this, AD_TENCENT_REWARD_TASK, new RewardVideoADListener() {
      //    rewardVideoAD = new RewardVideoAD(this, "6040295592058680", new RewardVideoADListener() {
      @Override
      public void onADClick() {
        LogUtil.showDLog(TAG, "onADClick");
      }

      @Override
      public void onADClose() {
        LogUtil.showDLog(TAG, "onADClose");
        TaskActivity1.this.finish();
      }

      @Override
      public void onADExpose() {
        LogUtil.showDLog(TAG, "onADExpose");
      }

      @Override
      public void onADLoad() {
        LogUtil.showDLog(TAG, "onADLoad");
        adLoaded = true;
        String msg = "load ad success ! expireTime = " + new Date(System.currentTimeMillis() +
            rewardVideoAD.getExpireTimestamp() - SystemClock.elapsedRealtime());
        Toast.makeText(TaskActivity1.this, msg, Toast.LENGTH_LONG).show();
        LogUtil.showDLog(TAG, "onADLoad msg = " + msg);
        if (rewardVideoAD.getRewardAdType() == RewardVideoAD.REWARD_TYPE_VIDEO) {
          Log.d(TAG,
              "eCPMLevel chuan = " + rewardVideoAD.getECPMLevel() + " ,video duration = "
                  + rewardVideoAD
                  .getVideoDuration());
        } else if (rewardVideoAD.getRewardAdType() == RewardVideoAD.REWARD_TYPE_PAGE) {
          Log.d(TAG, "eCPMLevel chuan = " + rewardVideoAD.getECPMLevel());
        }

        // 3. 展示激励视频广告
        if (adLoaded
            && rewardVideoAD != null) {//广告展示检查1：广告成功加载，此处也可以使用videoCached来实现视频预加载完成后再展示激励视频广告的逻辑
          if (!rewardVideoAD.hasShown()) {//广告展示检查2：当前广告数据还没有展示过
            long delta = 1000;//建议给广告过期时间加个buffer，单位ms，这里demo采用1000ms的buffer
            //广告展示检查3：展示广告前判断广告数据未过期
            if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {
//              if (view.getId() == R.id.show_ad_button) {
//                rewardVideoAD.showAD();
//              } else {
//                rewardVideoAD.showAD(RewardVideoActivity.this);
//              }
              rewardVideoAD.showAD(TaskActivity1.this);
            } else {
              Toast.makeText(TaskActivity1.this, "激励视频广告已过期，请再次请求广告后进行广告展示！", Toast.LENGTH_LONG)
                  .show();
            }
          } else {
            Toast.makeText(TaskActivity1.this, "此条广告已经展示过，请再次请求广告后进行广告展示！", Toast.LENGTH_LONG)
                .show();
          }
        } else {
          Toast.makeText(TaskActivity1.this, "成功加载广告后再进行广告展示！", Toast.LENGTH_LONG).show();
        }
      }

      @Override
      public void onADShow() {
        LogUtil.showDLog(TAG, "onADShow");
      }

      @Override
      public void onError(AdError adError) {
        LogUtil.showDLog(TAG, "onError");
        String msg = String.format(Locale.getDefault(), "onError, error code: %d, error msg: %s",
            adError.getErrorCode(), adError.getErrorMsg());
        LogUtil.showDLog(TAG, "onError msg = " + msg);
//        Toast.makeText(TaskActivity.this, msg, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onReward() {
        LogUtil.showDLog(TAG, "onReward");
      }

      @Override
      public void onVideoCached() {
        LogUtil.showDLog(TAG, "onVideoCached");
      }

      @Override
      public void onVideoComplete() {
        LogUtil.showDLog(TAG, "onVideoComplete");
      }
    }, true);
    adLoaded = false;
    videoCached = false;

    // 腾讯激励广告结束
  }

  private void initListener() {
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {

      }

      @Override
      public void success(String data) {
        if (netType == 1) {
          try {
            AdsBean adsBean = new Gson().fromJson(data, AdsBean.class);
            if (adsBean.code == 0) {
              SharedPrefsUtil.put(Contants.CSJ, adsBean.data.adsConfig.csj);
              SharedPrefsUtil.put(Contants.TX, adsBean.data.adsConfig.tx);
              csjTotal = Integer.valueOf(adsBean.data.adsConfig.csjTotal);
              txTotal = Integer.valueOf(adsBean.data.adsConfig.txTotal);
              mClickAds = adsBean.data.adsConfig.clickAds;

              if (adsBean.data.adsConfig.csj && csjTotal >= 1) {
                //加载穿山甲激励广告
                loadAd(AD_CHUAN_SHA_JIA_REWARD_TASK, TTAdConstant.VERTICAL);
              }
              if (adsBean.data.adsConfig.tx && txTotal >= 1) {
                // 2. 加载激励视频广告
//              rewardVideoAD.loadAD();
                // 加载腾讯插屏广告
                loadTxChaPingAD();
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
    iad = new UnifiedInterstitialAD(this, Contants.AD_TENCENT_CHA_PING,
        new UnifiedInterstitialADListener() {
          @Override
          public void onADClicked() {
            LogUtil.showDLog(TAG,
                "onADClicked : " + (iad.getExt() != null ? iad.getExt().get("clickUrl") : ""));
            if (AD_CLICK_TX == mClickAds) {
              Map<String, String> map = new HashMap<>();
              map.put("adsName", "0"); // 0:广点通 1:穿山甲 2:百度 3:adView
              map.put("adsType", "2"); // 0:开屏广告 1:视频激励广告 2：图文广告
              map.put("clickAds", AD_CLICK_TX + "");
              postAdsRecordAdd(map);
            }
          }

          @Override
          public void onADClosed() {
            LogUtil.showDLog(TAG, "onADClosed");
            if (txTotal < 1) {
              finish();
            } else {
              loadTxChaPingAD();
            }
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
            LogUtil.showDLog(TAG, "onADOpened");
          }

          @Override
          public void onADReceive() {
            // onADReceive之后才能调用getAdPatternType()
            if (iad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
              iad.setMediaListener(new UnifiedInterstitialMediaListener() {

                @Override
                public void onVideoComplete() {
                  LogUtil.showDLog(TAG, "onVideoComplete");
                }

                @Override
                public void onVideoError(AdError adError) {
                  LogUtil.showDLog(TAG,
                      "onVideoError, code = " + adError.getErrorCode() + ", msg = " + adError
                          .getErrorMsg());
                }

                @Override
                public void onVideoInit() {
                  LogUtil.showDLog(TAG, "onVideoInit");
                }

                @Override
                public void onVideoLoading() {
                  LogUtil.showDLog(TAG, "onVideoLoading");
                }

                @Override
                public void onVideoPageClose() {
                  LogUtil.showDLog(TAG, "onVideoPageClose");
                }

                @Override
                public void onVideoPageOpen() {
                  LogUtil.showDLog(TAG, "onVideoPageOpen");
                }

                @Override
                public void onVideoPause() {
                  LogUtil.showDLog(TAG, "onVideoPause");
                }

                @Override
                public void onVideoReady(long l) {
                  LogUtil.showDLog(TAG, "onVideoReady, duration = " + l);
                }

                @Override
                public void onVideoStart() {
                  LogUtil.showDLog(TAG, "onVideoStart");
                }
              });
            }
            showAD();
            // onADReceive之后才可调用getECPM()
            LogUtil.showDLog(TAG, "广告加载成功~");
            LogUtil.showDLog(TAG, "eCPMLevel tx = " + iad.getECPMLevel());
          }

          @Override
          public void onNoAD(AdError adError) {
            String msg = String.format(Locale.getDefault(), "onNoAD, error code: %d, error msg: %s",
                adError.getErrorCode(), adError.getErrorMsg());
            LogUtil.showDLog(TAG, msg);
          }

          @Override
          public void onVideoCached() {
            LogUtil.showDLog(TAG, "onVideoCached");
          }
        });
    return iad;
  }

  private void showAD() {
    if (iad != null) {
      Utils.show("点击这个广告有奖励~");
      LogUtil.showDLog(TAG, "点击tx有奖励");
      iad.show();
    } else {
      Toast.makeText(this, "请加载广告后再进行展示 ！ ", Toast.LENGTH_LONG).show();
    }
  }
  // 加载腾讯插屏广告结束


  private void loadAd(final String codeId, int orientation) {
    csjTotal--;
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
          Utils.show("点击这个广告有奖励~");
          LogUtil.showDLog(TAG, "点击csj有奖励");
          mttRewardVideoAd.showRewardVideoAd(TaskActivity1.this,
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
                if (AD_CLICK_CSJ == mClickAds) {
                  Map<String, String> map = new HashMap<>();
                  map.put("adsName", "1"); // 0:广点通 1:穿山甲 2:百度 3:adView
                  map.put("adsType", "1"); // 0:开屏广告 1:视频激励广告 2：图文广告
                  map.put("clickAds", AD_CLICK_CSJ + "");
                  postAdsRecordAdd(map);
                }
              }

              @Override
              public void onAdClose() {
                LogUtil.showELog(TAG, "Callback --> rewardVideoAd close");
                //TToast.show(RewardVideoActivity.this, "rewardVideoAd close");
//                videoPlayer.mediaController.clickPlay();
                if (csjTotal < 1) {
                  TaskActivity1.this.finish();
                } else {
                  loadAd(AD_CHUAN_SHA_JIA_REWARD_TASK, TTAdConstant.VERTICAL);
                }
              }

              //视频播放完成回调
              @Override
              public void onVideoComplete() {
                LogUtil.showELog(TAG, "Callback --> rewardVideoAd complete");
                //TToast.show(RewardVideoActivity.this, "rewardVideoAd complete");
                rewardVideoAD.loadAD();
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
    params.put("activityType", "click");
    post(url, Utils.paramsConvertString(params));
  }

}
