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
import com.cabbooking.utils.Common;
import com.cabbooking.utils.ConnectivityReceiver;
import com.cabbooking.utils.ToastMsg;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityLoginBinding binding;
    Common common;

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
    }

    public void initView() {
        common=new Common(this);
    }

    @Override
    public void onClick(View v) {
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

        String otp = "123456";
        String message="OTP send succesfully.";

        new ToastMsg(LoginActivity.this).toastIconSuccess(message);

        Intent i = new Intent(LoginActivity.this, OTPActivity.class);
        i.putExtra("mobile",number);
        i.putExtra("otp",otp);
        startActivity(i);
        finish();
    }
}