package com.cabbooking.utils;

import static android.content.ContentValues.TAG;
import static com.cabbooking.utils.SessionManagment.KEY_ID;
import android.location.Location;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabbooking.R;
import com.cabbooking.Response.CancleRideResp;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.ProfileDetailResp;
import com.cabbooking.activity.LoginActivity;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.DialogNoIntenetBinding;
import com.cabbooking.interfaces.SuccessCallBack;
import com.cabbooking.interfaces.WalletCallBack;
import com.cabbooking.model.AppSettingModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.Locale;
import java.util.Map;

public class Common {
    Context context;
    ToastMsg toastMsg;
    public static LatLng currenLocation;
    SessionManagment sessionManagment;
    Repository repository;
     String deviceId = "", fcmToken = "";;


    public Common(Context context) {
        this.context = context;
        toastMsg = new ToastMsg(context);
        sessionManagment=new SessionManagment(context);
        repository = new Repository(context);
    }


    // Function to get distance in km
    public float getDistanceInKm(Activity activity) {
        Location pickupLocation = new Location(String.valueOf(activity));
        pickupLocation.setLatitude(((MapActivity)context).getPickupLat());
        pickupLocation.setLongitude(((MapActivity)context).getPickupLng());

        Location destinationLocation = new Location(String.valueOf(activity));
        destinationLocation.setLatitude(((MapActivity) context).getDestinationLat());
        destinationLocation.setLongitude( ((MapActivity) context).getDestinationLng());

        float distanceInMeters = pickupLocation.distanceTo(destinationLocation);
        float distanceInKm = distanceInMeters / 1000; // Convert to km

        return distanceInKm;
    }

    public void addLocationData(JsonObject object) {
        object.addProperty("pickupLat", ((MapActivity)context).getPickupLat());
        object.addProperty("pickupLng", ((MapActivity) context).getPickupLng());
        object.addProperty("pickup", ((MapActivity) context).getPickupAddress());
        object.addProperty("destinationLat", ((MapActivity) context).getDestinationLat());
        object.addProperty("destinationLng", ((MapActivity) context).getDestinationLng());
        object.addProperty("destination", ((MapActivity) context).getDestionationAddress());;
    }
    public void shareLink(String link) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
    public void repositoryResponseCode(int code){
        switch (code) {
            case 401:
                // Handle unauthorized access
                commonTokenExpiredLogout((Activity) context);
                break;
        }
    }
    public String getDeviceId(){
        try {
            deviceId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }catch (Exception e){
            e.printStackTrace();
        }
        return deviceId;
    }
//    public String getFcmToken(){
//        try {
//            FirebaseMessaging.getInstance().getToken()
//                    .addOnCompleteListener(new OnCompleteListener<String>() {
//                        @Override
//                        public void onComplete(@NonNull Task<String> task) {
//                            if (!task.isSuccessful()) {
//                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                                return;
//                            }
//                            if (task!=null) {
//                                if (task.getResult()!=null)
//                                    fcmToken = task.getResult();
//                                }
//
//
//                        }
//                    });
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return fcmToken;
//    }

    public void commonTokenExpiredLogout(Activity activity){
        unSubscribeToTopic();
        sessionManagment.logout(activity);
    }
    public void callCancleRide(String reason,Activity activity,String userId,String tripId,Dialog dialog) {
        JsonObject object=new JsonObject();
        object.addProperty("userId",userId);
        object.addProperty("tripId",tripId);
        object.addProperty("cancelReason","My Reason here");
        repository.cancleRide(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {

                    CancleRideResp resp = (CancleRideResp) data;
                    Log.e("rideCancle ",data.toString());
                    if (resp.getStatus()==200) {
                        dialog.dismiss();
                        successToast(resp.getMessage());
                        Intent intent = new Intent(activity ,MapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        activity.startActivity(intent);
                        activity.finish();

                    }else{
                        errorToast(resp.getError());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onServerError(String errorMsg) {
                Log.e("errorMsg",errorMsg);
            }
        }, false);

    }
    public String changeDateFormate(String inputTime) {
        // First, create input format
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Then, create output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);

        try {
            // Parse input date string
            Date date = inputFormat.parse(inputTime);

            // Format to desired output and return it
            return outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return empty string if parsing fails
        }
    }

    public void callCancleDialog(Activity activity,String tripId){
        sessionManagment=new SessionManagment(context);
        Dialog dialog;

        dialog = new Dialog (activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView (R.layout.dialog_cancle_confirm);
        Button btn_yes;
        ImageView iv_close;
        btn_yes=dialog.findViewById (R.id.btn_yes);
        iv_close=dialog.findViewById (R.id.iv_close);

        EditText et_reason=dialog.findViewById(R.id.et_reason);


        iv_close.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                dialog.dismiss ();
            }
        });


