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
import com.cabbooking.Response.LoginResp;
import com.cabbooking.Response.OTPverificatioResp;
import com.cabbooking.databinding.ActivityOtpactivityBinding;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.utils.Apis;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.ConnectivityReceiver;
import com.cabbooking.utils.LoadingBar;
import com.cabbooking.utils.OnConfig;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
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
    int msg_status=0;
    String is_login="";
    Repository repository;
    String name="",email="";
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
        common.getAppSettingData(new OnConfig() {
            @Override
            public void getAppSettingData(AppSettingModel model) {

                msg_status=model.getMsg_status();
                gerenateOtp();
            }
        });
        allClick();
    }
    public void allClick() {
        binding.btnSubmit.setOnClickListener(this);
        binding.tvResendOtp.setOnClickListener(this);
    }

    public void initView() {
        repository=new Repository(this);
        loadingBar=new LoadingBar(this);
        sessionManagment=new SessionManagment(this);
        common=new Common(this);

        String jsonString=getIntent().getStringExtra("respobj");
        try {
            JSONObject receivedJson = new JSONObject(jsonString);
            number=receivedJson.getString("mobile");
            otpget=receivedJson.getString("otp");
            is_login=receivedJson.getString("is_login");
            if(is_login.equalsIgnoreCase("0")){
                name=receivedJson.getString("name");
                email=receivedJson.getString("email");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


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
     if(is_login.equalsIgnoreCase("1")) {
    JsonObject object = new JsonObject();
    object.addProperty("contactNo", number);
    repository.callLoginApi(object, new ResponseService() {
        @Override
        public void onResponse(Object data) {
            try {
                LoginResp resp = (LoginResp) data;
                Log.e("callLoginApi ", data.toString());
                if (resp.getStatus() == 200) {
                    LoginResp.RecordList recordList = resp.getRecordList();
                    otpget = recordList.getOtp();
                    common.successToast(resp.getMessage ());
                    setCounterTimer();
                    gerenateOtp();
                } else {
                    common.errorToast(resp.getError());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServerError(String errorMsg) {
            Log.e("errorMsg", errorMsg);
        }
    }, true);
}
     else{
    JsonObject object=new JsonObject();
    object.addProperty("contactNo",number);
    repository.callSignUpApi(object, new ResponseService() {
        @Override
        public void onResponse(Object data) {
            try {
                LoginResp resp = (LoginResp) data;
                Log.e("callSignupApi ",data.toString());
                if (resp.getStatus()==200) {
                    LoginResp.RecordList recordList = resp.getRecordList();
                    common.successToast(resp.getMessage ());
                    otpget = recordList.getOtp();
                    common.successToast(resp.getMessage ());
                    setCounterTimer();
                    gerenateOtp();
                }else{
                    common.errorToast(resp.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServerError(String errorMsg) {
            Log.e("errorMsg",errorMsg);
        }
    }, true);
}
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
        if(msg_status==0) {
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

    private void callOtpApi(String mobile,String otp) {
        JsonObject object=new JsonObject();
        object.addProperty("contactNo",mobile);
        object.addProperty("otp",otp);
        object.addProperty("isLogin",is_login);

        if(is_login.equalsIgnoreCase("0")) {
            if (!email.equalsIgnoreCase("")) {
                object.addProperty("email", email);
            }

            object.addProperty("name", name);
            object.addProperty("referral_code","");
        }

        repository.getOtpApi(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    OTPverificatioResp resp = (OTPverificatioResp) data;
                   // sessionManagment.createLoginSession(user_id, resp.getToken(),resp.getToken_type(),refercode);
                    Log.e("callOtpApi ",data.toString());
                    if (resp.getStatus() == 200) {
                        common.successToast(resp.getMessage());

                        if(is_login.equalsIgnoreCase("0")){
                            Intent intent = new Intent(OTPActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();

                        }else {
                            sessionManagment.setLoginValue();
                            sessionManagment.createLoginSession(String.valueOf(resp.getRecordList().getId()),
                                    resp.getToken(), resp.getToken_type(), resp.getRecordList().getOwn_refer_code(),
                                    resp.getRecordList().getProfileImage(),resp.getRecordList().getReferralLink());
                            sessionManagment.setValue(KEY_MOBILE, mobile);
                            Intent intent = new Intent(OTPActivity.this, MapActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();

                        }

                    }else{
                        common.errorToast(resp.getError());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onServerError(String errorMsg) {
                Log.e("errorMsg",errorMsg);
            }
        }, true);

    }

}