package com.youyu.gao.xiao.activity;

import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.NOTICE_ADS;
import static com.youyu.gao.xiao.utils.Contants.USER_ID;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.google.gson.Gson;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.util.AdError;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.applicatioin.TTAdManagerHolder;
import com.youyu.gao.xiao.bean.AdsBean;
import com.youyu.gao.xiao.dialog.DislikeDialog;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.Contants;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zhiyukai
 * @Date 2020.09.23 23:15
 * @Comment
 */
public class RuleActivity extends BaseActivity {

  private static final String TAG = RuleActivity.class.getSimpleName();
  @BindView(R.id.fl_back)
  FrameLayout flBack;
  @BindView(R.id.tv_title)
  TextView tvTitle;
  @BindView(R.id.tv_content)
  TextView tvContent;
  @BindView(R.id.tv_click_reminder)
  TextView tvClickReminder;
  @BindView(R.id.rl_title)
  RelativeLayout rlTitle;
  @BindView(R.id.bt_confirm)
  Button btConfirm;
  @BindView(R.id.tx_bannerContainer)
  FrameLayout txBannerContainer;
  @BindView(R.id.csj_express_container)
  FrameLayout csjExpressContainer;

  UnifiedBannerView bv;
  // 穿山甲广告开始
  private TTAdNative mTTAdNative;
  private TTAdDislike mTTAdDislike;
  private TTNativeExpressAd mTTAd;
  private long startTime = 0;
  private boolean mHasShowDownloadActive = false;
  // 穿山甲广告结束

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rule);
    ButterKnife.bind(this);
    initListener();
    netQuest();
    RuleActivity.this.getBanner().loadAD();
    initValue();
    initTTSDKConfig();
    loadCsjAd();
  }

  private void loadCsjAd() {
    loadExpressAd();
  }

  private void initTTSDKConfig() {
    //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象
    mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
    //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
    TTAdManagerHolder.get().requestPermissionIfNecessary(this);
  }

  private void initValue() {
    tvTitle.setText("任务规则");
  }

  private void netQuest() {
    String url = BASE_URL + NOTICE_ADS;
    Map<String, String> map = new HashMap<>();
    map.put("userId", SharedPrefsUtil.get(USER_ID, ""));
    post(url, Utils.paramsConvertString(map));
  }

  private void initListener() {
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {

      }

      @Override
      public void success(String data) {
        AdsBean adsBean = new Gson().fromJson(data, AdsBean.class);
        tvContent.setText(adsBean.data.content + "");
        tvClickReminder.setText(adsBean.data.adsConfig.clickAds + "");
      }
    });
  }

  @OnClick({R.id.fl_back, R.id.bt_confirm})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.fl_back:
        finish();
        break;
      case R.id.bt_confirm:
        startActivity(new Intent(this, TaskActivity.class));
        break;
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (bv != null) {
      bv.setLayoutParams(getUnifiedBannerLayoutParams());
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (bv != null) {
      bv.destroy();
    }
    if (mTTAd != null) {
      mTTAd.destroy();
    }
  }

  private UnifiedBannerView getBanner() {
    if (this.bv != null) {
      txBannerContainer.removeView(bv);
      bv.destroy();
    }
    this.bv = new UnifiedBannerView(this, Contants.AD_TENCENT_BANNER,
        new UnifiedBannerADListener() {

          @Override
          public void onADClicked() {
            LogUtil.showDLog(TAG, "onADClicked");
          }

          @Override
          public void onADCloseOverlay() {
            LogUtil.showDLog(TAG, "onADCloseOverlay");
          }

          @Override
          public void onADClosed() {
            LogUtil.showDLog(TAG, "onADClosed");
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
          public void onADOpenOverlay() {
            LogUtil.showDLog(TAG, "onADOpenOverlay");
          }

          @Override
          public void onADReceive() {
            LogUtil.showDLog(TAG, "onADReceive");
          }

          @Override
          public void onNoAD(AdError adError) {
            LogUtil.showDLog(TAG, "onNoAD");
          }
        });
    this.bv.setRefresh(5);// 5s刷新一次
    // 不需要传递tags使用下面构造函数
    // this.bv = new UnifiedBannerView(this, Constants.APPID, posId, this);
    txBannerContainer.addView(bv, getUnifiedBannerLayoutParams());
    return this.bv;
  }

  /**
   * banner2.0规定banner宽高比应该为6.4:1 , 开发者可自行设置符合规定宽高比的具体宽度和高度值
   */
  private FrameLayout.LayoutParams getUnifiedBannerLayoutParams() {
    Point screenSize = new Point();
    getWindowManager().getDefaultDisplay().getSize(screenSize);
    return new FrameLayout.LayoutParams(screenSize.x, Math.round(screenSize.x / 6.4F));
  }

  /**
   * 设置穿山甲的宽高
   */
  private FrameLayout.LayoutParams getCsjUnifiedBannerLayoutParams() {
    Point screenSize = new Point();
    getWindowManager().getDefaultDisplay().getSize(screenSize);
    return new FrameLayout.LayoutParams(screenSize.x, Math.round(screenSize.x / 6.4F));
  }

  private void loadExpressAd() {
    csjExpressContainer.removeAllViews();
    Point screenSize = new Point();
    getWindowManager().getDefaultDisplay().getSize(screenSize);
    int w = Utils.px2dip(this, screenSize.x);
    int h = Utils.px2dip(this, 400);
    //step4:创建广告请求参数AdSlot,具体参数含义参考文档
    AdSlot adSlot = new AdSlot.Builder()
        .setCodeId(Contants.AD_CHUAN_SHA_JIA_BANNER_TASK) //广告位id
        .setSupportDeepLink(true)
        .setAdCount(3) //请求广告数量为1到3条
//        .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //期望模板广告view的size,单位dp
        .setExpressViewAcceptedSize(w, h)
        .build();
    //step5:请求广告，对请求回调的广告作渲染处理
    mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
      @Override
      public void onError(int code, String message) {
        LogUtil.showDLog(TAG, "load error : " + code + ", " + message);
        csjExpressContainer.removeAllViews();
      }

      @Override
      public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
        if (ads == null || ads.size() == 0) {
          return;
        }
        mTTAd = ads.get(0);
        mTTAd.setSlideIntervalTime(30 * 1000);
        bindAdListener(mTTAd);
        startTime = System.currentTimeMillis();
        mTTAd.render();
      }
    });
  }

  private void bindAdListener(TTNativeExpressAd ad) {
    ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
      @Override
      public void onAdClicked(View view, int type) {
        LogUtil.showDLog(TAG, "广告被点击 type = " + type);
      }

      @Override
      public void onAdShow(View view, int type) {
        LogUtil.showDLog(TAG, "广告展示 type = " + type);
      }

      @Override
      public void onRenderFail(View view, String msg, int code) {
        LogUtil.showDLog(TAG, "render fail:" + (System.currentTimeMillis() - startTime));
      }

      @Override
      public void onRenderSuccess(View view, float width, float height) {
        Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
        //返回view的宽高 单位 dp
        LogUtil.showDLog(TAG, "渲染成功");
        csjExpressContainer.removeAllViews();
        csjExpressContainer.addView(view);
      }
    });
    //dislike设置
    bindDislike(ad, false);
    if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
      return;
    }
    ad.setDownloadListener(new TTAppDownloadListener() {
      @Override
      public void onIdle() {
        LogUtil.showDLog(TAG, "点击开始下载");
      }

      @Override
      public void onDownloadActive(long totalBytes, long currBytes, String fileName,
                                   String appName) {
        if (!mHasShowDownloadActive) {
          mHasShowDownloadActive = true;
          LogUtil.showDLog(TAG, "下载中，点击暂停");
        }
      }

      @Override
      public void onDownloadPaused(long totalBytes, long currBytes, String fileName,
                                   String appName) {
        LogUtil.showDLog(TAG, "下载暂停，点击继续");
      }

      @Override
      public void onDownloadFailed(long totalBytes, long currBytes, String fileName,
                                   String appName) {
        LogUtil.showDLog(TAG, "下载失败，点击重新下载");
      }

      @Override
      public void onInstalled(String fileName, String appName) {
        LogUtil.showDLog(TAG, "安装完成，点击图片打开");
      }

      @Override
      public void onDownloadFinished(long totalBytes, String fileName, String appName) {
        LogUtil.showDLog(TAG, "点击安装");
      }
    });
  }

  /**
   * 设置广告的不喜欢, 注意：强烈建议设置该逻辑，如果不设置dislike处理逻辑，则模板广告中的 dislike区域不响应dislike事件。
   *
   * @param customStyle 是否自定义样式，true:样式自定义
   */
  private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
    if (customStyle) {
      //使用自定义样式
      List<FilterWord> words = ad.getFilterWords();
      if (words == null || words.isEmpty()) {
        return;
      }

      final DislikeDialog dislikeDialog = new DislikeDialog(this, words);
      dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
        @Override
        public void onItemClick(FilterWord filterWord) {
          //屏蔽广告
          LogUtil.showDLog(TAG, "点击");
          //用户选择不喜欢原因后，移除广告展示
          csjExpressContainer.removeAllViews();
        }
      });
      ad.setDislikeDialog(dislikeDialog);
      return;
    }
    //使用默认模板中默认dislike弹出样式
    ad.setDislikeCallback(this, new TTAdDislike.DislikeInteractionCallback() {
      @Override
      public void onSelected(int position, String value) {
        LogUtil.showDLog(TAG, "ad 点击");
        //用户选择不喜欢原因后，移除广告展示
        csjExpressContainer.removeAllViews();
      }

      @Override
      public void onCancel() {
        LogUtil.showDLog(TAG, "ad 点击取消");
      }

      @Override
      public void onRefuse() {

      }

    });
  }
}
