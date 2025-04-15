package com.cabbooking.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.cabbooking.activity.LoginActivity;


public class SessionManagment {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String PREFS_NAME="CabBooking";
    int PRIVATE_MODE = 0;
    public static String USER_NAME="user_name";
    public static String USER_ID="user_id";
    public static String USER_MOBILE="user_number";
    String LOGIN = "login";


    public SessionManagment(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = prefs.edit();

    }
    public void setValue(String key,String value) {
        editor.putBoolean(LOGIN, true);
        editor.putString(key, value);
        editor.apply();
    } public void setLoginValue() {
        editor.putBoolean(LOGIN, true);
        editor.apply();
    }
    public boolean isLogin() {
       return  prefs.getBoolean(LOGIN, false);
    }
    public String getValue(String key){
        return prefs.getString(key,"");
    }
    public  void logout( Context context){

        editor.clear();
        editor.commit();
        Intent logout = new Intent (context, LoginActivity.class);
        logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(logout);
        ((AppCompatActivity)context).finish();



    }

}
