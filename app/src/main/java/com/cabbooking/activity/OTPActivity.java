package com.cabbooking.activity;

import static com.cabbooking.utils.SessionManagment.KEY_MOBILE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.cabbooking.R;
import com.cabbooking.databinding.ActivityOtpactivityBinding;
import com.cabbooking.utils.Apis;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.ConnectivityReceiver;
import com.cabbooking.utils.LoadingBar;
import com.cabbooking.utils.RetrofitClient;
import com.cabbooking.utils.SessionManagment;
import com.cabbooking.utils.SmsListener;
import com.cabbooking.utils.SmsReceiver;
import com.cabbooking.utils.ToastMsg;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityOtpactivityBinding binding;
    Common common;
    CountDownTimer cTimer ;
    long mTimeLeftInMillis = 60000;
    String number="",otpget="";
    String msg_status="0",is_login="";
    public static final String OTP_REGEX = "[0-9]{3,6}";
    SessionManagment sessionManagment;
    LoadingBar loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_otpactivity);
        initView();
        setCounterTimer();
//        common.getAppSettingData(new OnConfig() {
//            @Override
//            public void getAppSettingData(AppSettingModel model) {
//                Log.d ("msg_Statsus", "getAppSettingData: "+String.valueOf (msg_status));
//                msg_status=model.getData ().getMsg_status();
                gerenateOtp();
