package com.cabbooking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cabbooking.R;

public class SplashActivity extends AppCompatActivity {
    final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                gotonext();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
    public void gotonext(){
        //if (sessionMangement.getKeyVal("is_login")){
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
//        }else {
//            Intent intent = new Intent(context, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }
}