package com.cabbooking.utils;

import com.cabbooking.Response.LoginResp;
import com.cabbooking.Response.OTPverificatioResp;
import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Apis {

    @POST("login")
    Call<LoginResp> login(@Body  JsonObject jsonObject);// commom for login and registration
    @POST("user_register")
    Call<JsonObject> user_register(
            @Body JsonObject jsonObject);
    @POST("OTP_VERIFICATION")
    Call<OTPverificatioResp> OtpVerification(@Body  JsonObject jsonObject);// commom for login and registration




}
