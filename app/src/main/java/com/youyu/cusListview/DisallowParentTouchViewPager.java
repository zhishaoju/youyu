package com.youyu.cusListview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;

/**
 * @Author zhisiyi
 * @Date 2020.04.25 12:11
 * @Comment
 */
public class DisallowParentTouchViewPager extends ViewPager {

  private ViewGroup parent;

  public DisallowParentTouchViewPager(Context context) {
    super(context);
  }

  public DisallowParentTouchViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setNestParent(ViewGroup parent) {
    this.parent = parent;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (parent != null) {
      parent.requestDisallowInterceptTouchEvent(true);
    }
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (parent != null) {
      parent.requestDisallowInterceptTouchEvent(true);
    }
    return super.onTouchEvent(ev);
  }

  private float mDownX, mDownY;

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        //记录下按下时当前的xy左标
        mDownX = ev.getX();
        mDownY = ev.getY();
        getParent().requestDisallowInterceptTouchEvent(true);
        break;
      case MotionEvent.ACTION_MOVE:
        //滑动时计算x轴y轴的移动距离 ，如果x轴移动距离大于y轴，说明是水平滑动，禁止下拉刷新
        if (Math.abs(ev.getX() - mDownX) > Math.abs(ev.getY() - mDownY)) {
          getParent().requestDisallowInterceptTouchEvent(true);
          ((CusRecycleView) ((ViewPagerPullToRefreshLayout) getParent()).getChildAt(2))
              .setCanPullDown(false);
          ((CusRecycleView) ((ViewPagerPullToRefreshLayout) getParent()).getChildAt(2))
              .setCanPullUp(true);
        } else {
          getParent().requestDisallowInterceptTouchEvent(false);
          ((CusRecycleView) ((ViewPagerPullToRefreshLayout) getParent()).getChildAt(2))
              .setCanPullDown(true);
          ((CusRecycleView) ((ViewPagerPullToRefreshLayout) getParent()).getChildAt(2))
              .setCanPullUp(false);
        }
        break;
      case MotionEvent.ACTION_CANCEL:
        getParent().requestDisallowInterceptTouchEvent(false);
        ((CusRecycleView) ((ViewPagerPullToRefreshLayout) getParent()).getChildAt(2))
            .setCanPullDown(true);
        ((CusRecycleView) ((ViewPagerPullToRefreshLayout) getParent()).getChildAt(2))
            .setCanPullUp(true);
        break;
      case MotionEvent.ACTION_UP:
        getParent().requestDisallowInterceptTouchEvent(false);
        ((CusRecycleView) ((ViewPagerPullToRefreshLayout) getParent()).getChildAt(2))
            .setCanPullDown(true);
        ((CusRecycleView) ((ViewPagerPullToRefreshLayout) getParent()).getChildAt(2))
            .setCanPullUp(true);
        break;
      default:
        break;
    }
    return super.dispatchTouchEvent(ev);

  }
}
