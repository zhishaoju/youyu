package com.youyu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.youyu.R;
import com.youyu.bean.ActiveModel;
import com.youyu.cusListview.CusRecycleView;

/**
 * @Author zhisiyi
 * @Date 2020.04.20 22:22
 * @Comment
 */
public class ActiveActivity extends BaseActivity {

  @BindView(R.id.fl_back)
  FrameLayout flBack;
  @BindView(R.id.tv_title)
  TextView tvTitle;
  @BindView(R.id.content_view)
  CusRecycleView contentView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_active);
    ButterKnife.bind(this);
    initValue();
  }

  private void initValue() {
  }


}
