package com.cabbooking.utils;

import com.cabbooking.Response.LoginResp;
import com.cabbooking.Response.OTPverificatioResp;
import com.cabbooking.model.AppSettingModel;
import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Apis {

    @POST("login")
    Call<LoginResp> login(@Body  JsonObject jsonObject);// commom for login and registration
    @POST("signUp")
    Call<LoginResp> signUp(@Body JsonObject jsonObject);
    @POST("verifyOtp")
    Call<OTPverificatioResp> OtpVerification(@Body  JsonObject jsonObject);// commom for login and registration
  @POST("appSettings")
  Call<AppSettingModel> appSetting();



}
