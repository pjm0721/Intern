package com.example.kyngpook.Buyer;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
    private Context context;
    private SharedPreferences prefs = null;

    public SharedPreferenceUtil(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("User Info", Context.MODE_PRIVATE);
    }

    //ID, 전화번호, 닉네임, 주소
    public void setStringData(String key, String str) {
        prefs.edit().putString(key, str).apply();
    }

    public String getStringData(String key, String defaultV) {
        return prefs.getString(key, defaultV);
    }

}