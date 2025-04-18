package com.cabbooking.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabbooking.R;
import com.cabbooking.databinding.DialogNoIntenetBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Common {
    Context context;
    ToastMsg toastMsg;
    public Common(Context context) {
        this.context = context;
        toastMsg = new ToastMsg(context);
        //sessionManagment=new SessionManagment(context);
    }
    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
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
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            final Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("KK:mm aa").format(dateObj);
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
}
