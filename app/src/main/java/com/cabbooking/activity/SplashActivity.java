package com.cabbooking.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.cabbooking.R;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Constants;
import com.cabbooking.utils.SessionManagment;

public class SplashActivity extends AppCompatActivity {
    final int SPLASH_DISPLAY_LENGTH = 2000;
    SessionManagment sessionManagment;
    Common common;
    private InstallReferrerClient referrerClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManagment=new SessionManagment(this);
        common=new Common(this);
        common.generateToken();
        common.getDeviceId();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                gotonext();
            }
        }, SPLASH_DISPLAY_LENGTH);


        Uri data = getIntent().getData();

        if (data != null && data.getQueryParameter("code") != null) {
            String referralCode = data.getQueryParameter("code");

            assert referralCode != null;
            Log.e("referralCode",referralCode);
            Constants.REFERRAL_CODE = referralCode;


            // Save referral code locally (in case app is installed later)
//            SharedPreferences prefs = getSharedPreferences("ReferralPrefs", MODE_PRIVATE);
//            prefs.edit().putString("referral_code", referralCode).apply();
        }


        // Check for saved referral code
        SharedPreferences prefs = getSharedPreferences("ReferralPrefs", MODE_PRIVATE);
        String savedCode = prefs.getString("referral_code", null);
        if (savedCode != null) {
            Log.e("savedCodecfvgbhnj",savedCode);
//            referralInput.setText(savedCode);
        } else {
            getInstallReferrer(); // Fetch from Install Referrer API
        }

    }



    private void getInstallReferrer() {
        referrerClient = InstallReferrerClient.newBuilder(this).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                    try {
                        ReferrerDetails response = referrerClient.getInstallReferrer();
                        String referrerUrl = response.getInstallReferrer();
                        Uri referrerUri = getIntent().getData();
//                        Uri referrerUri = Uri.parse("https://jhansicab.anshuwap.com/referal?" + referrerUrl);

                        String referralCode = referrerUri.getQueryParameter("code");

                        if (referralCode != null) {
                            Log.e("referralCodesxdcfgbhjn",referralCode);
//                            referralInput.setText(referralCode);
                            SharedPreferences prefs = getSharedPreferences("ReferralPrefs", MODE_PRIVATE);
                            prefs.edit().putString("referral_code", referralCode).apply();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
            }
        });
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