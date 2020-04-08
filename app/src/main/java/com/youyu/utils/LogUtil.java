package com.youyu.utils;

import android.util.Log;

public class LogUtil {

  public static void showELog(String tag, String msg) {
    if (Contants.DEBUG) {
      Log.e(tag, msg);
    }
  }

  public static void showDLog(String tag, String msg) {
    if (Contants.DEBUG) {
      Log.d(tag, msg);
    }
  }
}
