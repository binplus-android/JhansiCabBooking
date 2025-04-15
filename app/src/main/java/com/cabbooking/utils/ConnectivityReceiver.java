package com.cabbooking.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.databinding.DataBindingUtil;

import com.cabbooking.R;
import com.cabbooking.databinding.DialogNoIntenetBinding;


public class ConnectivityReceiver extends BroadcastReceiver {
    Dialog dialog;
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (!isConnected){
            noInternetDialog(context);
        }else{
            if (dialog!=null){
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        }

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) AppController.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

    public void noInternetDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogNoIntenetBinding dBinding=
                DataBindingUtil.inflate(LayoutInflater.from(context),
                        R.layout.dialog_no_intenet,null,false);
        dialog.setContentView(dBinding.getRoot());
        dialog.setCancelable(false);
        dialog.getWindow();
        //  dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        if(!((Activity) context).isFinishing()) {
            //show dialog
            dialog.show();
        }

        dialog.setCanceledOnTouchOutside(false);
    }
}