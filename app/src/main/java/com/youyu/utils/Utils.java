package com.youyu.utils;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;
import com.youyu.applicatioin.MainApplication;
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
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
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
        TelephonyManager manager = (TelephonyManager) MainApplication.getInstance()
            .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method method = manager.getClass().getMethod("getImei", int.class);
            String imei1 = (String) method.invoke(manager, 0);
            String imei2 = (String) method.invoke(manager, 1);
            if (TextUtils.isEmpty(imei2)) {
                return imei1;
            }
            if (!TextUtils.isEmpty(imei1)) {
                //因为手机卡插在不同位置，获取到的imei1和imei2值会交换，所以取它们的最小值,保证拿到的imei都是同一个
                String imei = "";
                if (imei1.compareTo(imei2) <= 0) {
                    imei = imei1;
                } else {
                    imei = imei2;
                }
                return imei;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return manager.getDeviceId();
        }
        return "";
    }

}
