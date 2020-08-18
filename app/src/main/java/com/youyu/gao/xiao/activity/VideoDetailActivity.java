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
import com.google.gson.Gson;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.adapter.VideoDetailCommentListAdapter;
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

                videoPlayer.mediaController.clickPlay();
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

}
