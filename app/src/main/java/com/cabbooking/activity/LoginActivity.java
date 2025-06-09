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
import com.cabbooking.Response.LoginResp;
import com.cabbooking.databinding.ActivityLoginBinding;
import com.cabbooking.utils.Apis;
import com.cabbooking.utils.BaseActivity;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.ConnectivityReceiver;
import com.cabbooking.utils.LoadingBar;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.RetrofitClient;
import com.cabbooking.utils.ToastMsg;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    ActivityLoginBinding binding;
    Common common;
    LoadingBar loadingBar;
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
        common.generateToken();
        allClick();

    }
    public void allClick() {
        binding.btnOtp.setOnClickListener(this);
        binding.linSignup.setOnClickListener(this);
    }
    public void initView() {
        repository=new Repository(LoginActivity.this);
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
           // if(ConnectivityReceiver.isConnected()){
                if (!common.isValidMobileNumber(binding.etMob.getText().toString())) {
                    new ToastMsg(LoginActivity.this).toastIconError(getString(R.string.enter_valid_mobile));
                }
                else {
                    String number = binding.etMob.getText().toString();
                    login(number);
                }
//            }
//            else{
//                common.noInternetDialog();
//            }
        }
    }
    public void login(String number){
        JsonObject object=new JsonObject();
        object.addProperty("contactNo",number);
        repository.callLoginApi(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    LoginResp resp = (LoginResp) data;
                    Log.e("callLoginApi ",data.toString());
                    if (resp.getStatus()==200) {
                        LoginResp.RecordList recordList = resp.getRecordList();
                        common.successToast(resp.getMessage ());
                        String message=resp.getMessage();
                        new ToastMsg(LoginActivity.this).toastIconSuccess(message);
                        JsonObject respObj=new JsonObject();
                        respObj.addProperty("mobile",number);
                        respObj.addProperty("otp",recordList.getOtp());
                        respObj.addProperty("is_login","1");
                        Intent i = new Intent(LoginActivity.this, OTPActivity.class);
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