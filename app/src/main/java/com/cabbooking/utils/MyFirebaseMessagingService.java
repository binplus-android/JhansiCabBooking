package com.cabbooking.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.activity.SplashActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
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
        String title = "", message = "",type="";
        try {

            JSONObject data = new JSONObject(remoteMessage.getData());
            Log.e("datanotification",data.toString());
            JSONObject body = new JSONObject(data.getString("body"));
            title = body.getString("title");
            message = body.getString("description");
            type = body.getString("type");
            // Build and display the custom notification
            sendNotification(title, message,type);
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
    private void sendNotification(String title, String message,String type_id) {
        PendingIntent pendingIntent=null;
        if (type_id.equalsIgnoreCase("")){
            Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

         pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
    }else{
            Intent intent = new Intent(this, MapActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
            );
        }

        // Build notification using default layout
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Your app's notification icon
                .setContentTitle(title)        // Title of the notification
                .setContentText(message)       // Message content
                .setAutoCancel(true)
                .setCustomContentView(getCustomContentView(title, message, type_id)) // Set your custom view// Dismiss notification when clicked
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set notification priority
                .setContentIntent(pendingIntent); // Set the pending intent

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android Oreo and above, create the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications");
            notificationManager.createNotificationChannel(channel);
        }

        // Generate a unique notification ID
        int notificationId = (int) System.currentTimeMillis();

        notificationManager.notify(notificationId, notificationBuilder.build());
    }
    private RemoteViews getCustomContentView(String title, String body, String type) {
        DateTimeFormatter dtf = null;
        String time="";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("hh:mm a");
            LocalDateTime now = LocalDateTime.now();
            time = dtf.format(now);
        }


        RemoteViews notificationLayout = null;
        if (type.equalsIgnoreCase("")) {
            notificationLayout = new RemoteViews(getPackageName(), R.layout.push_noification_);
            notificationLayout.setTextViewText(R.id.tv_name, Html.fromHtml(title));
            notificationLayout.setTextViewText(R.id.tv_result, Html.fromHtml(body));
            notificationLayout.setTextViewText(R.id.tv_time, time);
            notificationLayout.setImageViewResource(R.id.img_time, R.drawable.logo);
        } else {
            notificationLayout =new RemoteViews(getPackageName(), R.layout.push_noification_idwise);
            //lin_call--lin_share
            notificationLayout.setTextViewText(R.id.tv_name, Html.fromHtml(title));
            notificationLayout.setTextViewText(R.id.tv_result, Html.fromHtml(body));
            notificationLayout.setTextViewText(R.id.tv_vnumber, Html.fromHtml(body));
            notificationLayout.setTextViewText(R.id.tv_vname, Html.fromHtml(body));
            notificationLayout.setTextViewText(R.id.tv_otp, Html.fromHtml(body));
            notificationLayout.setImageViewResource(R.id.img_time, R.drawable.logo);
            notificationLayout.setImageViewResource(R.id.img_v, R.drawable.logo);




            // Call Intent
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:9876543210"));
            PendingIntent callPendingIntent = PendingIntent.getActivity(
                    this, 101, callIntent, PendingIntent.FLAG_IMMUTABLE);

            // Share Intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check this: " + body);
            PendingIntent sharePendingIntent = PendingIntent.getActivity(
                    this, 102, Intent.createChooser(shareIntent, "Share via"), PendingIntent.FLAG_IMMUTABLE);

            //  Bind to layout clicks
            notificationLayout.setOnClickPendingIntent(R.id.lin_call, callPendingIntent);
            notificationLayout.setOnClickPendingIntent(R.id.lin_share, sharePendingIntent);
        }

        return notificationLayout;
    }

}
