package com.youyu.gao.xiao.service;

import static com.youyu.gao.xiao.utils.Contants.BroadcastConst.RECORD_ACTION;
import static com.youyu.gao.xiao.utils.Contants.BroadcastConst.RECORD_STATUS;
import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.RECORD_ADD;
import static com.youyu.gao.xiao.utils.Contants.POST_ID;
import static com.youyu.gao.xiao.utils.Contants.USER_ID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.SharedPrefsUtil;
import org.json.JSONObject;

/**
 * @Author zhisiyi
 * @Date 2020.07.29 21:09
 * @Comment
 */
public class PlayReqService extends BaseService {

  private String TAG = PlayReqService.class.getSimpleName();

  private RecordReceiver mRecordReceiver;

  private static Handler mHandler = new Handler();

  @Override
  public void onCreate() {
    super.onCreate();
    // 1. 实例化BroadcastReceiver子类 &  IntentFilter
    mRecordReceiver = new RecordReceiver();
    // 2. 设置接收广播的类型
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(RECORD_ACTION);
    // 3. 动态注册：调用Context的registerReceiver（）方法
    registerReceiver(mRecordReceiver, intentFilter);

    initListener();
  }

  private void initListener() {
    setNetListener(new RequestResponse() {
      @Override
      public void failure(Exception e) {

      }

      @Override
      public void success(String data) {
        LogUtil.showDLog(TAG, "成功：" + data);
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mHandler.removeCallbacks(handlerTimer);
    unregisterReceiver(mRecordReceiver);
  }

  public class RecordReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      int flag = intent.getIntExtra(RECORD_STATUS, 0);
      LogUtil.showDLog(TAG, "RecordReceiver flag = " + flag);

      // flag:1开始计时；2结束计时
      if (flag == 1) {
        mHandler.post(handlerTimer);
      } else if (flag == 2) {
        mHandler.removeCallbacks(handlerTimer);
      }
    }
  }


  private Runnable handlerTimer = new Runnable() {
    @Override
    public void run() {
      // 发送网络请求
      recordNet();
      mHandler.postDelayed(this, 5000);
    }

    private void recordNet() {
      String url = BASE_URL + RECORD_ADD;
      String params = "";
      try {
        JSONObject jsonObject = new JSONObject();
        String postId = SharedPrefsUtil.get(POST_ID, "");
        LogUtil.showELog(TAG, "postId is " + postId);
        jsonObject.put("userId", SharedPrefsUtil.get(USER_ID, ""));
        jsonObject.put("postId", postId);
        jsonObject.put("duration", 5);
        params = jsonObject.toString();
      } catch (Exception e) {
        LogUtil.showELog(TAG, "recordNet e is " + e.getLocalizedMessage());
      }
      post(url, params);
    }
  };


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
