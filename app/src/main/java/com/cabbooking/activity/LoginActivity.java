package com.cabbooking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.cabbooking.R;
import com.cabbooking.databinding.ActivityLoginBinding;
import com.cabbooking.utils.Apis;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.ConnectivityReceiver;
import com.cabbooking.utils.LoadingBar;
import com.cabbooking.utils.RetrofitClient;
import com.cabbooking.utils.ToastMsg;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityLoginBinding binding;
    Common common;
    LoadingBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
        allClick();

    }
    public void allClick() {
        binding.btnOtp.setOnClickListener(this);
        binding.linSignup.setOnClickListener(this);
    }
    public void initView() {
        loadingBar=new LoadingBar(this);
        common=new Common(this);
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.lin_signup){
            Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(i);
        }
        if(v.getId()==R.id.btn_otp){
            if(ConnectivityReceiver.isConnected()){
                if (!common.isValidMobileNumber(binding.etMob.getText().toString())) {
                    new ToastMsg(LoginActivity.this).toastIconError(getString(R.string.enter_valid_mobile));
                }
                else {
                    String number = binding.etMob.getText().toString();
                    login(number);
                }
            }
            else{
                common.noInternetDialog();
            }
        }
    }
    public void login(String number){
      //  loadingBar.show();
//        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
//        Apis signUpApi = retrofit.create(Apis.class);
//        JsonObject object = new JsonObject();
//        object.addProperty("mobile",number);
//        Call<JsonObject> call = signUpApi.userlogin(object);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
              // loadingBar.dismiss();
//
//                try {
//                    if (response.code() == 200 && response.body() != null) {
//                        JsonObject responseBody = response.body();
//                        Log.e("Response", responseBody.toString());
//
//                        if (responseBody.get("response").getAsBoolean()) { // Success status check
//                            Log.e("hhhhh", "onResponse: " + response.body());
//                            JSONObject jsonObject=new JSONObject(String.valueOf(responseBody.getAsJsonObject("data").getAsJsonObject()));


                            String otp = "123456";
                            String message="OTP send successfully.";
                            new ToastMsg(LoginActivity.this).toastIconSuccess(message);
                            Intent i = new Intent(LoginActivity.this, OTPActivity.class);
                            i.putExtra("mobile",number);
                            i.putExtra("otp",otp);
                            startActivity(i);
                            finish();
//                        }
//                        else {
//
//                            new ToastMsg(LoginActivity.this).toastIconError(responseBody.get("message").getAsString());
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
//                new ToastMsg(LoginActivity.this).toastIconError(getString(R.string.error_toast));
//            }
//        });



    }
}