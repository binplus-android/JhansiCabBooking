package com.cabbooking.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

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
import com.cabbooking.Response.TripDetailRes;
import com.cabbooking.Response.TripRiderResp;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.model.BookingHistoryModel;
import com.cabbooking.model.EnquiryModel;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.model.WalletHistoryModel;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.BuildConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Repository {
    private final Apis apiInterface;
    LoadingBar progressbar;

    Common common;
    Context context;
    SessionManagment sessionMangement;
    public String deviceManufacturer = android.os.Build.MANUFACTURER;
    public String deviceModelname = Build.MODEL;
    public static String SERVER_ERROR_MESSAGE = "Server Error";
    public String appVersion = String.valueOf(BuildConfig.VERSION_CODE);
    public String appId = "1";
    public String deviceLocation="";

    public Repository(Context context) {
        this.context = context;
     // common=new Common(context);
        apiInterface = RetrofitClient.getRetrofitInstance(context).create(Apis.class);
        progressbar = new LoadingBar(context);
    }
    private void showErrorMsg(ResponseService responseService, Throwable t) {
        responseService.onServerError(t.getMessage());
        sessionMangement = new SessionManagment(context);
        String show_error = sessionMangement.getValue("session_error_status");
        if (show_error != null) {
            if (!show_error.isEmpty() && show_error.equals("1")) {
                //common.errorToast(SERVER_ERROR_MESSAGE);
            }
        }
    }
//    private void forceToShowErrorMsg(ResponseService responseService, Throwable t) {
//        responseService.onServerError(t.getMessage());
//        common.errorToast(SERVER_ERROR_MESSAGE);
//
//    }
    private void showHideProgressBar(boolean showStatus) {
        if (showStatus) {
            progressbar.show();
        } else {
            progressbar.dismiss();
        }
    }

    public void getOtpApi(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);
        sessionMangement=new SessionManagment(context);
        JsonObject userDeviceDetails = new JsonObject();
        userDeviceDetails.addProperty("appId", "1");
        userDeviceDetails.addProperty("appVersion", "1");
        userDeviceDetails.addProperty("deviceId", common.getDeviceId());
        userDeviceDetails.addProperty("deviceLocation", deviceLocation);
        userDeviceDetails.addProperty("deviceManufacturer", deviceManufacturer);
        userDeviceDetails.addProperty("deviceModel", deviceModelname);
        userDeviceDetails.addProperty("fcmToken", sessionMangement.getToken());
        postData.add("userDeviceDetails", userDeviceDetails);
        apiInterface.OtpVerification(postData).enqueue(new Callback<OTPverificatioResp>() {
            @Override
            public void onResponse(Call<OTPverificatioResp> call,
                                   Response<OTPverificatioResp> response) {
                Log.e("repository_login", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<OTPverificatioResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });
    }

    public void callLoginApi(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.login(postData).enqueue(new Callback<LoginResp>() {
            @Override
            public void onResponse(Call<LoginResp> call, Response<LoginResp> response) {
                Log.e("repository_login", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }

    public void callSignUpApi(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);
        postData.addProperty("device_id",common.getDeviceId());

        apiInterface.signUp(postData).enqueue(new Callback<LoginResp>() {
            @Override
            public void onResponse(Call<LoginResp> call, Response<LoginResp> response) {
                Log.e("repository_login", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
    public void callAppSettingAPI(ResponseService responseService,boolean showProgress){
        showHideProgressBar(showProgress);
        apiInterface.appSetting().enqueue(new Callback<AppSettingModel>() {
            @Override
            public void onResponse(Call<AppSettingModel> call, Response<AppSettingModel> response) {
                Log.e("repository_app_setting",response.toString());
                if (response.isSuccessful()){
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                }else {
                    common.repositoryResponseCode(response.code());
                }
            }
            @Override
            public void onFailure(Call<AppSettingModel> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_eror",t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
    public void getVechicleData(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.getVehicleFare(postData).enqueue(new Callback<VechicleModel>() {
            @Override
            public void onResponse(Call<VechicleModel> call, Response<VechicleModel> response) {
                Log.e("repository_vechicle", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<VechicleModel> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
    public void getNotification(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.getNotification(postData).enqueue(new Callback<NotificationResp>() {
            @Override
            public void onResponse(Call<NotificationResp> call, Response<NotificationResp> response) {
                Log.e("repository_Notification", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<NotificationResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
    public void addTrip(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.addTrip(postData).enqueue(new Callback<PickupResp>() {
            @Override
            public void onResponse(Call<PickupResp> call, Response<PickupResp> response) {
                Log.e("repository_vechicle", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<PickupResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
    public void getTripRiderStatus(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.tripStatus(postData).enqueue(new Callback<TripRiderResp>() {
            @Override
            public void onResponse(Call<TripRiderResp> call, Response<TripRiderResp> response) {
                Log.e("repositiry_riderResp", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<TripRiderResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
    public void getDriverDetail(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.driverDetail(postData).enqueue(new Callback<DriverDetailResp>() {
            @Override
            public void onResponse(Call<DriverDetailResp> call, Response<DriverDetailResp> response) {
                Log.e("repositiry_riderResp", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<DriverDetailResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
    public void cancleRide(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.cancleRide(postData).enqueue(new Callback<CancleRideResp>() {
            @Override
            public void onResponse(Call<CancleRideResp> call, Response<CancleRideResp> response) {
                Log.e("repositiry_riderResp", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<CancleRideResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
 public void paymentApi(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.paymentApi(postData).enqueue(new Callback<PaymentResp>() {
            @Override
            public void onResponse(Call<PaymentResp> call, Response<PaymentResp> response) {
                Log.e("repositiry_payment", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<PaymentResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
public void driverLocation(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.driverLocation(postData).enqueue(new Callback<DriverLocationResp>() {
            @Override
            public void onResponse(Call<DriverLocationResp> call, Response<DriverLocationResp> response) {
                Log.e("repositiry_drilocation", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<DriverLocationResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
public void getDetailTrip(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.tripDetail(postData).enqueue(new Callback<TripDetailRes>() {
            @Override
            public void onResponse(Call<TripDetailRes> call, Response<TripDetailRes> response) {
                Log.e("repositiry_detail", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<TripDetailRes> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }

    public void postEnquiry(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.postEnquiry(postData).enqueue(new Callback<CommonResp>() {
            @Override
            public void onResponse(Call<CommonResp> call, Response<CommonResp> response) {
                Log.e("repository_enquiry", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<CommonResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
     public void feedBack(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.feedBack(postData).enqueue(new Callback<CommonResp>() {
            @Override
            public void onResponse(Call<CommonResp> call, Response<CommonResp> response) {
                Log.e("feedback", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<CommonResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }

    public void getEnquiryList(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common = new Common(context);

        apiInterface.getEnquiry(postData).enqueue(new Callback<EnquiryModel>() {
            @Override
            public void onResponse(Call<EnquiryModel> call, Response<EnquiryModel> response) {
                Log.e("repository_Notification", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<EnquiryModel> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });
    }
        public void getCurrentBooking(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.getCurrentBooking(postData).enqueue(new Callback<HomeBookingResp>() {
            @Override
            public void onResponse(Call<HomeBookingResp> call, Response<HomeBookingResp> response) {
                Log.e("curentBook", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<HomeBookingResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });
    }

    public void getWalletHistory(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.getWalletHistory(postData).enqueue(new Callback<WalletHistoryModel>() {
            @Override
            public void onResponse(Call<WalletHistoryModel> call, Response<WalletHistoryModel> response) {
                Log.e("WalletHistoryModel", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<WalletHistoryModel> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
      public void getBookingHistory(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.getBookingHistory(postData).enqueue(new Callback<BookingHistoryModel>() {
            @Override
            public void onResponse(Call<BookingHistoryModel> call, Response<BookingHistoryModel> response) {
                Log.e("getBookingHistory", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<BookingHistoryModel> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
 public void getBookingDetail(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.getBookingDetail(postData).enqueue(new Callback<BookingDetailResp>() {
            @Override
            public void onResponse(Call<BookingDetailResp> call, Response<BookingDetailResp> response) {
                Log.e("WalletHistoryModel", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<BookingDetailResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }
 public void getProfile(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.getProfileData(postData).enqueue(new Callback<ProfileDetailResp>() {
            @Override
            public void onResponse(Call<ProfileDetailResp> call, Response<ProfileDetailResp> response) {
                Log.e("profileDetails", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<ProfileDetailResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }

public void postProfile(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.postProfileData(postData).enqueue(new Callback<ProfileUpdateResp>() {
            @Override
            public void onResponse(Call<ProfileUpdateResp> call, Response<ProfileUpdateResp> response) {
                Log.e("WalletHistoryModel", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<ProfileUpdateResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }

public void postProfileImage(JsonObject postData, ResponseService responseService, boolean showProgress) {
        showHideProgressBar(showProgress);
        common=new Common(context);

        apiInterface.postProfileImage(postData).enqueue(new Callback<ProfileUpdateResp>() {
            @Override
            public void onResponse(Call<ProfileUpdateResp> call, Response<ProfileUpdateResp> response) {
                Log.e("WalletHistoryModel", response.toString());
                if (response.isSuccessful()) {
                    showHideProgressBar(false);
                    responseService.onResponse(response.body());
                } else {
                    common.repositoryResponseCode(response.code());
                }
            }

            @Override
            public void onFailure(Call<ProfileUpdateResp> call, Throwable t) {
                showHideProgressBar(false);
                Log.e("repository_login_error", t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }




}