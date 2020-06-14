package com.example.kyngpook.Login_Signup;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginSharedPreferenceUtil {
    private Context context;
    private SharedPreferences prefs = null;

    public LoginSharedPreferenceUtil(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
    }

    //ID, 권한 저장하기
    public void setStringData(String key, String str) {
        prefs.edit().putString(key, str).apply();
    }

    public String getStringData(String key, String defaultV) {
        return prefs.getString(key, defaultV);
    }

    //자동 로그인 여부 저장하기
    public void setBooleanData(String key, Boolean v) {
        prefs.edit().putBoolean(key, v).apply();
    }

    public Boolean getBooleanData(String key, Boolean defaultV) {
        return prefs.getBoolean(key, defaultV);
    }

}