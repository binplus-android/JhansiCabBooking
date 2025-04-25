package com.cabbooking.utils;

import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.LoginResp;
import com.cabbooking.Response.OTPverificatioResp;
import com.cabbooking.Response.PickupResp;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.model.VechicleModel;
import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Apis {

    @POST("login")
    Call<LoginResp> login(@Body  JsonObject jsonObject);
    @POST("signUp")
    Call<LoginResp> signUp(@Body JsonObject jsonObject);
    @POST("verifyOtp")
    Call<OTPverificatioResp> OtpVerification(@Body  JsonObject jsonObject);
    @POST("getVehicleFare")
    Call<VechicleModel> getVehicleFare(@Body  JsonObject jsonObject);
     @POST("addTrip")
    Call<PickupResp> addTrip(@Body  JsonObject jsonObject);
  @POST("appSettings")
  Call<AppSettingModel> appSetting();



}
