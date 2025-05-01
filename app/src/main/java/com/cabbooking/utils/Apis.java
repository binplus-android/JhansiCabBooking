package com.cabbooking.utils;

import com.cabbooking.Response.BookingDetailResp;
import com.cabbooking.Response.CancleRideResp;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.DriverDetailResp;
import com.cabbooking.Response.DriverLocationResp;
import com.cabbooking.Response.LoginResp;
import com.cabbooking.Response.NotificationResp;
import com.cabbooking.Response.OTPverificatioResp;
import com.cabbooking.Response.PaymentResp;
import com.cabbooking.Response.PickupResp;
import com.cabbooking.Response.ProfileDetailResp;
import com.cabbooking.Response.TripDetailRes;
import com.cabbooking.Response.TripRiderResp;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.model.BookingHistoryModel;
import com.cabbooking.model.EnquiryModel;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.model.WalletHistoryModel;
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
    @POST("tripStatus")
    Call<TripRiderResp> tripStatus(@Body  JsonObject jsonObject);
    @POST("driverDetail")
    Call<DriverDetailResp> driverDetail(@Body  JsonObject jsonObject);
    @POST("cancelTrip")
    Call<CancleRideResp> cancleRide(@Body  JsonObject jsonObject);

    @POST("tripDetail")
    Call<TripDetailRes> tripDetail(@Body  JsonObject jsonObject);

    @POST("payment")
    Call<PaymentResp> paymentApi(@Body  JsonObject jsonObject);

    @POST("driverLiveLocation")
    Call<DriverLocationResp> driverLocation(@Body  JsonObject jsonObject);
  @POST("appSettings")
  Call<AppSettingModel> appSetting();
  //dummy
    @POST("getVehicleFare")
    Call<NotificationResp> getNotification(@Body  JsonObject jsonObject);
    @POST("getVehicleFare")
    Call<EnquiryModel> getEnquiry(@Body  JsonObject jsonObject);
    @POST("getVehicleFare")
    Call<WalletHistoryModel> getWalletHistory(@Body  JsonObject jsonObject);
    @POST("getVehicleFare")
    Call<BookingHistoryModel> getBookingHistory(@Body  JsonObject jsonObject);
    @POST("getVehicleFare")
    Call<BookingDetailResp> getBookingDetail(@Body  JsonObject jsonObject);
    @POST("getVehicleFare")
    Call<ProfileDetailResp> getProfileData(@Body  JsonObject jsonObject);
    @POST("getVehicleFare")
    Call<CommonResp> postProfileData(@Body  JsonObject jsonObject);
    @POST("getVehicleFare")
    Call<CommonResp> postProfileImage(@Body  JsonObject jsonObject);
    @POST("getVehicleFare")
    Call<CommonResp> postEnquiry(@Body  JsonObject jsonObject);



}
