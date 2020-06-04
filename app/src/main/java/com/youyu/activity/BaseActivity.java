package com.youyu.activity;

import static com.youyu.utils.Utils.jsonObjectIntGetValue;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import com.youyu.BuildConfig;
import com.youyu.net.NetInterface;
import com.youyu.utils.Contants;
import com.youyu.utils.JsonUtils;
import com.youyu.utils.LogUtil;
import com.youyu.utils.Utils;
import java.io.IOException;
import java.lang.ref.WeakReference;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhisiyi
 * @date 20.04.08 16:09
 * @comment
 */
public class BaseActivity extends Activity {

  private final String TAG = BaseActivity.class.getSimpleName();
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private final Object mTag = new Object();

  private OkHttpClient mHttpClient;
  private NetInterface.RequestResponse mNetInterface;
  protected CusHandler mCusHandler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //无title
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    //全屏
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    initNet();
    mCusHandler = new CusHandler(this);
  }

  protected void setNetListener(NetInterface.RequestResponse requestResponse) {
    mNetInterface = requestResponse;
  }

  private void initNet() {
    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    Builder builder = new OkHttpClient.Builder();
    mHttpClient = null;
    if (BuildConfig.DEBUG) {
      mHttpClient = builder.addNetworkInterceptor(httpLoggingInterceptor).build();
    }
    mHttpClient = builder.build();
  }

  protected void post(String url, String params) {
    LogUtil.showELog(TAG, "post 请求url = " + url);
    LogUtil.showELog(TAG, "post 请求params = " + params);
    if (JsonUtils.isJsonObject(params)) {
//            FormBody.Builder body = new FormBody.Builder();
      try {
//                JSONObject jsonObject = new JSONObject(params);
//                Iterator<String> keys = jsonObject.keys();
//                while (keys.hasNext()) {
//                    String key = keys.next();
//                    String value = jsonObject.getString(key);
//                    body.add(key, value);
//                }
////              body.add("params", params);
//                RequestBody requestBody = body.build();
        // MediaType.parse("application/json;charset=utf-8"),
        // 这一块就是okhttp设置Content-Type
        RequestBody requestBody = RequestBody
            .create(MediaType.parse("application/json;charset=utf-8"), params);
        Request request = new Request.Builder()
            .post(requestBody)
            .url(url)
            .tag(mTag)
            .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
          @Override
          public void onFailure(okhttp3.Call call, IOException e) {
            mCusHandler.removeCallbacksAndMessages(null);
            LogUtil.showELog(TAG, "post IOException" + e.toString());
            Message mFail = new Message();
            mFail.obj = e;
            mFail.what = 1;
            mCusHandler.sendMessage(mFail);
          }

          @Override
          public void onResponse(okhttp3.Call call, Response response)
              throws IOException {
            String resposeStr = response.body().string();
            mCusHandler.removeCallbacksAndMessages(null);
            LogUtil
                .showELog(TAG,
                    "onResponse(Call call, Response response) success: " + resposeStr);
            Message mSucces = new Message();
            mSucces.obj = resposeStr;
            mSucces.what = 2;
            mCusHandler.sendMessage(mSucces);
          }
        });

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  protected void get(String url) {
    LogUtil.showELog(TAG, "get 请求 url = " + url);
    final Request request = new Request.Builder()
        .url(url)
        .get()
        .build();
    mHttpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        LogUtil.showELog(TAG, "get请求返回失败的值：" + e.getMessage());
        Message mGetFail = new Message();
        mGetFail.obj = e;
        mGetFail.what = 3;
        mCusHandler.sendMessage(mGetFail);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        String sdata = response.body().string();
        LogUtil.showELog(TAG, "get请求返回成功的值：" + sdata);
        Message mGetSuccess = new Message();
        mGetSuccess.obj = sdata;
        mGetSuccess.what = 4;
        mCusHandler.sendMessage(mGetSuccess);
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    cancleAll(mTag);
  }

  public void cancleAll(Object tag) {
    Dispatcher dispatcher = mHttpClient.dispatcher();
    synchronized (dispatcher) {
      for (Call call : dispatcher.queuedCalls()) {
        if (tag.equals(call.request().tag())) {
          call.cancel();
        }
      }
      for (Call call : dispatcher.runningCalls()) {
        if (tag.equals(call.request().tag())) {
          call.cancel();
        }
      }
    }
  }

  protected static class CusHandler extends Handler {

    private final WeakReference<BaseActivity> mActivity;

    public CusHandler(BaseActivity activity) {
      mActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
      final BaseActivity activity = mActivity.get();
      if (activity == null) {
        super.handleMessage(msg);
        return;
      }
      activity.cusHandleMessage(msg);
    }
  }

  protected void cusHandleMessage(Message msg) {
    LogUtil.showDLog(TAG, "cusHandleMessage(Message msg) msg.what = " + msg.what + ";"
        + "\n msg.obj = " + msg.obj);
    switch (msg.what) {
      case 1: // post 请求失败的情况
        mNetInterface.failure((Exception) msg.obj);
        break;
      case 2: // post 请求成功的情况
        String resposeStr = (String) msg.obj;
        try {
          if (JsonUtils.isJsonObject(resposeStr)) {
            JSONObject jsonObject = null;
            try {
              jsonObject = new JSONObject(resposeStr);
              int state = jsonObjectIntGetValue(jsonObject, "state");
              String msgPost = Utils.jsonObjectStringGetValue(jsonObject, "msg");
              Utils.show(msgPost);
//              if (Contants.NetStatus.OK == state) {
                mNetInterface.success(resposeStr);
//              }
            } catch (JSONException e) {
              LogUtil.showELog(TAG,
                  "cusHandleMessage post JSONException:" + e.toString());
            }
          }
        } catch (Exception e) {
          LogUtil.showELog(TAG, "e:" + e.toString());
        }
        break;
      case 3:// get请求失败的情况
        mNetInterface.failure((Exception) msg.obj);
        break;
      case 4: // get请求成功的情况
        String sdata = (String) msg.obj;
        try {
          if (JsonUtils.isJsonObject(sdata)) {
            JSONObject jsonObject = null;
            try {
              jsonObject = new JSONObject(sdata);
              int state = jsonObjectIntGetValue(jsonObject, "state");
              String msgGet = jsonObject.getString("msg");
              Utils.show(msgGet);
//              if (Contants.NetStatus.OK == state) {
                mNetInterface.success(sdata);
//              }
            } catch (JSONException e) {
              LogUtil.showELog(TAG, "get JSONException:" + e.toString());
            }
          }
        } catch (Exception e) {
          LogUtil.showELog(TAG, "get e:" + e.toString());
        }
        break;
      default:
        break;
    }
  }
}
