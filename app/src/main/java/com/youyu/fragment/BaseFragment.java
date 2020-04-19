package com.youyu.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.youyu.net.NetInterface;
import com.youyu.utils.Contants;
import com.youyu.utils.JsonUtils;
import com.youyu.utils.LogUtil;
import com.youyu.utils.Utils;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseFragment extends Fragment {

  private final String TAG = BaseFragment.class.getSimpleName();

  private final Object mTag = new Object();

  private OkHttpClient mHttpClient;
  private NetInterface.RequestResponse mNetInteface;
  protected CusHandler mCusHandler;
  protected int mTaskFlag; // 1:取消任务；2:继续任务

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogUtil.showDLog(TAG, "onCreate()");
    initNet();
    mCusHandler = new CusHandler(this);
  }

  protected void setNetLisenter(NetInterface.RequestResponse requestResponse) {
    mNetInteface = requestResponse;
  }

  private void initNet() {
    if (mHttpClient == null) {
      mHttpClient = new OkHttpClient();
    }
  }

  protected void post(String url, String params) {
    if (mHttpClient == null) {
      mHttpClient = new OkHttpClient();
    }
    LogUtil.showELog(TAG, "post url = " + url);
    LogUtil.showELog(TAG, "post 请求params = " + params);
    if (JsonUtils.isJsonObject(params)) {
      FormBody.Builder body = new FormBody.Builder();
      try {
        JSONObject jsonObject = new JSONObject(params);
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
          String key = keys.next();
          String value = jsonObject.getString(key);
          body.add(key, value);
        }
        RequestBody requestBody = body.build();
        Request request = new Request.Builder()
            .post(requestBody)
            .url(url)
            .tag(mTag)
            .build();
        mHttpClient.newCall(request).enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
            LogUtil.showELog(TAG, "post(String url, String params) "
                + "onFailure(Call call, IOException e) e = " + e.toString());
            Message mFail = new Message();
            mFail.obj = e;
            mFail.what = 1;
            mCusHandler.sendMessage(mFail);
          }

          @Override
          public void onResponse(Call call, Response response) throws IOException {
            String resposeStr = response.body().string();
            LogUtil.showELog(TAG, "post(String url, String params) "
                + "onResponse(Call call, Response response) "
                + "success: " + resposeStr);
            Message mSucces = new Message();
            mSucces.obj = resposeStr;
            mSucces.what = 2;
            mCusHandler.sendMessage(mSucces);
          }
        });

      } catch (JSONException e) {
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

  protected static class CusHandler extends Handler {

    private final WeakReference<BaseFragment> mFragment;

    public CusHandler(BaseFragment fragment) {
      mFragment = new WeakReference<>(fragment);
    }

    @Override
    public void handleMessage(Message msg) {
      final BaseFragment fragment = mFragment.get();
      if (fragment == null) {
        super.handleMessage(msg);
        return;
      }
      fragment.cusHandleMessage(msg);
    }
  }

  protected void cusHandleMessage(Message msg) {
    switch (msg.what) {
      case 1: // post 请求失败的情况
        mNetInteface.failure((Exception) msg.obj);
        break;
      case 2: // post 请求成功的情况
        String resposeStr = (String) msg.obj;
        try {
          if (JsonUtils.isJsonObject(resposeStr)) {
            JSONObject jsonObject = null;
            try {
              jsonObject = new JSONObject(resposeStr);
              String state = Utils.jsonObjectStringGetValue(jsonObject, "state");
              String msgPost = Utils.jsonObjectStringGetValue(jsonObject, "msg");
              Utils.show(msgPost);
              if (Contants.NetStatus.OK.equals(state)) {
                mNetInteface.success(resposeStr);
              }
            } catch (JSONException e) {
              LogUtil.showELog(TAG, "cusHandleMessage case 2 post JSONException:" + e.toString());
            }
          }
        } catch (Exception e) {
          LogUtil.showELog(TAG, "cusHandleMessage case 2 catch (Exception e) e:" + e.toString());
        }
        break;
      case 3:// get请求失败的情况
        mNetInteface.failure((Exception) msg.obj);
        break;
      case 4: // get请求成功的情况
        String sdata = (String) msg.obj;
        try {
          if (JsonUtils.isJsonObject(sdata)) {
            JSONObject jsonObject = null;
            try {
              jsonObject = new JSONObject(sdata);
              String state = Utils.jsonObjectStringGetValue(jsonObject, "state");
              String msgGet = Utils.jsonObjectStringGetValue(jsonObject, "msg");
              Utils.show(msgGet);
              if (Contants.NetStatus.OK.equals(state)) {
                mNetInteface.success(sdata);
              }
            } catch (JSONException e) {
              LogUtil.showELog(TAG, "cusHandleMessage case 4 get JSONException:" + e.getMessage());
            }
          }
        } catch (Exception e) {
          LogUtil.showELog(TAG, "cusHandleMessage case 4 get e:" + e.toString());
        }
        break;
    }
  }
}
