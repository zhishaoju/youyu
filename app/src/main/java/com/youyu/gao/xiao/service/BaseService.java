package com.youyu.gao.xiao.service;

import static com.youyu.gao.xiao.utils.Utils.jsonObjectIntGetValue;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;
import com.youyu.gao.xiao.BuildConfig;
import com.youyu.gao.xiao.net.NetInterface;
import com.youyu.gao.xiao.utils.JsonUtils;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.Utils;
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
 * @Author zhisiyi
 * @Date 2020.08.01 10:26
 * @Comment
 */
public class BaseService extends Service {

  private final String TAG = BaseService.class.getSimpleName();
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private final Object mTag = new Object();

  private OkHttpClient mHttpClient;
  private NetInterface.RequestResponse mNetInterface;
  protected CusHandler mCusHandler;

  @Override
  public void onCreate() {
    super.onCreate();
    initNet();
    mCusHandler = new CusHandler(this);
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
      try {
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

  @Override
  public void onDestroy() {
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

  protected void setNetListener(NetInterface.RequestResponse requestResponse) {
    mNetInterface = requestResponse;
  }

  protected static class CusHandler extends Handler {

    private final WeakReference<BaseService> mActivity;

    public CusHandler(BaseService service) {
      mActivity = new WeakReference<>(service);
    }

    @Override
    public void handleMessage(Message msg) {
      final BaseService activity = mActivity.get();
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
              int code = jsonObjectIntGetValue(jsonObject, "code");
              String msgPost = Utils.jsonObjectStringGetValue(jsonObject, "msg");
              if (code != 0) {
                Utils.show(msgPost);
              }
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
              int code = jsonObjectIntGetValue(jsonObject, "code");
              String msgGet = jsonObject.getString("msg");
              if (code != 0) {
                Utils.show(msgGet);
              }
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


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
