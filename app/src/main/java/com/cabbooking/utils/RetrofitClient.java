package com.cabbooking.utils;

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



    public static final String BASE_URL = "";
    public static String ApiUserId="",ApiKey="";
    private static Retrofit retrofit;



    public static Retrofit getRetrofitInstance() {
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();

        builder.addInterceptor(new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {

                String str = ApiUserId+":"+ApiKey;
                String header = "Basic "+ Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
                Log.e("dcfvgbhj",header);
                Request request;

                request = chain.request().newBuilder()
                        .addHeader("authorization", header)
                        .addHeader("Content-Type", "application/json").
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
