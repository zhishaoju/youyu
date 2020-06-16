package com.youyu.activity;

import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;
import static com.youyu.utils.Contants.Net.BASE_URL;
import static com.youyu.utils.Contants.Net.COLLECTION_ADD;
import static com.youyu.utils.Contants.Net.COMMENT_DETAIL;
import static com.youyu.utils.Contants.Net.COMMENT_LIST;
import static com.youyu.utils.Contants.Net.SEND_COMMENT;
import static com.youyu.utils.Contants.USER_ID;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.youyu.R;
import com.youyu.adapter.VideoDetailCommentListAdapter;
import com.youyu.bean.CommentBean;
import com.youyu.bean.VideoPlayerItemInfo;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.cusListview.PullToRefreshLayout;
import com.youyu.net.NetInterface.RequestResponse;
import com.youyu.utils.LogUtil;
import com.youyu.utils.MediaHelper;
import com.youyu.utils.SharedPrefsUtil;
import com.youyu.utils.Utils;
import com.youyu.view.CircleImageView;
import com.youyu.view.VideoPlayer;
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
  @BindView(R.id.pull_icon)
  ImageView pullIcon;
  @BindView(R.id.refreshing_icon)
  ImageView refreshingIcon;
  @BindView(R.id.state_tv)
  TextView stateTv;
  @BindView(R.id.state_iv)
  ImageView stateIv;
  @BindView(R.id.head_view)
  RelativeLayout headView;
  @BindView(R.id.pinglun_list_view)
  CusRecycleView pinglunListView;
  @BindView(R.id.pullup_icon)
  ImageView pullupIcon;
  @BindView(R.id.loading_icon)
  ImageView loadingIcon;
  @BindView(R.id.loadstate_tv)
  TextView loadstateTv;
  @BindView(R.id.loadstate_iv)
  ImageView loadstateIv;
  @BindView(R.id.loadmore_view)
  RelativeLayout loadmoreView;
  @BindView(R.id.refresh_view)
  PullToRefreshLayout refreshView;
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

  private int flag = -1;

  private int mPageNumer = 1;
  private int mRefresh; // =1 代表刷新；=2 代表加载更多
  private int pageSize = 10;

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
    refreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {

      @Override
      public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        refresh();
      }

      @Override
      public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPageNumer += 1;
        loadMore(mPageNumer);
      }
    });
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {

      }

      @Override
      public void success(String data) {
        if (1 == flag) {
          try {
            JSONObject jsonObject = new JSONObject(data);
            int code = Utils.jsonObjectIntGetValue(jsonObject, "code");
            if (code == 0) {
              JSONObject jo = jsonObject.getJSONObject("data");
              if (jo != null) {
                VideoPlayerItemInfo videoPlayerItemInfo = new Gson()
                    .fromJson(jo.toString(), VideoPlayerItemInfo.class);
                videoPlayerItemInfo.postId = mPostId;
                Glide.with(VideoDetailActivity.this)
                    .load(videoPlayerItemInfo.coverImage)
                    .into(ivBg);

                if (TextUtils.isEmpty(videoPlayerItemInfo.description)) {
                  tvContentDesc.setText(videoPlayerItemInfo.title + "");
                } else {
                  tvContentDesc.setText(videoPlayerItemInfo.description + "");
                }

                tvVideoZan.setText(videoPlayerItemInfo.agreeTotal + "");
                tvVideoPinglun.setText(videoPlayerItemInfo.commentTotal + "");

                videoPlayer.setPlayData(videoPlayerItemInfo);
                videoPlayer.initViewDisplay(videoPlayerItemInfo.duration);
              }
              flag = 2;
              refresh();
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
            }
          } catch (Exception e) {
            LogUtil.showELog(TAG, "parseData(String data) catch (JSONException e)"
                + " 异常：" + e.toString());
            Utils.show("解析数据失败");
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
          refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
        } else if (mRefresh == 2) {
          mCommentListAdapter.appendData(mData);
          refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        }
      }
    } catch (Exception e) {
      LogUtil.showELog(TAG, "parseData(String data) catch (JSONException e)"
          + " 异常：" + e.toString());
      Utils.show("解析数据失败");
    }
  }

  private void refresh() {
    mPageNumer = 1;
    mRefresh = 1;
    flag = 2;
    LogUtil.showDLog(TAG, "refresh()");
    String url = BASE_URL + COMMENT_LIST;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("postId", mPostId);
      jsonObject.put("pageNum", mPageNumer);
      jsonObject.put("pageSize", pageSize);
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "indexFragment refresh e :" + e.getLocalizedMessage());
    }
    String param = jsonObject.toString();
    post(url, param);
  }

  private void loadMore(int pageNum) {
    LogUtil.showDLog(TAG, "loadMore(int pageNum) pageNum = " + pageNum);
    mRefresh = 2;
    flag = 2;
    String url = BASE_URL + COMMENT_LIST;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("postId", mPostId);
      jsonObject.put("pageNum", pageNum);
      jsonObject.put("pageSize", pageSize);
    } catch (JSONException e) {
      LogUtil.showELog(TAG, "indexFragment refresh e :" + e.getLocalizedMessage());
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
    if (MediaHelper.getInstance().isPlaying()) {
      MediaHelper.release();
    }
  }

  @OnClick({R.id.fl_back, R.id.ll_attention, R.id.pinglun_list_view, R.id.et_comment_content,
      R.id.ll_zan, R.id.ll_pinglun, R.id.ll_shoucang, R.id.ll_share})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.fl_back:
        finish();
        break;
      case R.id.ll_attention:
        break;
      case R.id.pinglun_list_view:
        break;
      case R.id.et_comment_content:
        break;
      case R.id.ll_zan:
        break;
      case R.id.ll_pinglun:
        break;
      case R.id.ll_shoucang:
        shouCang();
        break;
      case R.id.ll_share:
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

  public void uploadStatue() {
    flag = 5;
    String url = BASE_URL + SEND_COMMENT;
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

}
