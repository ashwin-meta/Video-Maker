package com.photofusion.holi.videomaker.photo.slideshow.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {


    public static final String WA_TREE_URI = "firstvalue";
    public static final String WB_TREE_URI = "secoundvalue";
    private static SharedPreferences mPreferences;

    private static SharedPreferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = context.getApplicationContext()
                    .getSharedPreferences("stat_data", Context.MODE_PRIVATE);
        }
        return mPreferences;
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getInstance(context).getInt(key, defaultValue);
    }

    public static void setInt(Context context, String key, int value) {
        getInstance(context).edit().putInt(key, value).apply();
    }

    public static void clearPrefs(Context context) {
        getInstance(context).edit().clear().apply();
    }


    public static void setREMOTE_AD(Context context, String value) {
        getInstance(context).edit().putString(WB_TREE_URI, value).apply();
    }

    public static String getREMOTE_AD(Context context) {
        return getInstance(context).getString(WB_TREE_URI, "a");
    }
}