        btn_yes.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                String reason=et_reason.getText().toString();
                if (reason.equalsIgnoreCase("")) {
                    errorToast("Reason required");
                } else {
//                dialog.dismiss();
                    callCancleRide(reason, activity, sessionManagment.getUserDetails().get(KEY_ID), tripId, dialog);
                }
            }
        });
        dialog.setCanceledOnTouchOutside (false);
        dialog.show ();

    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public String checkNullString(String value) {
        String str = "";
        if (value == null || value.isEmpty ( ) || value.equals ("")) {
            str = "";
        } else {
            str = value;
        }
        return str;
    }

    public boolean isValidMobileNumber(String mobileNumber) {
        String pattern = "^[6-9][0-9]{9}$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(mobileNumber);
        return m.matches();
    }

    public void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager ( );
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction ( );
        fragmentTransaction.replace (R.id.main_framelayout, fragment);
        fragmentTransaction.addToBackStack (null);
        fragmentTransaction.commit ( );

    }

    public void popFragment() {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    public void calling(String phoneNumber){
            Uri dialUri = Uri.parse("tel:" + phoneNumber);
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, dialUri);
            context.startActivity(dialIntent);

    }

    public boolean isValidName(String name){
        if (name.isEmpty ( )) {
           // errorToast ( context.getString(R.string.name_required));
           return false;

        } else if (name.trim().contains ("[0-9]")) {
           // errorToast (context.getString(R.string.number_not_allow));
            return false;

        } else {
           return true;
        }
    }
    public void noInternetDialog() {
        try {


            Dialog dialogInternet = new Dialog (context);
            dialogInternet.requestWindowFeature (Window.FEATURE_NO_TITLE);
            DialogNoIntenetBinding dBinding =
                    DataBindingUtil.inflate (LayoutInflater.from (context),
                            R.layout.dialog_no_intenet, null, false);
            dialogInternet.setContentView (dBinding.getRoot());
            dialogInternet.setCancelable (true);
            dialogInternet.getWindow ( );

            //  dialogInternet.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogInternet.getWindow ( ).setBackgroundDrawable (new ColorDrawable(Color.TRANSPARENT));
            dialogInternet.getWindow ( ).setGravity (Gravity.CENTER);
            TextView tv_no_internet=dialogInternet.findViewById(R.id.tv_no_internet);
            Button btn_ok=dialogInternet.findViewById(R.id.btn_ok);
//            btn_ok.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialogInternet.dismiss();
//                }
//            });


//            else{
            dialogInternet.show ( );
            // }

            if(ConnectivityReceiver.isConnected()){
                if(dialogInternet.isShowing()){
                    dialogInternet.dismiss();}
            }

            dialogInternet.setCanceledOnTouchOutside (true);
        }catch (Exception e){
            e.printStackTrace ();
        }

    }

    public void errorToast(String msg) {
        if (msg == null || msg.isEmpty()) {
            return;
        }

        toastMsg.toastIconError(msg);
    }

    public void setMap(Boolean is_search,boolean is_map,int size,FrameLayout img,LinearLayout lin_search  ){

        ViewGroup.LayoutParams params = img.getLayoutParams();

// Always keep width as MATCH_PARENT
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PT, size,  img.getResources().getDisplayMetrics());
        params.height = heightInPx;
