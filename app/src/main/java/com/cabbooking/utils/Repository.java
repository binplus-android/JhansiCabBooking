package com.cabbooking.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.cabbooking.Response.LoginResp;
import com.cabbooking.Response.OTPverificatioResp;
import com.cabbooking.model.AppSettingModel;
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
        JsonObject userDeviceDetails = new JsonObject();
        userDeviceDetails.addProperty("appId", "1");
        userDeviceDetails.addProperty("appVersion", "1");
        userDeviceDetails.addProperty("deviceId", common.getDeviceId());
        userDeviceDetails.addProperty("deviceLocation", deviceLocation);
        userDeviceDetails.addProperty("deviceManufacturer", deviceManufacturer);
        userDeviceDetails.addProperty("deviceModel", deviceModelname);
        userDeviceDetails.addProperty("fcmToken", common.getFcmToken());
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
                Log.e("repository_app_settingerror",t.toString());
                showErrorMsg(responseService, t);
            }
        });

    }



}