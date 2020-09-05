package com.youyu.gao.xiao.utils;


import static android.content.Context.TELEPHONY_SERVICE;

import android.Manifest.permission;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.applicatioin.MainApplication;
import com.youyu.gao.xiao.bean.ActiveItemUi;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author zsj
 * @Date 2020.04.08 23:20
 * @Commit
 */
public class Utils {

  private static final String TAG = Utils.class.getSimpleName();

  //保存文件的路径
  private static final String CACHE_IMAGE_DIR = "aray/cache/devices";
  //保存的文件 采用隐藏文件的形式进行保存
  private static final String DEVICES_FILE_NAME = ".DEVICES";
  private static final String UPLOAD_PIC_PATH =
      Environment.getExternalStorageDirectory() + "/upload";


  public static float getScreenWidthDp(Context context) {
    final float scale = context.getResources().getDisplayMetrics().density;
    float width = context.getResources().getDisplayMetrics().widthPixels;
    return width / (scale <= 0 ? 1 : scale) + 0.5f;
  }

  //全面屏、刘海屏适配
  public static float getHeight(Activity activity) {
    hideBottomUIMenu(activity);
    float height;
    int realHeight = getRealHeight(activity);
    if (hasNotchScreen(activity)) {
      height = px2dip(activity, realHeight - getStatusBarHeight(activity));
    } else {
      height = px2dip(activity, realHeight);
    }
    return height;
  }

  public static void hideBottomUIMenu(Activity activity) {
    if (activity == null) {
      return;
    }
    try {
      //隐藏虚拟按键，并且全屏
      if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
        View v = activity.getWindow().getDecorView();
        v.setSystemUiVisibility(View.GONE);
      } else if (Build.VERSION.SDK_INT >= 19) {
        //for new api versions.
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            //                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //获取屏幕真实高度，不包含下方虚拟导航栏
  public static int getRealHeight(Context context) {
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = windowManager.getDefaultDisplay();
    DisplayMetrics dm = new DisplayMetrics();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      display.getRealMetrics(dm);
    } else {
      display.getMetrics(dm);
    }
    int realHeight = dm.heightPixels;
    return realHeight;
  }

  //获取状态栏高度
  public static float getStatusBarHeight(Context context) {
    float height = 0;
    int resourceId = context.getApplicationContext().getResources()
        .getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      height = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
    }
    return height;
  }

