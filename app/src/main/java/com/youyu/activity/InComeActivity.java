package com.youyu.activity;

import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.youyu.R;
import com.youyu.adapter.InComeAdapter;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.net.NetInterface.RequestResponse;

/**
 * @Author zhisiyi
 * @Date 2020.04.21 12:30
 * @Comment
 */
public class InComeActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.content_view)
    CusRecycleView incomeListView;
    @BindView(R.id.pull_to_refresh)
    PullToRefreshLayout pullToRefreshLayout;



    private InComeAdapter mInComeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_detail);
        ButterKnife.bind(this);
        initValue();
        initListener();
    }

    private void initListener() {
        setNetListener(new RequestResponse() {
            @Override
            public void failure(Exception e) {

            }

            @Override
            public void success(String data) {

//                mInComeAdapter.setData(data);
            }
        });
    }

    private void initValue() {
        mInComeAdapter = new InComeAdapter(this);
        incomeListView.setAdapter(mInComeAdapter);
    }
}
