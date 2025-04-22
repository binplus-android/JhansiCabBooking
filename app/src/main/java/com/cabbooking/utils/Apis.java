package com.cabbooking.utils;

import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Apis {
    @POST("user_register")
    Call<JsonObject> user_register(
            @Body JsonObject jsonObject);
    @POST("verify_otp")
    Call<JsonObject> verifyOTP(
            @Body JsonObject jsonObject);
    @POST("user_login")
    Call<JsonObject> userlogin(
            @Body JsonObject jsonObject);


}
