package com.youyu.activity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.youyu.R;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.cusListview.PullToRefreshLayout;

/**
 * @Author zhisiyi
 * @Date 2020.04.22 18:34
 * @Comment
 */
public class ActiveDetailActivity extends BaseActivity {

    @BindView(R.id.fl_back)
    FrameLayout flBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_active_name)
    TextView tvActiveName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_require)
    TextView tvRequire;
    @BindView(R.id.tv_join_num)
    TextView tvJoinNum;
    @BindView(R.id.tv_active_count_down)
    TextView tvActiveCountDown;
    @BindView(R.id.tv_active_money)
    TextView tvActiveMoney;
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
    @BindView(R.id.finish_task_view)
    CusRecycleView finishTaskView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_detail);
        ButterKnife.bind(this);
        initValue();
    }

    private void initValue() {
        
    }
}
