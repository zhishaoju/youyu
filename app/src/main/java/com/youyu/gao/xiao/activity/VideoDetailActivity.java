package com.youyu.gao.xiao.activity;

import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;
import static com.youyu.gao.xiao.utils.Contants.AD_CHUAN_SHA_JIA_REWARD_WAIT;
import static com.youyu.gao.xiao.utils.Contants.AD_TENCENT_REWARD_WAIT;
import static com.youyu.gao.xiao.utils.Contants.BroadcastConst.RECORD_ACTION;
import static com.youyu.gao.xiao.utils.Contants.BroadcastConst.RECORD_STATUS;
import static com.youyu.gao.xiao.utils.Contants.CHANNEL_ID;
import static com.youyu.gao.xiao.utils.Contants.Net.ADSRECORD_ADD;
import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.COLLECTION_ADD;
import static com.youyu.gao.xiao.utils.Contants.Net.COMMENT_DETAIL;
import static com.youyu.gao.xiao.utils.Contants.Net.COMMENT_LIST;
import static com.youyu.gao.xiao.utils.Contants.Net.NOTICE_ADS;
import static com.youyu.gao.xiao.utils.Contants.Net.POST_UPDATE;
import static com.youyu.gao.xiao.utils.Contants.Net.SEND_COMMENT;
import static com.youyu.gao.xiao.utils.Contants.PAGE_SIZE;
import static com.youyu.gao.xiao.utils.Contants.POST_ID;
import static com.youyu.gao.xiao.utils.Contants.USER_ID;
import static com.youyu.gao.xiao.utils.Contants.USER_PHONE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoView.OnStateChangeListener;
import com.google.gson.Gson;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.adapter.VideoDetailCommentListAdapter;
import com.youyu.gao.xiao.applicatioin.TTAdManagerHolder;
import com.youyu.gao.xiao.bean.AdsBean;
import com.youyu.gao.xiao.bean.CommentBean;
import com.youyu.gao.xiao.bean.VideoPlayerItemInfo;
import com.youyu.gao.xiao.cusListview.CusRecycleView;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.Contants;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;
import com.youyu.gao.xiao.view.CircleImageView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author zhisiyi
 * @Date 2020.04.23 18:46
 * @Comment
 */
public class VideoDetailActivity extends BaseActivity {

  private String TAG = VideoDetailActivity.class.getSimpleName();
  @BindView(R.id.iv_bg)
  ImageView ivBg;
  @BindView(R.id.videoPlayer)
  VideoView videoPlayer;
  @BindView(R.id.fl_back)
  FrameLayout flBack;
  @BindView(R.id.civ_head_pic)
  CircleImageView civHeadPic;
  @BindView(R.id.tv_user_name)
  TextView tvUserName;
  @BindView(R.id.tv_time_num)
  TextView tvTimeNum;
  @BindView(R.id.ll_attention)
  LinearLayout llAttention;
  @BindView(R.id.tv_content_desc)
  TextView tvContentDesc;
  @BindView(R.id.content_view)
  CusRecycleView pinglunListView;

  @BindView(R.id.pull_to_refresh)
  PullToRefreshLayout pullToRefreshLayout;
  @BindView(R.id.et_comment_content)
  EditText etCommentContent;
  @BindView(R.id.iv_video_zan)
  ImageView ivVideoZan;
  @BindView(R.id.tv_video_zan)
  TextView tvVideoZan;
  @BindView(R.id.ll_zan)
  LinearLayout llZan;
  @BindView(R.id.iv_video_pinglun)
  ImageView ivVideoPinglun;
  @BindView(R.id.tv_video_pinglun)
  TextView tvVideoPinglun;
  @BindView(R.id.ll_pinglun)
  LinearLayout llPinglun;
  @BindView(R.id.iv_video_shoucang)
  ImageView ivVideoShoucang;
  @BindView(R.id.tv_video_shoucang)
  TextView tvVideoShoucang;
  @BindView(R.id.ll_shoucang)
  LinearLayout llShoucang;
  @BindView(R.id.iv_video_share)
  ImageView ivVideoShare;
  @BindView(R.id.tv_video_share)
  TextView tvVideoShare;
  @BindView(R.id.ll_share)
  LinearLayout llShare;
  @BindView(R.id.ll_comment)
  LinearLayout llComment;

