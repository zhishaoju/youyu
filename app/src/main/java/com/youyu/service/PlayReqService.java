package com.youyu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 * @Author zhisiyi
 * @Date 2020.07.29 21:09
 * @Comment
 */
public class PlayReqService extends Service {

  @Override
  public void onCreate() {
    super.onCreate();
    
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