//            // Set height to MATCH_PARENT
//            params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        if(is_search){
            lin_search.setVisibility(View.VISIBLE);
        }else{
            lin_search.setVisibility(View.GONE);
        }if(is_map){
            img.setVisibility(View.VISIBLE);
        }else{
            img.setVisibility(View.GONE);
        }

        img.setLayoutParams(params);
    }
    public void successToast(String msg) {
        if (msg == null || msg.isEmpty()) {
            return;
        }

        toastMsg.toastIconSuccess(msg);
    }

    public String getCurrentDate(){
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        Log.e("dcfgh", String.valueOf(currentDate));

        return String.valueOf(currentDate);
    }

    public String getCurrentTime(){
        String formattedFutureTime="";
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            LocalTime currentTime = LocalTime.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//            formattedTime = currentTime.format(formatter);
//        }


        // Get current time
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalTime currentTime = LocalTime.now();

            // Add 30 minutes to the current time
            LocalTime futureTime = currentTime.plusMinutes(30);

            // Format the times (optional)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedCurrentTime = currentTime.format(formatter);
            formattedFutureTime = futureTime.format(formatter);

            System.out.println("Current Time: " + formattedCurrentTime);
            System.out.println("Time after adding 30 minutes: " + formattedFutureTime);
        }

        return formattedFutureTime;
    }

    public String timeConversion12hrs(String time){
//      covert 24 hrs format to 12 hrs
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
            final Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("hh:mm a", Locale.US).format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
            return time;
        }
    }

    public String convertDateFormat(String myDate){
        String inputDateStr = myDate;

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = inputFormat.parse(inputDateStr);
            String outputDateStr = outputFormat.format(date);

            return outputDateStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return myDate;
        }
    }

    public void dialogScrollOff(BottomSheetDialog mBottomSheetDialog) {
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog dialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);

                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                behavior.setHideable(false);

                // 🔐 Ultimate lock: Block collapse/dismiss even on scroll
                behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING ||
                                newState == BottomSheetBehavior.STATE_SETTLING ||
                                newState == BottomSheetBehavior.STATE_COLLAPSED ||
                                newState == BottomSheetBehavior.STATE_HIDDEN) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        // no-op
                    }
                });
            }
            mBottomSheetDialog.setCanceledOnTouchOutside(false);
            mBottomSheetDialog.setCancelable(true);

        });
    }

    public void unSubscribeToTopic(){
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("jhansi_cab")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Subscribed";
                            if (!task.isSuccessful()) {
                                msg = "Subscribe failed";
                            }
                            Log.d(TAG, msg);
//                        Toast.makeText(SplashActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  JsonObject  getCommonAddressPost(){
        JsonObject object=new JsonObject();

        JsonObject pickupObject = new JsonObject();
        pickupObject.addProperty("lat", ((MapActivity) context).getPickupLat());
        pickupObject.addProperty("lng", ((MapActivity)context).getPickupLng());
        pickupObject.addProperty("address", ((MapActivity) context).getPickupAddress());

        object.add("pickup", pickupObject);

        JsonObject destinationObject = new JsonObject();
        destinationObject.addProperty("lat", ((MapActivity) context).getDestinationLat());
        destinationObject.addProperty("lng", ((MapActivity) context).getDestinationLng());
        destinationObject.addProperty("address", ((MapActivity) context).getDestionationAddress());
        object.add("destination", destinationObject);
        return object;


    }


    public void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("jhansi_cab")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d(TAG, msg);
