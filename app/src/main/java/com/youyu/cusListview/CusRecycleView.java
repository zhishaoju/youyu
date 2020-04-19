package com.youyu.cusListview;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author zhisiyi
 * @Date 2020.04.19 18:22
 * @Comment
 */
public class CusRecycleView extends RecyclerView implements Pullable {

  public CusRecycleView(@NonNull Context context) {
    super(context);
  }

  public CusRecycleView(@NonNull Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public CusRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean canPullDown() {
    RecyclerView.LayoutManager layoutManager = getLayoutManager();
    LinearLayoutManager linearManager;
    int lastItemPosition = 0;
    int firstItemPosition = 0;
    //判断是当前layoutManager是否为LinearLayoutManager
    // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
    if (layoutManager instanceof LinearLayoutManager) {
      linearManager = (LinearLayoutManager) layoutManager;
      //获取最后一个可见view的位置
      lastItemPosition = linearManager.findLastVisibleItemPosition();
      //获取第一个可见view的位置
      firstItemPosition = linearManager.findFirstVisibleItemPosition();
    }

    if (getChildCount() == 0) {
      // 没有item的时候也可以下拉刷新
      return true;
    } else if (firstItemPosition == 0
        && getChildAt(0).getTop() >= 0) {
      // 滑到顶部了
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean canPullUp() {
    RecyclerView.LayoutManager layoutManager = getLayoutManager();
    LinearLayoutManager linearManager;
    int lastItemPosition = 0;
    int firstItemPosition = 0;
    //判断是当前layoutManager是否为LinearLayoutManager
    // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
    if (layoutManager instanceof LinearLayoutManager) {
      linearManager = (LinearLayoutManager) layoutManager;
      //获取最后一个可见view的位置
      lastItemPosition = linearManager.findLastVisibleItemPosition();
      //获取第一个可见view的位置
      firstItemPosition = linearManager.findFirstVisibleItemPosition();
    }

    if (getChildCount() == 0) {
      // 没有item的时候也可以上拉加载
      return true;
    } else if (lastItemPosition == (getChildCount() - 1)) {
      // 滑到底部了
      if (getChildAt(lastItemPosition - firstItemPosition) != null
          && getChildAt(
          lastItemPosition
              - firstItemPosition).getBottom() <= getMeasuredHeight()) {
        return true;
      }
    }
    return false;
  }
}
