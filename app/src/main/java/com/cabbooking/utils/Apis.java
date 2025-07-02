package com.cabbooking.utils;

import com.cabbooking.Response.BookingDetailResp;
import com.cabbooking.Response.CancleRideResp;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.DriverDetailResp;
import com.cabbooking.Response.DriverLocationResp;
import com.cabbooking.Response.HomeBookingResp;
import com.cabbooking.Response.LoginResp;
import com.cabbooking.Response.NotificationResp;
import com.cabbooking.Response.OTPverificatioResp;
import com.cabbooking.Response.PaymentResp;
import com.cabbooking.Response.PickupResp;
import com.cabbooking.Response.ProfileDetailResp;
import com.cabbooking.Response.ProfileUpdateResp;
import com.cabbooking.Response.ServiceLOcationResp;
import com.cabbooking.Response.TripDetailRes;
import com.cabbooking.Response.TripRiderResp;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.model.BookingHistoryModel;
import com.cabbooking.model.EnquiryModel;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.model.WalletHistoryModel;
import com.google.gson.JsonObject;
import okhttp3.ResponseBody;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

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

    @POST("getNotifications")
    Call<NotificationResp> getNotification(@Body  JsonObject jsonObject);
    @POST("getCurrentTrips")
    Call<HomeBookingResp> getCurrentBooking(@Body  JsonObject jsonObject);
    @POST("getEnquiry")
    Call<EnquiryModel> getEnquiry(@Body  JsonObject jsonObject);
    @POST("getWalletHistory")
    Call<WalletHistoryModel> getWalletHistory(@Body  JsonObject jsonObject);
    @POST("getBookingHistory")
    Call<BookingHistoryModel> getBookingHistory(@Body  JsonObject jsonObject);
    @POST("bookingHistoryDetail")
    Call<BookingDetailResp> getBookingDetail(@Body  JsonObject jsonObject);
    @POST("getProfile")
    Call<ProfileDetailResp> getProfileData(@Body  JsonObject jsonObject);
    @POST("updateProfile")
    Call<ProfileUpdateResp> postProfileData(@Body  JsonObject jsonObject);
    @POST("updateProfile")
    Call<ProfileUpdateResp> postProfileImage(@Body  JsonObject jsonObject);
    @POST("addEnquiry")
    Call<CommonResp> postEnquiry(@Body  JsonObject jsonObject);
    @POST("updateFeedback")
    Call<CommonResp> feedBack(@Body  JsonObject jsonObject);
    @POST("getServiceLocations")
    Call<ServiceLOcationResp> serviceLocation(@Body  JsonObject jsonObject);

    @GET("printTripInvoice/{id}")
    Call<ResponseBody> downloadInvoice(@Path("id") String tripId);

}
