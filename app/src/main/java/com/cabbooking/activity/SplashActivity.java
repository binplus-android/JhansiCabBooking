package com.cabbooking.activity;

import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;
import static com.cabbooking.utils.SessionManagment.KEY_ENQUIRY;
import static com.cabbooking.utils.SessionManagment.KEY_HOME_IMG1;
import static com.cabbooking.utils.SessionManagment.KEY_HOME_IMG2;
import static com.cabbooking.utils.SessionManagment.KEY_ID;
import static com.cabbooking.utils.SessionManagment.KEY_NOTIFICATION;
import static com.cabbooking.utils.SessionManagment.KEY_PRIVACY;
import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_EMAIL;
import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_MOBILE;
import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_SUBJ;
import static com.cabbooking.utils.SessionManagment.KEY_TERMS;
import static com.cabbooking.utils.SessionManagment.KEY_WHATSPP;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.cabbooking.Response.NotificationResp;
import com.cabbooking.adapter.EnquiryAdapter;
import com.cabbooking.adapter.NotificationAdapter;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.model.EnquiryModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Constants;
import com.cabbooking.utils.OnConfig;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    final int SPLASH_DISPLAY_LENGTH = 2000;
    SessionManagment sessionManagment;
    Common common;
    private InstallReferrerClient referrerClient;
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManagment=new SessionManagment(this);
        common=new Common(this);
        repository=new Repository(this);
        sessionStoring();


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

    private void sessionStoring() {
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        //common data
        common.getAppSettingData(new OnConfig() {
            @Override
            public void getAppSettingData(AppSettingModel model) {
                sessionManagment.setValue(KEY_TERMS,model.getTerms_conditions());
                sessionManagment.setValue(KEY_PRIVACY,model.getPrivacy_policy());
                sessionManagment.setValue(KEY_SUPPORT_EMAIL,model.getSupport_email());
                sessionManagment.setValue(KEY_SUPPORT_MOBILE,model.getSupport_mobile());
                sessionManagment.setValue(KEY_WHATSPP,model.getSupport_whatsapp());
                sessionManagment.setValue(KEY_SUPPORT_SUBJ,model.getSupport_message());
                sessionManagment.setValue(KEY_HOME_IMG1,model.getHomeImage1());
                sessionManagment.setValue(KEY_HOME_IMG2,model.getHomeImage2());
            }
        });
        //enquiry
            repository.getEnquiryList(object, new ResponseService() {
                @Override
                public void onResponse(Object data) {
                    try {
                        EnquiryModel resp = (EnquiryModel) data;
                        Log.e("getEnquiryresp ",data.toString());
                        if (resp.getStatus()==200) {
                            Gson gson = new Gson();
                            String jsonList = gson.toJson(resp.getRecordList());
                            sessionManagment.setValue(KEY_ENQUIRY, jsonList);
                        }
                        else{
                            sessionManagment.setValue(KEY_ENQUIRY, "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onServerError(String errorMsg) {
                    Log.e("errorMsg",errorMsg);
                }
            }, false);
//notification

//        repository.getNotification(object, new ResponseService() {
//            @Override
//            public void onResponse(Object data) {
//                try {
//                    NotificationResp resp = (NotificationResp) data;
//                    Log.e("getNotificationResp ",data.toString());
//                    if (resp.getStatus()==200) {
//                        Gson gson = new Gson();
//                        String jsonList = gson.toJson(resp.getRecordList());
//                        sessionManagment.setValue(KEY_NOTIFICATION, jsonList);
//                    }
//                    else{
//                        sessionManagment.setValue(KEY_NOTIFICATION, "");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onServerError(String errorMsg) {
//                Log.e("errorMsg",errorMsg);
//            }
//        }, false);


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

                        if (referrerUri!=null) {
                            String referralCode = referrerUri.getQueryParameter("code");

                            if (referralCode != null) {
                                Log.e("referralCodesxdcfgbhjn", referralCode);
//                            referralInput.setText(referralCode);
                                SharedPreferences prefs = getSharedPreferences("ReferralPrefs", MODE_PRIVATE);
                                prefs.edit().putString("referral_code", referralCode).apply();
                            }
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