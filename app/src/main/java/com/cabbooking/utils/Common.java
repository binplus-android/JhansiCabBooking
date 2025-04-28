package com.cabbooking.utils;

import static android.content.ContentValues.TAG;
import static com.cabbooking.utils.SessionManagment.KEY_ID;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
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
import com.cabbooking.activity.LoginActivity;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.DialogNoIntenetBinding;
import com.cabbooking.model.AppSettingModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    public void repositoryResponseCode(int code){
        switch (code) {
            case 401:
                // Handle unauthorized access
                commonTokenExpiredLogout(new Activity());
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
    public String getFcmToken(){
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            if (task!=null) {
                                if (task.getResult()!=null)
                                    fcmToken = task.getResult();
                                }


                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
        return fcmToken;
    }

    public void commonTokenExpiredLogout(Activity activity){
        unSubscribeToTopic();
        sessionManagment.logout(activity);
    }
    public void callCancleRide(Activity activity,String userId,String tripId,Dialog dialog) {
        JsonObject object=new JsonObject();
        object.addProperty("userId",userId);
        object.addProperty("tripId",tripId);
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
        Button btn_no,btn_yes;
        btn_yes=dialog.findViewById (R.id.btn_yes);
        btn_no=dialog.findViewById (R.id.btn_no);

        btn_no.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                dialog.dismiss ();
            }
        });

        btn_yes.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();
                callCancleRide(activity,sessionManagment.getUserDetails().get(KEY_ID),tripId,dialog);
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
            FirebaseMessaging.getInstance().unsubscribeFromTopic("user")
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
        FirebaseMessaging.getInstance().subscribeToTopic("user")
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

}
