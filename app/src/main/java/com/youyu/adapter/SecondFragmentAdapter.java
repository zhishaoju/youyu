package com.youyu.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.youyu.bean.ActiveModel;
import com.youyu.bean.IncomeModel;
import java.util.ArrayList;

/**
 * @Author zhisiyi
 * @Date 2020.04.25 12:53
 * @Comment
 */
public class SecondFragmentAdapter extends BaseAdapter {
  private Context mCtx;

  private ArrayList mData = new ArrayList<ActiveModel>();

  public SecondFragmentAdapter(Context context) {
    mCtx = context;
  }
  @Override
  public int getCount() {
    return 0;
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    return null;
  }
}
