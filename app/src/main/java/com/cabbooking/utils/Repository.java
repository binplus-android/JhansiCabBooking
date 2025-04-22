package com.cabbooking.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.LoginResp;
import com.cabbooking.Response.OTPverificatioResp;
import com.cabbooking.activity.LoginActivity;
import com.cabbooking.utils.Apis;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.LoadingBar;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.RetrofitClient;
import com.cabbooking.utils.SessionManagment;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Repository {
    private final Apis apiInterface;
    LoadingBar progressbar;
    Common common;
    Context context;
    SessionManagment sessionMangement;
    public static String SERVER_ERROR_MESSAGE = "Server Error";

    public Repository(Context context) {
        this.context = context;
       // common = new Common(context);

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
        postData.addProperty("device_id",common.getDeviceId());

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
//    public void callLogout(JsonObject postData, ResponseService responseService, boolean showProgress){
//        showHideProgressBar(showProgress);
//        apiInterface.
//                logout(postData).enqueue(new Callback<CommonResp>() {
//                    @Override
//                    public void onResponse(Call<CommonResp> call, Response<CommonResp> response) {
//                        showHideProgressBar(false);
//                        if (response.isSuccessful()){
//                            responseService.onResponse(response.body());
//                        }else {
//
//                            common.errorToast("Session Out");
//                            sessionMangement  = new SessionManagment(context);
//                            sessionMangement.logout(context);
//                            common.unSubscribeToTopic();
//                            Intent intent = new Intent(context, LoginActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                            intent.putExtra("page", "login");
//                            context.startActivity(intent);
//                            clearLoginInfo();
//                            SDKManager.sharedInstance().logout();
//                        }
//                    }
//                    @Override
//                    public void onFailure(Call<CommonResp> call, Throwable t) {
//                        showHideProgressBar(false);
//                        showErrorMsg(responseService, t);
//                    }
//                });
//    }


}