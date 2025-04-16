package com.cabbooking.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        initView();
        allClick();
    }

    public void allClick() {
        binding.btnOtp.setOnClickListener(this);
        binding.linLogin.setOnClickListener(this);

    }

    public void initView() {
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
                } else if (!common.isValidEmailAddress(binding.etEmail.getText().toString())) {
                    new ToastMsg(SignUpActivity.this).toastIconError(getString(R.string.enter_valid_email));
                } else if (!binding.chBox.isChecked()) {
                    new ToastMsg(SignUpActivity.this).toastIconError(getString(R.string.i_agree_check));
                } else {
                    String number = binding.etMob.getText().toString();
                    Signup(number);
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
                Intent intent = new Intent(SignUpActivity.this, PolicyActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(SignUpActivity.this, PolicyActivity.class);
                startActivity(intent);
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