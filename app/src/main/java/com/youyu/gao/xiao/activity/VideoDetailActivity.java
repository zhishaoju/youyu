package com.youyu.gao.xiao.activity;

import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;
import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.COLLECTION_ADD;
import static com.youyu.gao.xiao.utils.Contants.Net.COMMENT_DETAIL;
import static com.youyu.gao.xiao.utils.Contants.Net.COMMENT_LIST;
import static com.youyu.gao.xiao.utils.Contants.Net.POST_UPDATE;
import static com.youyu.gao.xiao.utils.Contants.Net.SEND_COMMENT;
import static com.youyu.gao.xiao.utils.Contants.PAGE_SIZE;
import static com.youyu.gao.xiao.utils.Contants.POST_ID;
import static com.youyu.gao.xiao.utils.Contants.USER_ID;
import static com.youyu.gao.xiao.utils.Contants.USER_PHONE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.google.gson.Gson;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.adapter.VideoDetailCommentListAdapter;
import com.youyu.gao.xiao.applicatioin.TTAdManagerHolder;
import com.youyu.gao.xiao.bean.CommentBean;
import com.youyu.gao.xiao.bean.VideoPlayerItemInfo;
import com.youyu.gao.xiao.cusListview.CusRecycleView;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import com.youyu.gao.xiao.utils.Utils;
import com.youyu.gao.xiao.view.CircleImageView;
import com.youyu.gao.xiao.view.VideoPlayer;
import java.util.ArrayList;
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
  VideoPlayer videoPlayer;
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

  //1：加载详情; 2:评论列表；3: 发表评论；4:收藏；5:点赞
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_video_detail);
    ButterKnife.bind(this);
    etCommentContent.setInputType(TYPE_TEXT_FLAG_MULTI_LINE);
    etCommentContent.setSingleLine(false);
    initValue();
    initListener();
  }

  private void initValue() {
    mCommentListAdapter = new VideoDetailCommentListAdapter(this);
    //初始化RecyclerView
    lm = new LinearLayoutManager(this);
    pinglunListView.setLayoutManager(lm);
    pinglunListView.setAdapter(mCommentListAdapter);

    //step3:创建TTAdNative对象,用于调用广告请求接口
    mTTAdNative = TTAdManagerHolder.get().createAdNative(this);


    loadAd("945477236", TTAdConstant.VERTICAL);
  }

  @Override
  protected void onResume() {
    super.onResume();
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


  @Override
  protected void onDestroy() {
    super.onDestroy();
    videoPlayer.mediaController.destroy();
  }

  private void initListener() {
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

                videoPlayer.setPlayData(mVideoPlayerItemInfo);
                videoPlayer.initViewDisplay(mVideoPlayerItemInfo.duration);

//                videoPlayer.mediaController.clickPlay();
              }
              flag = 2;
              refreshCus();
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
        }
      }
    });
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
//    if (MediaHelper.getInstance().isPlaying()) {
//      MediaHelper.release();
//    }
    videoPlayer.mediaController.destroy();
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
      }

      //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
      @Override
      public void onRewardVideoCached() {
        LogUtil.showELog(TAG, "Callback --> onRewardVideoCached");
        mIsLoaded = true;
        //TToast.show(RewardVideoActivity.this, "Callback --> rewardVideoAd video cached");

        if (mttRewardVideoAd != null&&mIsLoaded) {
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
              }

              @Override
              public void onAdVideoBarClick() {
                LogUtil.showELog(TAG, "Callback --> rewardVideoAd bar click");
                //TToast.show(RewardVideoActivity.this, "rewardVideoAd bar click");
              }

              @Override
              public void onAdClose() {
                LogUtil.showELog(TAG, "Callback --> rewardVideoAd close");
                //TToast.show(RewardVideoActivity.this, "rewardVideoAd close");
                videoPlayer.mediaController.clickPlay();
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

}