  public static int px2dip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
  }

  /**
   * 判断是否是刘海屏
   */
  public static boolean hasNotchScreen(Activity activity) {
    if (getInt("ro.miui.notch", activity) == 1 || hasNotchAtHuawei(activity) || hasNotchAtOPPO(
        activity)
        || hasNotchAtVivo(activity) || isAndroidPHasNotch(activity)) { //TODO 各种品牌
      return true;
    }

    return false;
  }

  /**
   * Android P 刘海屏判断
   */
  public static boolean isAndroidPHasNotch(Activity activity) {
    boolean ret = false;
    if (android.os.Build.VERSION.SDK_INT >= 28) {
      try {
        Class windowInsets = Class.forName("android.view.WindowInsets");
        Method method = windowInsets.getMethod("getDisplayCutout");
        Object displayCutout = method.invoke(windowInsets);
        if (displayCutout != null) {
          ret = true;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return ret;
  }

  /**
   * 小米刘海屏判断.
   *
   * @return 0 if it is not notch ; return 1 means notch
   * @throws IllegalArgumentException if the key exceeds 32 characters
   */
  public static int getInt(String key, Activity activity) {
    int result = 0;
    if (isMiui()) {
      try {
        ClassLoader classLoader = activity.getClassLoader();
        @SuppressWarnings("rawtypes")
        Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
        //参数类型
        @SuppressWarnings("rawtypes")
        Class[] paramTypes = new Class[2];
        paramTypes[0] = String.class;
        paramTypes[1] = int.class;
        Method getInt = SystemProperties.getMethod("getInt", paramTypes);
        //参数
        Object[] params = new Object[2];
        params[0] = new String(key);
        params[1] = new Integer(0);
        result = (Integer) getInt.invoke(SystemProperties, params);

      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * 华为刘海屏判断
   */
  public static boolean hasNotchAtHuawei(Context context) {
    boolean ret = false;
    try {
      ClassLoader classLoader = context.getClassLoader();
      Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
      Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
      ret = (boolean) get.invoke(HwNotchSizeUtil);
    } catch (ClassNotFoundException e) {
    } catch (NoSuchMethodException e) {
    } catch (Exception e) {
    } finally {
      return ret;
    }
  }

  public static final int VIVO_NOTCH = 0x00000020;//是否有刘海
  public static final int VIVO_FILLET = 0x00000008;//是否有圆角

  /**
   * VIVO刘海屏判断
   */
  public static boolean hasNotchAtVivo(Context context) {
    boolean ret = false;
    try {
      ClassLoader classLoader = context.getClassLoader();
      Class FtFeature = classLoader.loadClass("android.util.FtFeature");
      Method method = FtFeature.getMethod("isFeatureSupport", int.class);
      ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
    } catch (ClassNotFoundException e) {
    } catch (NoSuchMethodException e) {
    } catch (Exception e) {
    } finally {
      return ret;
    }
  }

  /**
   * O-P-P-O刘海屏判断
   */
  public static boolean hasNotchAtOPPO(Context context) {
    String temp = "com.kllk.feature.screen.heteromorphism";
    String name = getKllkDecryptString(temp);
    return context.getPackageManager().hasSystemFeature(name);
  }

  public static boolean isMiui() {
    boolean sIsMiui = false;
    try {
      Class<?> clz = Class.forName("miui.os.Build");
      if (clz != null) {
        sIsMiui = true;
        //noinspection ConstantConditions
        return sIsMiui;
      }
    } catch (Exception e) {
      // ignore
    }
    return sIsMiui;
  }

  /**
   * 用于o-p-p-o 版本隐私协议
   */
  public static String getKllkDecryptString(String encryptionString) {

    if (TextUtils.isEmpty(encryptionString)) {
      return "";
    }
    String decryptTag = "";
    String decryptCapitalized = "O" + "P" + "P" + "O";
    String decrypt = "o" + "p" + "p" + "o";
    if (encryptionString.contains("KLLK")) {
      decryptTag = encryptionString.replace("KLLK", decryptCapitalized);
    } else if (encryptionString.contains("kllk")) {
      decryptTag = encryptionString.replace("kllk", decrypt);
    }
    return decryptTag;

  }

  public static void setShowOnLocked(Activity activity) {
    try {
      if (activity == null) {
        return;
      }
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
          | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    } catch (Throwable t) {
    }
  }

  public static long getVersionCode() {
    long versionCode = 0;
//    try {
//      PackageInfo packageInfo = MainApplication.getInstance().getPackageManager()
//          .getPackageInfo(PACKAGE_NAME, 0);
//      versionCode = packageInfo.getLongVersionCode();
//    } catch (NameNotFoundException e) {
//      e.printStackTrace();
//      LogUtil.showELog(TAG, "getVersionCode e = " + e.getLocalizedMessage());
//    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
      PackageManager packageManager = MainApplication.getInstance().getPackageManager();
      try {
        PackageInfo packageInfo = packageManager
            .getPackageInfo(MainApplication.getInstance().getPackageName(), 0);
        versionCode = packageInfo.versionCode;
      } catch (Exception e) {
        LogUtil.showELog(TAG, "getVersionCode 1 e = " + e.getLocalizedMessage());
      }
    } else {
      PackageManager packageManager = MainApplication.getInstance().getPackageManager();
      try {
        PackageInfo packageInfo = packageManager
            .getPackageInfo(MainApplication.getInstance().getPackageName(), 0);
        versionCode = packageInfo.getLongVersionCode();
      } catch (Exception e) {
        LogUtil.showELog(TAG, "getVersionCode 2 e = " + e.getLocalizedMessage());
      }
    }

    return versionCode;
  }

  public static void show(String msg) {
    if (!TextUtils.isEmpty(msg) && !("null".equals(msg))) {
      Toast.makeText(MainApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }
  }

  public static String getDeviceId() {
    //读取保存的在sd卡中的唯一标识符
    String deviceId = null;
    try {
      deviceId = readDeviceID();
    } catch (IOException e) {
      e.printStackTrace();
    }
    //用于生成最终的唯一标识符
    StringBuffer s = new StringBuffer();
    //判断是否已经生成过,
    if (deviceId != null && !"".equals(deviceId)) {
      return deviceId;
    }
    try {
      //获取IMES(也就是常说的DeviceId)
//      deviceId = getIMIEStatus();
      deviceId = "";
      s.append(deviceId);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      //获取设备的MACAddress地址 去掉中间相隔的冒号
      deviceId = getLocalMac().replace(":", "");
      s.append(deviceId);
    } catch (Exception e) {
      e.printStackTrace();
    }
//        }

    //如果以上搜没有获取相应的则自己生成相应的UUID作为相应设备唯一标识符
    if (s == null || s.length() <= 0) {
      UUID uuid = UUID.randomUUID();
      deviceId = uuid.toString().replace("-", "");
      s.append(deviceId);
    }
    //为了统一格式对设备的唯一标识进行md5加密 最终生成32位字符串
    String md5 = getMD5(s.toString(), false);
    if (s.length() > 0) {
      //持久化操作, 进行保存到SD卡中
      saveDeviceID(md5);
    }
    return md5;
  }

  public static void saveDeviceID(String str) {
    File file = getDevicesDir();
    try {
      FileOutputStream fos = new FileOutputStream(file);
      Writer out = new OutputStreamWriter(fos, "UTF-8");
      out.write(str);
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String readDeviceID() throws IOException {
    File file = getDevicesDir();
    StringBuffer buffer = new StringBuffer();
    try {
      FileInputStream fis = new FileInputStream(file);
      InputStreamReader isr = null;
      try {
        isr = new InputStreamReader(fis, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      Reader in = new BufferedReader(isr);
      int i;
      while ((i = in.read()) > -1) {
        buffer.append((char) i);
      }
      in.close();
      return buffer.toString();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static File getDevicesDir() {
    File mCropFile = null;
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      File cropdir = new File(Environment.getExternalStorageDirectory(), CACHE_IMAGE_DIR);
      if (!cropdir.exists()) {
        cropdir.mkdirs();
      }
      mCropFile = new File(cropdir, DEVICES_FILE_NAME); // 用当前时间给取得的图片命名
    } else {
      File cropdir = new File(MainApplication.getInstance().getFilesDir(), CACHE_IMAGE_DIR);
      if (!cropdir.exists()) {
        cropdir.mkdirs();
      }
      mCropFile = new File(cropdir, DEVICES_FILE_NAME);
    }
    return mCropFile;
  }

  public static String getMD5(String message, boolean upperCase) {
    String md5str = "";
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");

      byte[] input = message.getBytes();

      byte[] buff = md.digest(input);

      md5str = bytesToHex(buff, upperCase);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return md5str;
  }

  public static String bytesToHex(byte[] bytes, boolean upperCase) {
    StringBuffer md5str = new StringBuffer();
    int digital;
    for (int i = 0; i < bytes.length; i++) {
      digital = bytes[i];

      if (digital < 0) {
        digital += 256;
      }
      if (digital < 16) {
        md5str.append("0");
      }
      md5str.append(Integer.toHexString(digital));
    }
    if (upperCase) {
      return md5str.toString().toUpperCase();
    }
    return md5str.toString().toLowerCase();
  }

  private static String getLocalMac() {
    String macAddress = null;
    StringBuffer buf = new StringBuffer();
    NetworkInterface networkInterface = null;
    try {
      networkInterface = NetworkInterface.getByName("eth1");
      if (networkInterface == null) {
        networkInterface = NetworkInterface.getByName("wlan0");
      }
      if (networkInterface == null) {
        return "";
      }
      byte[] addr = networkInterface.getHardwareAddress();

      for (byte b : addr) {
        buf.append(String.format("%02X:", b));
      }
      if (buf.length() > 0) {
        buf.deleteCharAt(buf.length() - 1);
      }
      macAddress = buf.toString();
    } catch (SocketException e) {
      e.printStackTrace();
      return "";
    }
    return macAddress;
  }

  public static String getAssetFile(String fileName) {
    AssetManager am = MainApplication.getInstance().getAssets();
    InputStream is = null;
    try {
      is = am.open(fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
    //read the stream
    String province = read(is);
    return province;
  }

  private static String read(InputStream in) {
    BufferedReader br = null;
    StringBuffer sb = new StringBuffer();
    sb.append("");
    try {
      String str;
      //convert inputStream to reader
      br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
      while ((str = br.readLine()) != null) {
        sb.append(str);
        sb.append("\n");
      }
    } catch (UnsupportedEncodingException e) {
      show("文本编码出现异常");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
        if (br != null) {
          br.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    return sb.toString();
  }


  //建立保存头像的路径及名称
  public static File getOutputMediaFile(String picName) {// 防止图片被冲掉
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//                + "/Android/data/"
//                + MainApplication.getInstance().getPackageName()
//                + "/Files");
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                LogUtil.showELog(TAG, mediaStorageDir.getPath() + "目录创建失败");
//                return Environment.getExternalStorageDirectory();
//            }
//        }
    File mediaStorageDir = new File(UPLOAD_PIC_PATH);
    if (!mediaStorageDir.exists()) {
      mediaStorageDir.mkdirs();
    }
    File mediaFile;
    String mImageName = picName + ".png";
    mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
    return mediaFile;
  }

  //保存图像
  public static void storeImage(Bitmap image, String picName) {
    File pictureFile = getOutputMediaFile(picName);
    if (pictureFile == null) {
      LogUtil.showELog(TAG,
          "Error creating media file, check storage permissions: ");// e.getMessage());
      return;
    }
    try {
      FileOutputStream fos = new FileOutputStream(pictureFile);
      image.compress(Bitmap.CompressFormat.PNG, 100, fos);
      fos.flush();
      fos.close();
    } catch (FileNotFoundException e) {
      LogUtil.showELog(TAG, "File not found: " + e.getMessage());
    } catch (IOException e) {
      LogUtil.showELog(TAG, "Error accessing file: " + e.getMessage());
    }
  }

  public static String jsonObjectStringGetValue(JSONObject jo, String key) {
    LogUtil.showELog(TAG, "jsonObjectStringGetValue(JSONObject jo, String key)"
        + "jo = " + jo.toString() + "; key = " + key);
    if (jo == null || key == null) {
      return "";
    }
    String v = "";
    if (!jo.isNull(key)) {
      try {
        v = jo.getString(key);
      } catch (JSONException e) {
        LogUtil.showELog(TAG, "jsonObjectStringGetValue JSONException e = " + e.toString());
      }
    }
    return v;
  }

  public static int jsonObjectIntGetValue(JSONObject jo, String key) {
    LogUtil.showELog(TAG, "jsonObjectIntGetValue(JSONObject jo, String key)"
        + "jo = " + jo.toString() + "; key = " + key);
    if (jo == null || key == null) {
      return -1;
    }
    int v = -1;
    if (!jo.isNull(key)) {
      try {
        v = jo.getInt(key);
      } catch (JSONException e) {
        LogUtil.showELog(TAG, "jsonObjectIntGetValue JSONException e = " + e.toString());
      }
    }
    return v;
  }

  /**
   * 根据Uri返回文件绝对路径 兼容了file:///开头的 和 content://开头的情况
   */
  public static String getRealFilePathFromUri(final Context context, final Uri uri) {
    if (null == uri) {
      return null;
    }
    final String scheme = uri.getScheme();
    String data = null;
    if (scheme == null) {
      data = uri.getPath();
    } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(scheme)) {
      data = uri.getPath();
    } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(scheme)) {
      Cursor cursor = context.getContentResolver()
          .query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
      if (null != cursor) {
        if (cursor.moveToFirst()) {
          int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
          if (index > -1) {
            data = cursor.getString(index);
          }
        }
        cursor.close();
      }
    }
    return data;
  }

  /**
   * 检查文件是否存在
   */
  public static String checkDirPath(String dirPath) {
    if (TextUtils.isEmpty(dirPath)) {
      return "";
    }
    File dir = new File(dirPath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    return dirPath;
  }

  public static byte[] bitmapToBytes(Bitmap bitmap) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
    byte[] bytes = baos.toByteArray();
    return bytes;
  }

  public static String getStringByStringXml(int id) {
    String s = MainApplication.getInstance().getResources().getString(id);
    if (!TextUtils.isEmpty(s)) {
      return s;
    }
    return "";
  }

  public static int dip2px(float dpValue) {
    final float scale = MainApplication.getInstance().getResources()
        .getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  public static void setToClipboard(String content) {
    //获取剪贴板管理器：
    ClipboardManager cm = (ClipboardManager) MainApplication.getInstance()
        .getSystemService(Context.CLIPBOARD_SERVICE);
    // 创建普通字符型ClipData
    ClipData mClipData = ClipData.newPlainText("Label", content);
    // 将ClipData内容放到系统剪贴板里。
    cm.setPrimaryClip(mClipData);
  }

  public static int[] getWH() {
    WindowManager windowManager = (WindowManager) MainApplication.getInstance()
        .getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics outMetrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getMetrics(outMetrics);
    int widthPixels = outMetrics.widthPixels;
    int heightPixels = outMetrics.heightPixels;
    LogUtil.showDLog(TAG, "widthPixels = " + widthPixels + ",heightPixels = " + heightPixels);
    int[] result = {widthPixels, heightPixels};
    return result;
  }

  public static String getIMEI() {
    String imei = "";
    try {
      TelephonyManager tm = (TelephonyManager) MainApplication.getInstance()
          .getSystemService(TELEPHONY_SERVICE);
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        if (ActivityCompat
            .checkSelfPermission(MainApplication.getInstance(), permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
          // TODO: Consider calling
          //    ActivityCompat#requestPermissions
          // here to request the missing permissions, and then overriding
          //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
          //                                          int[] grantResults)
          // to handle the case where the user grants the permission. See the documentation
          // for ActivityCompat#requestPermissions for more details.
          return "";
        }
        imei = tm.getDeviceId();
      } else {
        Method method = tm.getClass().getMethod("getImei");
        imei = (String) method.invoke(tm);
      }
    } catch (Exception e) {
      LogUtil.showELog(TAG, "getIMEI e = " + e.getLocalizedMessage());
    }

    if (TextUtils.isEmpty(imei)) {
      imei = Settings.System.getString(
          MainApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    return imei;

//    TelephonyManager manager = (TelephonyManager) MainApplication.getInstance()
//        .getSystemService(TELEPHONY_SERVICE);
//    try {
//      Method method = manager.getClass().getMethod("getImei", int.class);
//      String imei1 = (String) method.invoke(manager, 0);
//      String imei2 = (String) method.invoke(manager, 1);
//      if (TextUtils.isEmpty(imei2)) {
//        return imei1;
//      }
//      if (!TextUtils.isEmpty(imei1)) {
//        //因为手机卡插在不同位置，获取到的imei1和imei2值会交换，所以取它们的最小值,保证拿到的imei都是同一个
//        String imei = "";
//        if (imei1.compareTo(imei2) <= 0) {
//          imei = imei1;
//        } else {
//          imei = imei2;
//        }
//        return imei;
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//      return manager.getDeviceId();
//    }
//    return "";
  }

  public static ActiveItemUi transform(int status) {
    ActiveItemUi activeItemUi = new ActiveItemUi();
    switch (status) {
      case 0:
        activeItemUi.stateName = "未开始";
        activeItemUi.bgValue = R.drawable.corner_second_no_start_bg;
        break;
      case 1:
        activeItemUi.stateName = "进行中";
        activeItemUi.bgValue = R.drawable.corner_second_hava_in_hand_bg;
        break;
      case 2:
        activeItemUi.stateName = "已结束";
        activeItemUi.bgValue = R.drawable.corner_second_finish_bg;
        break;
      default:
        break;
    }
    return activeItemUi;
  }

  public static Properties getProperties(Context c) {
    Properties urlProps;
    Properties props = new Properties();
    InputStream in = null;
    try {
      //方法一：通过activity中的context攻取setting.properties的FileInputStream
      //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
      //InputStream in = c.getAssets().open("appConfig.properties");
      in = c.getAssets().open("appConfig");
      //方法二：通过class获取setting.properties的FileInputStream
      //InputStream in = PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
      props.load(in);
    } catch (Exception e1) {
      e1.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    urlProps = props;
    return urlProps;
  }

  public static String fromPropertiesGetValue(String key) {
    Properties proper = getProperties(MainApplication.getInstance());
    String value = proper.getProperty(key);
    return value;
  }
}
