package com.youyu.gao.xiao.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @Author zsj
 * @Date 2020.04.08 23:20
 * @Commit
 */
public class JsonUtils {

  private static final String TAG = JsonUtils.class.getSimpleName();

  public static boolean isJsonObject(String jsonStr) {
    LogUtil.showELog(TAG, "isJsonObject(String jsonStr) jsonStr = " + jsonStr);
    if (jsonStr == null) {
      return false;
    }
    Object json = null;
    try {
      json = new JSONTokener(jsonStr).nextValue();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    if (json instanceof JSONObject) {
      return true;
    }
    return false;
  }

  public static boolean isJsonArray(String jsonStr) {

    Object json = null;
    try {
      json = new JSONTokener(jsonStr).nextValue();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    if (json instanceof JSONArray) {
      return true;
    }
    return false;
  }
}