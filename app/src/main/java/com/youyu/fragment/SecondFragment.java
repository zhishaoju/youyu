package com.youyu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.youyu.R;
import com.youyu.activity.ActiveDetailActivity;
import com.youyu.adapter.ActiveAdapter;
import com.youyu.adapter.ActiveAdapter.OnItemClickListener;
import com.youyu.adapter.SecondFragmentAdapter;
import com.youyu.adapter.SecondFragmentAdapter.ItemClickListener;
import com.youyu.adapter.SecondPagerAdapter;
import com.youyu.bean.ActiveModel;
import com.youyu.bean.SecondViewPagerModel;
import com.youyu.cusListview.CusRecycleView;
import com.youyu.cusListview.PullToRefreshLayout;
import java.util.ArrayList;

public class SecondFragment extends BaseFragment {

  private final String TAG = SecondFragment.class.getSimpleName();
  @BindView(R.id.viewPager)
  ViewPager viewPager;
  @BindView(R.id.refresh_view)
  PullToRefreshLayout refreshView;
  @BindView(R.id.content_view)
  CusRecycleView contentView;


  private SecondPagerAdapter mSecondPagerAdapter;

  private SecondFragmentAdapter mActiveAdapter;

  private ArrayList<SecondViewPagerModel> mData;
  private ArrayList<ActiveModel> mActiveModelList;
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
    mData = new ArrayList<>();
    mActiveModelList = new ArrayList<>();
    mSecondPagerAdapter = new SecondPagerAdapter(getActivity());
    mActiveAdapter = new SecondFragmentAdapter(getActivity());
    contentView.setLayoutManager(new LinearLayoutManager(getActivity()));
    contentView.setAdapter(mActiveAdapter);
    viewPager.setAdapter(mSecondPagerAdapter);

    SecondViewPagerModel secondViewPagerModel1 = new SecondViewPagerModel();
    secondViewPagerModel1.picUrl =
        "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1587785579&di=bbf01fe8138ca1a06072dfef7c065ab8&src=http://a3.att.hudong.com/14/75/01300000164186121366756803686.jpg";

    SecondViewPagerModel secondViewPagerModel2 = new SecondViewPagerModel();
    secondViewPagerModel2.picUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1587795671000&di=3fdc39d8af33f1065ea3531e12dd9207&imgtype=0&src=http%3A%2F%2Fc.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Fd009b3de9c82d1587e249850820a19d8bd3e42a9.jpg";
    mData.add(secondViewPagerModel1);
    mData.add(secondViewPagerModel2);

    mSecondPagerAdapter.setData(mData);

    ActiveModel activeModel = new ActiveModel();
    activeModel.title = "test1";
    activeModel.beginTime = "20200425";
    activeModel.endTime = "20200609";
    activeModel.count = "2131";
    activeModel.status = 1;

    ActiveModel activeModel1 = new ActiveModel();
    activeModel1.title = "test2";
    activeModel1.beginTime = "20200426";
    activeModel1.endTime = "20200612";
    activeModel1.count = "213166";
    activeModel1.status = 2;

    mActiveModelList.add(activeModel);
    mActiveModelList.add(activeModel1);

    mActiveAdapter.setData(mActiveModelList);

  }

  private void initListener() {
    mActiveAdapter.setOnClickListener(new ItemClickListener() {
      @Override
      public void OnItemClickListener(ActiveModel activeModel) {
        startActivity(new Intent(getActivity(), ActiveDetailActivity.class));
      }
    });
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
}
