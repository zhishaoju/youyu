package com.youyu.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.youyu.R;

public class SecondFragment extends BaseFragment {

  private final String TAG = SecondFragment.class.getSimpleName();
  private Unbinder mUnBinder;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_second, container, false);
    mUnBinder = ButterKnife.bind(this, view);
    initValue();
    initListener();
    return view;
  }

  private void initValue() {

  }

  private void initListener() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnBinder.unbind();
  }

  @OnClick({})
  public void onViewClicked(View view) {
    switch (view.getId()) {
    }
  }

}