  private String mPostId;

  private Intent mIntent;

  //1：加载详情; 2:评论列表；3: 发表评论；4:收藏；5:点赞; 6:显示广告；7：点击记录
  private int flag = -1;

  private VideoPlayerItemInfo mVideoPlayerItemInfo;

  private int mPageNumer = 1;
  private int mRefresh; // =1 代表刷新；=2 代表加载更多
  private int pageSize = PAGE_SIZE;
  private int mTotal;

  private ArrayList<CommentBean> mData = new ArrayList<>();
  private VideoDetailCommentListAdapter mCommentListAdapter;
  private LinearLayoutManager lm;

  private TTAdNative mTTAdNative;
  private TTRewardVideoAd mttRewardVideoAd;
  private boolean mIsLoaded;
  private boolean mHasShowDownloadActive = false;

  StandardVideoController controller;

  // 腾讯激励广告开始
  private RewardVideoAD rewardVideoAD;
  private boolean adLoaded;//广告加载成功标志
  private boolean videoCached;//视频素材文件下载完成标志
  private boolean mNoLoad = true;

  // 腾讯激励广告结束

  // 腾讯插屏广告开始
  private UnifiedInterstitialAD iad;

  // 腾讯插屏广告结束
  private int netType; // 1:notices 2:add
  private int mClickAds = -1;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_video_detail);
    ButterKnife.bind(this);
    etCommentContent.setInputType(TYPE_TEXT_FLAG_MULTI_LINE);
    etCommentContent.setSingleLine(false);
    initValue();
    initListener();
    getComment();
  }


  private void initValue() {
    controller = new StandardVideoController(this);
    mCommentListAdapter = new VideoDetailCommentListAdapter(this);
    controller.addDefaultControlComponent("标题", false);
    videoPlayer.setVideoController(controller); //设置控制器
    //初始化RecyclerView
    lm = new LinearLayoutManager(this);
    pinglunListView.setLayoutManager(lm);
    pinglunListView.setAdapter(mCommentListAdapter);

    //step3:创建TTAdNative对象,用于调用广告请求接口
    mTTAdNative = TTAdManagerHolder.get().createAdNative(this);

    // 腾讯激励广告开始
    // 1. 初始化激励视频广告
    rewardVideoAD = new RewardVideoAD(this, AD_TENCENT_REWARD_WAIT, new RewardVideoADListener() {
      @Override
      public void onADClick() {
        LogUtil.showDLog(TAG, "onADClick");
      }

      @Override
      public void onADClose() {
        LogUtil.showDLog(TAG, "onADClose");

        videoPlayer.start(); //开始播放，不调用则不自动播放
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
        Toast.makeText(VideoDetailActivity.this, msg, Toast.LENGTH_LONG).show();

        if (rewardVideoAD.getRewardAdType() == RewardVideoAD.REWARD_TYPE_VIDEO) {
          Log.d(TAG,
              "eCPMLevel = " + rewardVideoAD.getECPMLevel() + " ,video duration = " + rewardVideoAD
                  .getVideoDuration());
        } else if (rewardVideoAD.getRewardAdType() == RewardVideoAD.REWARD_TYPE_PAGE) {
          Log.d(TAG, "eCPMLevel = " + rewardVideoAD.getECPMLevel());
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
              rewardVideoAD.showAD(VideoDetailActivity.this);
            } else {
              Toast.makeText(VideoDetailActivity.this, "激励视频广告已过期，请再次请求广告后进行广告展示！",
                  Toast.LENGTH_LONG)
                  .show();
            }
          } else {
            Toast.makeText(VideoDetailActivity.this, "此条广告已经展示过，请再次请求广告后进行广告展示！", Toast.LENGTH_LONG)
                .show();
          }
        } else {
          Toast.makeText(VideoDetailActivity.this, "成功加载广告后再进行广告展示！", Toast.LENGTH_LONG).show();
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
        Toast.makeText(VideoDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
        videoPlayer.start(); //开始播放，不调用则不自动播放
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

  @Override
  protected void onResume() {
    super.onResume();
    videoPlayer.resume();
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    videoPlayer.release();
    if (iad != null) {
      iad.destroy();
    }
  }

  private void getComment() {
    flag = 1;
    String url = BASE_URL + COMMENT_DETAIL;
    mIntent = getIntent();
    if (mIntent != null) {
      mPostId = mIntent.getStringExtra("postId");
      SharedPrefsUtil.put(POST_ID, mPostId);
    }
    String params = "";
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      jsonObject.put("postId", mPostId);
      params = jsonObject.toString();
    } catch (Exception e) {
      LogUtil.showELog(TAG, "onResume e is " + e.getLocalizedMessage());
    }

    post(url, params);
  }

  private void initListener() {

    videoPlayer.addOnStateChangeListener(new OnStateChangeListener() {
      @Override
      public void onPlayerStateChanged(int playerState) {
        LogUtil.showDLog(TAG, "playerState = " + playerState);
      }

      @Override
      public void onPlayStateChanged(int playState) {
        LogUtil.showDLog(TAG, "playState = " + playState);
        if (4 == playState || 0 == playState) {
          // 暂停
          stopTime();
        } else if (3 == playState) {
          // 播放
          startTime();
        }
      }
    });

    etCommentContent.setOnEditorActionListener(new OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
          LogUtil.showDLog(TAG, "执行发送操作");
          sendComment(etCommentContent.getText().toString());
        }
        return false;
      }
    });
    if (pullToRefreshLayout != null) {
      pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
        @Override
        public void refresh() {
          refreshCus();
        }

        @Override
        public void loadMore() {
          if (mTotal < mPageNumer * pageSize) {
            Utils.show("没有更多数据啦");
            pullToRefreshLayout.finishLoadMore();
          } else {
            mPageNumer += 1;
            loadMoreCus(mPageNumer);
          }
        }
      });
    }

    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {
        LogUtil.showELog(TAG, "setNetListener e " + e);
      }

      @Override
      public void success(String data) {
        LogUtil.showELog(TAG, "flag = " + flag);
        if (1 == flag) {
          try {
            JSONObject jsonObject = new JSONObject(data);
            int code = Utils.jsonObjectIntGetValue(jsonObject, "code");
            if (code == 0) {
              JSONObject jo = jsonObject.getJSONObject("data");
              if (jo != null) {
                mVideoPlayerItemInfo = new Gson()
                    .fromJson(jo.toString(), VideoPlayerItemInfo.class);
                mVideoPlayerItemInfo.postId = mPostId;
                Glide.with(VideoDetailActivity.this)
                    .load(mVideoPlayerItemInfo.coverImage)
                    .into(ivBg);

                Glide.with(VideoDetailActivity.this)
                    .load(mVideoPlayerItemInfo.avatarUrl)
                    .into(civHeadPic);

                tvUserName.setText(mVideoPlayerItemInfo.userName + "");
                String timeNum =
                    mVideoPlayerItemInfo.createTime + " " + mVideoPlayerItemInfo.readTotal + "次观看";
                tvTimeNum.setText(timeNum);

                if (TextUtils.isEmpty(mVideoPlayerItemInfo.description)) {
                  tvContentDesc.setText(mVideoPlayerItemInfo.title + "");
                } else {
                  tvContentDesc.setText(mVideoPlayerItemInfo.description + "");
                }

                tvVideoZan.setText(mVideoPlayerItemInfo.agreeTotal + "");
                tvVideoPinglun.setText(mVideoPlayerItemInfo.commentTotal + "");
                videoPlayer.setUrl(mVideoPlayerItemInfo.playUrl); //设置视频地址
              }
              if (mNoLoad) {
                getAddType();
                mNoLoad = false;
              }
            }
          } catch (Exception e) {
            LogUtil.showELog(TAG, "parseData(String data) catch (JSONException e)"
                + " 异常：" + e.toString());
            Utils.show("解析数据失败");
          }
        } else if (2 == flag) {
          // 评论列表
          parseData(data);
        } else if (3 == flag) {
          // 发表评论
          try {
            JSONObject jsonObject = new JSONObject(data);
            int code = Utils.jsonObjectIntGetValue(jsonObject, "code");
            if (code == 0) {
              etCommentContent.setText("");
              InputMethodManager imm = (InputMethodManager) getSystemService(
                  Context.INPUT_METHOD_SERVICE);
              imm.hideSoftInputFromWindow(etCommentContent.getWindowToken(), 0);

              flag = 2;
              refreshCus();
            }
          } catch (Exception e) {
            LogUtil.showELog(TAG, "parseData(String data) catch (JSONException e)"
                + " 异常：" + e.toString());
            Utils.show("解析数据失败");
          }
        } else if (4 == flag) {
          try {
            JSONObject jsonObject = new JSONObject(data);
            int code = Utils.jsonObjectIntGetValue(jsonObject, "code");
            if (code == 0) {
              Utils.show("收藏成功~");
            }
          } catch (Exception e) {
            LogUtil.showELog(TAG, "4 parseData(String data) catch (JSONException e)"
                + " 异常：" + e.toString());
          }
        } else if (5 == flag) {
          // 点赞
          try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject da = jsonObject.getJSONObject("data");
            int agreeTotal = da.getInt("agreeTotal");

            tvVideoZan.setText("" + agreeTotal);
          } catch (Exception e) {
            LogUtil.showELog(TAG, "parseData(String data) 点赞和踩e：" + e);
          }
        } else if (6 == flag) {
          LogUtil.showELog(TAG, "parseData(String data) 广告 6 == flag");
          try {
            AdsBean adsBean = new Gson().fromJson(data, AdsBean.class);
            if (adsBean.code == 0) {
//              if (adsBean.data.adsConfig.csj) {
//                //加载穿山甲激励广告
//                loadAd(AD_CHUAN_SHA_JIA_REWARD_WAIT, TTAdConstant.VERTICAL);
//              } else if (adsBean.data.adsConfig.tx) {
//                // 2. 加载激励视频广告
////                rewardVideoAD.loadAD(); // 这个素材太少所以没有使用
//              }

//              0:广点通 1:穿山甲 2:百度 3:adView
              if (adsBean.data.videoDetail && adsBean.data.adType == 1) {
                //加载穿山甲激励广告
                loadAd(AD_CHUAN_SHA_JIA_REWARD_WAIT, TTAdConstant.VERTICAL);
              } else if (adsBean.data.videoDetail && adsBean.data.adType == 0) {
//                 2. 加载激励视频广告
//                rewardVideoAD.loadAD();
                // 加载腾讯插屏广告
                loadTxChaPingAD();
              }
            }
            refreshCus();
          } catch (Exception e) {
            LogUtil.showELog(TAG, "initListener: e = " + e.getLocalizedMessage());
          }
        }
      }
    });
  }

  private void loadTxChaPingAD() {
    iad = getIAD();
    setVideoOption();
    iad.loadAD();
  }

  private void parseData(String data) {
    LogUtil.showELog(TAG, "parseData(String data) 解析数据data：" + data);
    mData.clear();
    try {
      JSONObject jsonObject = new JSONObject(data);
      int code = Utils.jsonObjectIntGetValue(jsonObject, "code");
      int total = Utils.jsonObjectIntGetValue(jsonObject, "total");
      mTotal = total;
      if (code == 0) {
        JSONArray ja = jsonObject.getJSONArray("rows");
        if (ja != null) {
          int size = ja.length();
          for (int i = 0; i < size; i++) {
            String vpii = ja.getJSONObject(i).toString();
            CommentBean commentBean = new Gson()
                .fromJson(vpii, CommentBean.class);
            mData.add(commentBean);
          }
        }

        if (mRefresh == 1) {
          mCommentListAdapter.updateData(mData);
          if (pullToRefreshLayout != null) {
            pullToRefreshLayout.finishRefresh();
          }
        } else if (mRefresh == 2) {
          mCommentListAdapter.appendData(mData);
          if (pullToRefreshLayout != null) {
            pullToRefreshLayout.finishLoadMore();
          }
        }

        tvVideoPinglun.setText("" + mTotal);
      }
    } catch (Exception e) {
      LogUtil.showELog(TAG, "parseData(String data) catch (JSONException e)"
          + " 异常：" + e.toString());
      Utils.show("解析数据失败");
    }
  }

  private void refreshCus() {
    mPageNumer = 1;
    mRefresh = 1;
    flag = 2;
    LogUtil.showDLog(TAG, "refreshCus()");
    String url = BASE_URL + COMMENT_LIST;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("postId", mPostId);
      jsonObject.put("pageNum", mPageNumer);
      jsonObject.put("pageSize", pageSize);
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "indexFragment refreshCus e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }

  private void loadMoreCus(int pageNum) {
    LogUtil.showDLog(TAG, "loadMoreCus(int pageNum) pageNum = " + pageNum);
    mRefresh = 2;
    flag = 2;
    String url = BASE_URL + COMMENT_LIST;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("postId", mPostId);
      jsonObject.put("pageNum", pageNum);
      jsonObject.put("pageSize", pageSize);
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "loadMoreCus refresh e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }

  private void sendComment(String comment) {
    flag = 3;
    String url = BASE_URL + SEND_COMMENT;
    String params = "";
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      jsonObject.put("postId", mPostId);
      jsonObject.put("comment", comment);
      params = jsonObject.toString();
    } catch (Exception e) {
      LogUtil.showELog(TAG, "sendComment e is " + e.getLocalizedMessage());
    }

    post(url, params);
  }

  @Override
  protected void onPause() {
    super.onPause();
    LogUtil.showELog(TAG, "onPause()");
    videoPlayer.pause();
    stopTime();
  }

  private void stopTime() {
    // RECORD_STATUS :1开始计时；2结束计时
    Intent recordIntent = new Intent(RECORD_ACTION);
    recordIntent.putExtra(RECORD_STATUS, 2);
    sendBroadcast(recordIntent);
  }

  private void startTime() {
    // RECORD_STATUS :1开始计时；2结束计时
    Intent recordIntent = new Intent(RECORD_ACTION);
    recordIntent.putExtra(RECORD_STATUS, 1);
    sendBroadcast(recordIntent);
  }

  @OnClick({R.id.fl_back, R.id.ll_attention, R.id.et_comment_content,
      R.id.ll_zan, R.id.ll_pinglun, R.id.ll_shoucang, R.id.ll_share})
  public void onViewClicked(View view) {
    String phone = SharedPrefsUtil.get(USER_PHONE, "");
    switch (view.getId()) {
      case R.id.fl_back:
        finish();
        break;
      case R.id.ll_attention:
        break;
      case R.id.et_comment_content:
        break;
      case R.id.ll_zan:
        if (TextUtils.isEmpty(phone)) {
          Utils.show("您还没有登录~~");
        } else {
          zan();
        }
        break;
      case R.id.ll_pinglun:
        if (TextUtils.isEmpty(phone)) {
          Utils.show("您还没有登录~~");
        }
        break;
      case R.id.ll_shoucang:
        if (TextUtils.isEmpty(phone)) {
          Utils.show("您还没有登录~~");
        } else {
          shouCang();
        }
        break;
      case R.id.ll_share:
        if (TextUtils.isEmpty(phone)) {
          Utils.show("您还没有登录~~");
        }
        break;
      default:
        break;
    }
  }

  private void shouCang() {
    LogUtil.showDLog(TAG, "shouCang()");
    flag = 4;
    String url = BASE_URL + COLLECTION_ADD;
    String params = "";
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      jsonObject.put("postId", mPostId);
      params = jsonObject.toString();
    } catch (Exception e) {
      LogUtil.showELog(TAG, "shouCang e is " + e.getLocalizedMessage());
    }
    post(url, params);
  }


  private BroadcastReceiver uploadVideoStatueReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      LogUtil.showDLog(TAG, "接收系统动态注册广播消息");

    }
  };

  public void zan() {
    LogUtil.showELog(TAG, "uploadStatue");
    flag = 5;
    String url = BASE_URL + POST_UPDATE;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
      if (mVideoPlayerItemInfo != null) {
        jsonObject.put("id", mVideoPlayerItemInfo.id);
      }
      jsonObject.put("agreeTotal", 1);
      jsonObject.put("footTotal", 0);
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "uploadStatue refresh e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }

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
        videoPlayer.start(); //开始播放，不调用则不自动播放

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
          mttRewardVideoAd.showRewardVideoAd(VideoDetailActivity.this,
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
                Message mGetSuccess = new Message();
                mGetSuccess.obj = "点击这个广告有奖励";
                mGetSuccess.what = 5;
                mCusHandler.sendMessage(mGetSuccess);
              }

              @Override
              public void onAdVideoBarClick() {
                LogUtil.showELog(TAG, "Callback --> rewardVideoAd bar click");
                //TToast.show(RewardVideoActivity.this, "rewardVideoAd bar click");
                if (Contants.AD_CLICK_CSJ == mClickAds) {
                  Map<String, String> map = new HashMap<>();
                  map.put("adsName", "1"); // 0:广点通 1:穿山甲 2:百度 3:adView
                  map.put("adsType", "1"); // 0:开屏广告 1:视频激励广告 2：图文广告
                  postAdsRecordAdd(map);
                }
              }

              @Override
              public void onAdClose() {
                LogUtil.showELog(TAG, "Callback --> rewardVideoAd close");

                videoPlayer.start(); //开始播放，不调用则不自动播放
              }

              //视频播放完成回调
              @Override
              public void onVideoComplete() {
                LogUtil.showELog(TAG, "Callback --> rewardVideoAd complete");
                //TToast.show(RewardVideoActivity.this, "rewardVideoAd complete");
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
    flag = 6;
    String url = BASE_URL + NOTICE_ADS;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "getAddType e = " + e.getLocalizedMessage());
    }
    post(url, jsonObject.toString());
  }

  private void postAdsRecordAdd(Map params) {
    flag = 6;
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
            if (Contants.AD_CLICK_TX == mClickAds) {
              Map<String, String> map = new HashMap<>();
              map.put("adsName", "0"); // 0:广点通 1:穿山甲 2:百度 3:adView
              map.put("adsType", "2"); // 0:开屏广告 1:视频激励广告 2：图文广告
              postAdsRecordAdd(map);
            }
          }

          @Override
          public void onADClosed() {
            LogUtil.showDLog(TAG, "onADClosed");

            videoPlayer.start();
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
            LogUtil.showDLog(TAG, "eCPMLevel = " + iad.getECPMLevel());
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
      Message mGetSuccess = new Message();
      mGetSuccess.obj = "点击这个广告有奖励";
      mGetSuccess.what = 5;
      mCusHandler.sendMessage(mGetSuccess);
      iad.show();
    } else {
      Toast.makeText(this, "请加载广告后再进行展示 ！ ", Toast.LENGTH_LONG).show();
    }
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
  // 加载腾讯插屏广告结束


}