//                        Toast.makeText(SplashActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getAppSettingData(OnConfig listner){
        repository.callAppSettingAPI( new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    AppSettingModel resp = (AppSettingModel) data;
                    Log.e("indexApi", "onResponse: "+resp );
                    listner.getAppSettingData(resp);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onServerError(String errorMsg) {
                Log.e("errorMsg",errorMsg);
            }
        }, false);

    }

    public void fetchAndDrawRoute(Activity activity, GoogleMap mMap, String pickupLat, String pickupLng, String destLat, String destLng){
        String origin = "origin=" + pickupLat + "," + pickupLng;
        String destination = "destination=" + destLat + "," + destLng;
        String key = "key="+activity.getString(R.string.google_maps_key);

        String url = "https://maps.googleapis.com/maps/api/directions/json?" + origin + "&" + destination + "&" + key;


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    try {
                        Log.e("DirectionsAPI", "Response: " + responseData);
                        JSONObject json = new JSONObject(responseData);
                        JSONArray routes = json.getJSONArray("routes");
                        JSONObject route = routes.getJSONObject(0);
                        JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                        String points = overviewPolyline.getString("points");

                        List<LatLng> decodedPath = decodePolyline(points);

                        activity.runOnUiThread(() -> {
                            PolylineOptions options = new PolylineOptions().addAll(decodedPath).color(Color.BLUE).width(10);
                            mMap.addPolyline(options);
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((lat / 1E5), (lng / 1E5));
            poly.add(p);
        }

        return poly;
    }

    public void mobileIntent(String phoneNumber){
        Uri dialUri = Uri.parse("tel:" + phoneNumber);
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, dialUri);
        context.startActivity(dialIntent);
    }

    public void emailIntent(String emailAddress,String subject){
        //       Uri emailUri = Uri.parse("mailto:" + emailAddress);
//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, emailUri);
//        context.startActivity(emailIntent);

        String encodedSubject = Uri.encode(subject);

// Create the mailto URI with subject and body
        Uri emailUri = Uri.parse("mailto:" + emailAddress +
                "?subject=" + encodedSubject);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, emailUri);
        context.startActivity(emailIntent);
    }

    public void whatsApp(String number, String message){
        PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "whatsapp://send?phone="+ number +"&text=" + URLEncoder.encode(message, "UTF-8");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i);

            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public  String convertToAmPm(String time24) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date date = inputFormat.parse(time24);
            return outputFormat.format(date).toUpperCase();
        } catch (ParseException e) {
            e.printStackTrace();
            return time24;
        }
    }

    public String convertToBase64String(Uri uri, Context context) {
        String convertedString = null;
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);
            byte[] bytes = getBytes(in);

            Log.e("fgvbhjnk",uri.toString());

            // Check if image size exceeds 2 MB
//            if (bytes.length > 2 * 1024 * 1024) { // 2 MB = 2 * 1024 * 1024 bytes
//                errorToast(context.getString(R.string.image_size_exceeds));
//                return null; // Stop further processing
//            } else {
                Log.e("Image Validation", "Image size is within limit. Compressing...");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Compress image
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream); // 50% quality
                bytes = outputStream.toByteArray();

                Log.e("base64_data", "Compressed bytes size = " + bytes.length);
                convertedString = Base64.encodeToString(bytes, Base64.DEFAULT);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("error", "Error converting image: " + e.toString());
        }
        return convertedString;
    }

    private void shareReferralLink(String userCode) {
        String referralLink = "https://jhansicab.anshuwap.com/referal?code=" + userCode;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Join and earn a bonus! Use my referral link: " + referralLink);
        context.startActivity(Intent.createChooser(intent, "Share via"));
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public Bitmap handleImageRotation(Uri imageUri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                ExifInterface exifInterface = new ExifInterface(inputStream);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return rotateImage(originalBitmap, 90);
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return rotateImage(originalBitmap, 180);
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return rotateImage(originalBitmap, 270);
                    case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                        return flipImage(originalBitmap, true, false);
                    case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                        return flipImage(originalBitmap, false, true);
                    default:
                        return originalBitmap; // No rotation needed
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if there was an error
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Bitmap flipImage(Bitmap source, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public String convertBitmapToBase64(Bitmap bitmap) {
        try {
            // Create a ByteArrayOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Compress the bitmap to PNG format (you can also use JPEG with compression quality)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            // Get the byte array from the ByteArrayOutputStream
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // Convert byte array to Base64 string
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void generateToken(){
        getFirebaseNotificationToken(new SuccessCallBack() {
            @Override
            public void onSuccess(String token) {
                String UUID = "";
                UUID = token ;
               sessionManagment.addToken(UUID);
                Log.e("mytokenn",UUID);
            }
        });
    }

    public void getFirebaseNotificationToken(SuccessCallBack successCallBack){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        successCallBack.onSuccess(token);

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TAG", token);
//                        Toast.makeText(SplashActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public  void getWalletAmount(Context context, WalletCallBack callback) {
        // Create the request object for profile if needed
        SessionManagment sessionManagment=new SessionManagment(context);
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        Repository repository = new Repository(context); // or use singleton if needed
        repository.getProfile(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    ProfileDetailResp resp = (ProfileDetailResp) data;
                    if (resp.getStatus() == 200) {
                        int walletAmount = resp.getRecordList().getWalletAmount();
                        callback.onWalletAmountReceived(walletAmount);
                    } else {
                        callback.onError(resp.getError());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError("Parsing error");
                }
            }

            @Override
            public void onServerError(String errorMsg) {
                callback.onError(errorMsg);
            }
        }, false);
    }
}


