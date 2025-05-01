package com.cabbooking.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cabbooking.R;
import com.cabbooking.Response.LoginResp;
import com.cabbooking.databinding.ActivitySignUpBinding;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.ConnectivityReceiver;
import com.cabbooking.utils.Constants;
import com.cabbooking.utils.LoadingBar;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.cabbooking.utils.ToastMsg;
import com.google.gson.JsonObject;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySignUpBinding binding;
    Common common;
    LoadingBar loadingBar;
    SessionManagment sessionManagment;
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        if (Constants.REFERRAL_CODE!=null && !Constants.REFERRAL_CODE.isEmpty()) {
            Log.e("Signup_REFERRAL_CODE", Constants.REFERRAL_CODE);
        }

        SharedPreferences prefs = getSharedPreferences("ReferralPrefs", MODE_PRIVATE);
        String savedCode = prefs.getString("referral_code", null);
        if (savedCode != null) {
            Log.e("Signup_savedCode", savedCode);
        }

        initView();
        common.generateToken();
        allClick();

    }

    public void allClick() {
        binding.btnOtp.setOnClickListener(this);
        binding.linLogin.setOnClickListener(this);

    }

    public void initView() {
        repository=new Repository(this);
        sessionManagment=new SessionManagment(this);
        loadingBar=new LoadingBar(this);
        common = new Common(this);
        checkBoxCode();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lin_login) {
            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(i);
        }else if (v.getId() == R.id.btn_otp) {
            if (ConnectivityReceiver.isConnected()) {

                if (!common.isValidName(binding.etName.getText().toString())) {
                    new ToastMsg(SignUpActivity.this).toastIconError(getString(R.string.enter_valid_name));
                } else if (!common.isValidMobileNumber(binding.etMob.getText().toString())) {
                    new ToastMsg(SignUpActivity.this).toastIconError(getString(R.string.enter_valid_mobile));
                }
                if(binding.etEmail.getText().toString().isEmpty()){
                     if (!binding.chBox.isChecked()) {
                        new ToastMsg(SignUpActivity.this).toastIconError(getString(R.string.i_agree_check));
                    } else {
                        String name = binding.etName.getText().toString();
                        String number = binding.etMob.getText().toString();
                        String email = binding.etEmail.getText().toString();
                        signUp(number,name,email);
                    }
                }
                else {
                 if (!common.isValidEmailAddress(binding.etEmail.getText().toString())) {
                        new ToastMsg(SignUpActivity.this).toastIconError(getString(R.string.enter_valid_email));
                    } else if (!binding.chBox.isChecked()) {
                        new ToastMsg(SignUpActivity.this).toastIconError(getString(R.string.i_agree_check));
                    } else {
                        String name = binding.etName.getText().toString();
                        String number = binding.etMob.getText().toString();
                        String email = binding.etEmail.getText().toString();
                     signUp(number,name,email);
                    }
                }
            } else {
                common.noInternetDialog();
            }
        }
    }

    private void checkBoxCode() {
        TextView tvCheck = binding.tvCheck;
        CheckBox checkBox = binding.chBox;

        String fullText = "I agree to the Terms & Conditions and Privacy Policy";
        SpannableString spannable = new SpannableString(fullText);

        ClickableSpan termsSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent i = new Intent(SignUpActivity.this, PrivacyPolicyActivity.class);
                i.putExtra("type","terms");
                startActivity(i);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);  // Optional: remove underline
            }
        };

        int termsStart = fullText.indexOf("Terms & Conditions");
        spannable.setSpan(termsSpan, termsStart, termsStart + "Terms & Conditions".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), termsStart, termsStart + "Terms & Conditions".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent i = new Intent(SignUpActivity.this, PrivacyPolicyActivity.class);
                i.putExtra("type","policy");
                startActivity(i);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);  // Optional: remove underline
            }
        };

        int privacyStart = fullText.indexOf("Privacy Policy");
        spannable.setSpan(privacySpan, privacyStart, privacyStart + "Privacy Policy".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), privacyStart, privacyStart + "Privacy Policy".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvCheck.setText(spannable);
        tvCheck.setMovementMethod(LinkMovementMethod.getInstance());
        tvCheck.setHighlightColor(Color.TRANSPARENT);
    }


    public void signUp(String number,String name,String email){
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
                        JsonObject respObj=new JsonObject();
                        respObj.addProperty("mobile",number);
                        respObj.addProperty("otp",recordList.getOtp());
                        respObj.addProperty("is_login","0");
                        respObj.addProperty("name",name);
                        respObj.addProperty("email",email);
                        Intent i = new Intent(SignUpActivity.this, OTPActivity.class);;
                        i.putExtra("respobj",respObj.toString());
                        startActivity(i);
                        finish();
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