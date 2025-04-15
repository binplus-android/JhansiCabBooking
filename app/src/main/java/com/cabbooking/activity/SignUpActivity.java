package com.cabbooking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.cabbooking.R;
import com.cabbooking.databinding.ActivitySignUpBinding;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.ConnectivityReceiver;
import com.cabbooking.utils.ToastMsg;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySignUpBinding binding;
    Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        initView();
        allClick();

    }

    public void allClick() {
        binding.btnOtp.setOnClickListener(this);
        binding.linLogin.setOnClickListener(this);
    }

    public void initView() {
        common=new Common(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.lin_signup){
            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(i);
        }
        if(v.getId()==R.id.btn_otp){
            if(ConnectivityReceiver.isConnected()){

                if (!common.isValidName(binding.etName.getText().toString())) {
                    new ToastMsg(SignUpActivity.this).toastIconError(getString(R.string.enter_valid_name));
                }else if (!common.isValidMobileNumber(binding.etMob.getText().toString())) {
                    new ToastMsg(SignUpActivity.this).toastIconError(getString(R.string.enter_valid_mobile));
                }
                else if (!common.isValidEmailAddress(binding.etEmail.getText().toString())) {
                    new ToastMsg(SignUpActivity.this).toastIconError(getString(R.string.enter_valid_email));
                }
                else {
                    String number = binding.etMob.getText().toString();
                    Signup(number);
                }
            }
            else{
                common.noInternetDialog();
            }
        }
    }

    private void Signup(String number) {
        String otp = "123456";
        String message="OTP send succesfully.";
        new ToastMsg(SignUpActivity.this).toastIconSuccess(message);
        Intent i = new Intent(SignUpActivity.this, OTPActivity.class);
        i.putExtra("mobile",number);
        i.putExtra("otp",otp);
        startActivity(i);
        finish();
    }

}