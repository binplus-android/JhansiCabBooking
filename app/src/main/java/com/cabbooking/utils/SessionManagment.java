package com.cabbooking.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.cabbooking.activity.LoginActivity;

import java.util.HashMap;


public class SessionManagment {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String PREFS_NAME="CabBooking";
    int PRIVATE_MODE = 0;
    public static String KEY_NAME="user_name";
    public static String KEY_ID="user_id";
    public static String KEY_MOBILE="user_number";
    public static String KEY_TYPE="type";
    public static String KEY_OUTSTATION_TYPE="outstation_type";
    String LOGIN = "login";
    private static final String DEVICE_TOKEN = "device_token";
    public static final String IS_LOGIN_SUCCESS="is_login_success";
    public static final String KEY_TOKEN="token";
    public static final String KEY_REFERCODE="refercode";
    public static final String KEY_SHARE_LINK="shareLink";
    public static final String KEY_TOKEN_TYPE="tokenType";
    public static final String KEY_USER_IMAGE="IMAGE";

    public SessionManagment(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = prefs.edit();

    }

    public void createLoginSession(String id, String token, String token_type,String refer_code,String image,String shareLink) {
        editor.putBoolean(LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_TOKEN, token);
        editor.putString (KEY_REFERCODE, refer_code);
        editor.putString(KEY_TOKEN_TYPE, token_type);
        editor.putString(KEY_USER_IMAGE, image);
        editor.putString(KEY_SHARE_LINK, shareLink);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_ID, prefs.getString(KEY_ID, ""));
        user.put(KEY_TOKEN, prefs.getString(KEY_TOKEN, ""));
        user.put(KEY_TOKEN_TYPE, prefs.getString(KEY_TOKEN_TYPE, ""));
        user.put(KEY_REFERCODE, prefs.getString(KEY_REFERCODE, ""));
        user.put(KEY_USER_IMAGE, prefs.getString(KEY_USER_IMAGE, ""));
        user.put(KEY_SHARE_LINK, prefs.getString(KEY_SHARE_LINK, ""));
        return user;
    }

    public void setValue(String key,String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void setLoginValue() {
        editor.putBoolean(LOGIN, true);
        editor.apply();
    }
    public boolean isLogin() {
       return  prefs.getBoolean(LOGIN, false);
    }
    public String getValue(String key){
        return prefs.getString(key,"");
    }
    public  void logout(Activity activity){
        editor.clear();
        editor.commit();
        Intent logout = new Intent (activity, LoginActivity.class);
        logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(logout);
        activity.finish();

    }

    public void addToken(String token) {
        editor.putString(DEVICE_TOKEN,token);
        editor.commit();
    }

    public String getToken() {
        return prefs.getString(DEVICE_TOKEN,"");
    }

}