//            }
//        });
        allClick();
    }
    public void allClick() {
        binding.btnSubmit.setOnClickListener(this);
        binding.tvResendOtp.setOnClickListener(this);
    }

    public void initView() {
        loadingBar=new LoadingBar(this);
        sessionManagment=new SessionManagment(this);
        common=new Common(this);
        number=getIntent ().getStringExtra ("mobile");
        otpget=getIntent ().getStringExtra ("otp");
        is_login=getIntent().getStringExtra("is_login");
    }

    @Override
    public void onClick(View v) {

        if(v.getId ()==R.id.btn_submit) {
            if(ConnectivityReceiver.isConnected()){
                onValidation();
            }else {
                common.noInternetDialog();
            }
        }
        else if(v.getId ()==R.id.tv_resend_otp) {
            if(ConnectivityReceiver.isConnected()){
                onResend();
            } else {
                common.noInternetDialog();
            }
        }
    }

    public void onResend() {
        binding.otpView.setText ("");
        resendOTP();
    }
    private void resendOTP() {
//        loadingBar.show();
//        Retrofit retrofit = RetrofitClient.getRetrofitInstanceNewWork();
//        LoginApi api = retrofit.create(LoginApi.class);
//        JsonObject object = new JsonObject();
//        object.addProperty("mobile_number",number);
//        object.addProperty("is_login",is_login);
//
//        Call<JsonObject> call = api.resendOTP(Config.API_KEY, object);
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                loadingBar.dismiss();
//                try {
//                    if (response.code() == 200 && response.body() != null) {
//                        JsonObject responseBody = response.body();
//                        Log.e("Response", responseBody.toString());
//
//                        if (responseBody.get("response").getAsBoolean()) { // Success status check
//                            String message = responseBody.get("message").getAsString();
 //                           otpget = responseBody.get("otp").getAsString();
        otpget="123456";
        String  message="OTP Resend Successfully.";

                            new ToastMsg(OTPActivity.this).toastIconSuccess(message);
                            setCounterTimer();
                            gerenateOtp();

//                        } else {
//                            String message = responseBody.get("message").getAsString();
//                            new ToastMsg(OtpVerificationActivity.this).toastIconSuccess(message);
//
//                            Log.e("Error", "Response status is false.");
//                        }
//                    } else {
//                        Log.e("Error", "Response code: " + response.code());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.e("Error", "Exception: " + e.getMessage());
//                }
//            }
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                loadingBar.dismiss();
//                new ToastMsg(OtpVerificationActivity.this).toastIconError(getString(R.string.error_toast));
//            }
//        });
    }

    public void onValidation() {
        String etOtp = binding.otpView.getText ().toString ();
        if(etOtp.isEmpty ()){
            common.errorToast (getString (R.string.OTP_Required));
            binding.otpView.requestFocus ();
        } else if(etOtp.length()<4) {
            common.errorToast (getString (R.string.OTP_short));
            binding.otpView.requestFocus ();
        } else {
            if (etOtp.equals(otpget)) {
                callOtpApi(number,etOtp);
            } else{
                common.errorToast (getString (R.string.OTP_wrong));
            }
        }
    }

    public void  setCounterTimer() {
        mTimeLeftInMillis=60000;
        if(cTimer!=null){
            cTimer.cancel ();
        }
        cTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
                int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
                String timeLeftFormatted = String.format (Locale.getDefault ( ), "%02d:%02d", minutes, seconds);
                binding.tvMin.setText (String.valueOf (String.format ("%02d", minutes)));
                binding.tvSec.setText (String.valueOf (String.format ("%02d", seconds)));
                if(timeLeftFormatted.equalsIgnoreCase ("00:30")) {
                    binding.tvResendOtp.setVisibility (View.VISIBLE);
                } else if (timeLeftFormatted.equalsIgnoreCase ("00:00")) {
                    binding.tvResendOtp.setVisibility (View.VISIBLE);
                    cTimer.cancel ( );
                    binding.otpView.setText ("");
                }
            }

            @Override
            public void onFinish() {
                otpget="";
                binding.otpView.setText ("");
                binding.tvText.setText("Timeout");
            }
        }.start();
    }

    private void getsmsOtp() {
        try {
            SmsReceiver.bindListener (new SmsListener( ) {
                @Override
                public void messageReceived(String messageText) {
                    Log.e ("Message", messageText);
                    Pattern pattern = Pattern.compile (OTP_REGEX);
                    Matcher matcher = pattern.matcher (messageText);
                    String otp = "";
                    while (matcher.find ( )) {
                        otp = matcher.group ( );
                    }

                    if (!(otp.isEmpty ( ) || otp.equals (""))) {
                        binding.otpView.setText (otp);
                    }
                }
            });
        } catch (Exception ex) {
            common.errorToast(ex.getMessage());
        }

    }


    public void gerenateOtp(){
        if(msg_status.equalsIgnoreCase("0")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.otpView.setText (otpget);
                }
            },2000);
        } else{
            getsmsOtp ();
        }
    }

    private void callOtpApi(String mobile,String otp)
    {
//        loadingBar.show();
//        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
//        Apis api = retrofit.create(Apis.class);
//        JsonObject object = new JsonObject();
//        JSONObject jsonObject = null;
//        try {
//                object.addProperty("mobile_number",mobile);
//                object.addProperty("otp",otp);
//                object.addProperty("is_login",is_login);
//
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//
//        Call<JsonObject> call = api.verifyOTP(object);
//        call.enqueue(new Callback<JsonObject>() {
//
//
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                loadingBar.dismiss();
//
//                try {
//                    if (response.code() == 200 && response.body() != null) {
//                        JsonObject responseBody = response.body();
//                        Log.e("Response", responseBody.toString());
//
//                        if (responseBody.get("response").getAsBoolean()) { // Success status check
//                            //  JsonObject data = responseBody.get("data").getAsJsonObject();
//                            String message = responseBody.get("message").getAsString();
//                            new ToastMsg(OTPActivity.this).toastIconSuccess(message);
//                            JSONObject jsonObject=new JSONObject(String.valueOf(responseBody.getAsJsonObject("data").getAsJsonObject()));
                            sessionManagment.setLoginValue();
                            sessionManagment.setValue(KEY_MOBILE,mobile);
                            Intent intent = new Intent(OTPActivity.this, MapActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
//                        }
//                        else {
//                            String message = responseBody.get("message").getAsString();
//                            new ToastMsg(OTPActivity.this).toastIconError(message);
//
//                        }
//                    } else {
//                        Log.e("Error", "Response code: " + response.code());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.e("Error", "Exception: " + e.getMessage());
//                }
//            }
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                loadingBar.dismiss();
//                new ToastMsg(OtpVerificationActivity.this).toastIconError(getString(R.string.error_toast));
//            }
//        });


    }

}