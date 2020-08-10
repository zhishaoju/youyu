package com.youyu.gao.xiao.utils;

import android.content.Context;

import com.youyu.gao.xiao.applicatioin.MainApplication;

/**
 * @Author zsj
 * @Date 2020.04.08 23:20
 * @Commit
 */
public class SharedPrefsUtil {

    public final static String SETTING = "project_cache";
    public final static String LOGIN = "login";

    public static void put(String key, int value) {
        MainApplication.getInstance().getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    public static void put(String key, long value) {
        MainApplication.getInstance().getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit().putLong(key, value).commit();
    }

    public static void put(String key, boolean value) {
        MainApplication.getInstance().getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
    }

    public static void put(String key, String value) {
        MainApplication.getInstance().getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }

    public static int get(String key, int defValue) {
        return MainApplication.getInstance().getSharedPreferences(SETTING, Context.MODE_PRIVATE).getInt(key, defValue);
    }

    public static long get(String key, long defValue) {
        return MainApplication.getInstance().getSharedPreferences(SETTING, Context.MODE_PRIVATE).getLong(key, defValue);
    }

    public static boolean get(String key, boolean defValue) {
        return MainApplication.getInstance().getSharedPreferences(SETTING, Context.MODE_PRIVATE).getBoolean(key, defValue);
    }

    public static String get(String key, String defValue) {
        return MainApplication.getInstance().getSharedPreferences(SETTING, Context.MODE_PRIVATE).getString(key, defValue);
    }

    public static void remove(String key) {
        MainApplication.getInstance().getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit().remove(key).commit();
    }
}
