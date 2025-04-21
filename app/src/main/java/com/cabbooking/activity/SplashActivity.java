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
import com.cabbooking.utils.SessionManagment;

public class SplashActivity extends AppCompatActivity {
    final int SPLASH_DISPLAY_LENGTH = 2000;
    SessionManagment sessionManagment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManagment=new SessionManagment(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gotonext();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
    public void gotonext(){
        if (sessionManagment.isLogin()) {
            Intent intent = new Intent(SplashActivity.this, MapActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}