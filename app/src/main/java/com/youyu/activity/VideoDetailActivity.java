package com.youyu.activity;

import static com.youyu.utils.Contants.Net.BASE_URL;
import static com.youyu.utils.Contants.Net.POST_COMMENT;
import static com.youyu.utils.Contants.USER_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.youyu.R;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.cusListview.PullToRefreshLayout;
import com.youyu.net.NetInterface.RequestResponse;
import com.youyu.utils.LogUtil;
import com.youyu.utils.SharedPrefsUtil;
import com.youyu.view.CircleImageView;
import com.youyu.view.VideoPlayer;
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_video_detail);
    ButterKnife.bind(this);
    initListener();
  }

  @Override
  protected void onResume() {
    super.onResume();
    String url = BASE_URL + POST_COMMENT;
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
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {

      }

      @Override
      public void success(String data) {

      }
    });
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
        break;
      case R.id.ll_share:
        break;
      default:
        break;
    }
  }

}
