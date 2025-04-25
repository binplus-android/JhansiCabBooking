package com.cabbooking.utils;

import static com.cabbooking.utils.SessionManagment.KEY_TOKEN;
import static com.cabbooking.utils.SessionManagment.KEY_TOKEN_TYPE;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {



    public static final String BASE_URL="https://jhansicab.anshuwap.com/api/user/";
    public static final String IMAGE_BASE_URL="https://jhansicab.anshuwap.com/";
    public static String ApiUserId="",ApiKey="";
    private static Retrofit retrofit;



    public static Retrofit getRetrofitInstance(Context context) {
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();

        builder.addInterceptor(new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                String tokenType = ((new SessionManagment(context).getUserDetails().get(KEY_TOKEN_TYPE) == "") || (new SessionManagment(context).getUserDetails().get(KEY_TOKEN_TYPE) == null) ? "Bearer" : new SessionManagment(context).getUserDetails().get(KEY_TOKEN_TYPE));
                String token = ((new SessionManagment(context).getUserDetails().get(KEY_TOKEN) == "") || (new SessionManagment(context).getUserDetails().get(KEY_TOKEN) == null) ? "invalid token" : new SessionManagment(context).getUserDetails().get(KEY_TOKEN));

                Request request;

                request = chain.request().newBuilder()
                        .addHeader("Authorization", tokenType + " " + token)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json").
                        addHeader("Accept-Language", "en").build();


                return chain.proceed(request);
            }
        });

        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        retrofit = null;

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())//GsonConverterFactory
                    .client(builder.build())
                    .build();
        }
        return retrofit;
    }
}
