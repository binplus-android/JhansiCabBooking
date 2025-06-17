package com.cabbooking.utils;

import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.activity.SplashActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TOPIC_GLOBAL = "global";
    private static String CHANNEL_ID = "jhansi_cab_booking_123";
    private static final String CHANNEL_NAME = "jhansiCabBooking";
    private static final String TAG = "MyFirebaseMsgingService";
    public static  String rec_Token="";

    @Override
    public void onNewToken(String s) {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        rec_Token=refreshedToken.toString();
        // now subscribe to `global` topic to receive app wide notifications
        // IMPORTANT: subscribe to topic here also!
        FirebaseMessaging.getInstance().subscribeToTopic("jhansi_cab");

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String refreshedToken) {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
// Message data payload: {body={"type":1,"title":"","description":"Test notification"}}

        Log.e("dxcfvghbnj","wsdegrtyju");
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "sdxfcgvbh: " + remoteMessage.getData().toString());
        String title = "", message = "";
        try {

            JSONObject data = new JSONObject(remoteMessage.getData());
            Log.e("datanotification",data.toString());
            JSONObject body = new JSONObject(data.getString("body"));
            title = body.getString("title");
            message = body.getString("description");

            // Build and display the custom notification
            sendNotification(title, message,body);
        }catch (Exception e){
            e.printStackTrace();
        }

//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            Map<String, String> data = remoteMessage.getData();
//            handleData(data);
//        } else if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            handleNotification(remoteMessage.getNotification());
//        }// Check if message contains a notification payload.
    }
    private void sendNotification(String title, String message, JSONObject body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder;

        if (message.isEmpty()) {

            RemoteViews customView = new RemoteViews(getPackageName(), R.layout.push_noification_idwise);

            String imageUrl = null;
            try {
                imageUrl = IMAGE_BASE_URL + body.getString("driverProfileImage").toString();
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                // Set all text fields
                                try {
                                    customView.setTextViewText(R.id.tv_name, body.getString("title").toString());
                                    customView.setTextViewText(R.id.tv_description, body.getString("vehicleModelName").toString()+
                                            " OTP-"+body.getString("pickupOtp").toString());
                                    customView.setTextViewText(R.id.tv_vnumber, body.getString("vehicleNumber").toString());
                                    customView.setTextViewText(R.id.tv_vname, body.getString("vehicleModelName").toString());
                                    customView.setTextViewText(R.id.tv_otp, " OTP-"+body.getString("pickupOtp").toString());
                                    customView.setImageViewBitmap(R.id.img_v, resource);


                                    //  Bind to layout clicks
                                    // Prepare intents and build notification (same as your code)
                                    String channelId = CHANNEL_ID;
                                    int notificationId = 1234;

                                    Intent answerIntent = new Intent(getApplicationContext(), MapActivity.class);
                                    answerIntent.putExtra("page_type", "call");
                                    answerIntent.putExtra("body", body.toString());
                                    answerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    PendingIntent answerPendingIntent = PendingIntent.getActivity(getApplicationContext(), 101, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                                    Intent declineIntent = new Intent(getApplicationContext(), MapActivity.class);
                                    declineIntent.putExtra("page_type", "share");
                                    declineIntent.putExtra("body", body.toString());
                                    declineIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    PendingIntent declinePendingIntent = PendingIntent.getActivity(getApplicationContext(), 102, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                                            .setSmallIcon(R.drawable.logo)
                                            .setContentTitle(title)
                                            .setContentText(body.toString())
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setCustomContentView(customView)
                                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                            .addAction(R.drawable.ic_call, "Call", answerPendingIntent)
                                            .addAction(R.drawable.ic_delete, "Share", declinePendingIntent)
                                            .setAutoCancel(true);
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    notificationManager.notify(notificationId, builder.build());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }


                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Optional: you can set a placeholder bitmap
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                Bitmap defaultProfileImage = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                                customView.setImageViewBitmap(R.id.img_v, defaultProfileImage);
                            }


                        });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }




            // 👇 Use custom layout if description is empty





        }
        else {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
            );
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);
            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }
}
