package com.cabbooking.utils;
import android.app.Application;

import com.google.firebase.FirebaseApp;

public class AppController extends Application {

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FirebaseApp.initializeApp(this);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }
}
