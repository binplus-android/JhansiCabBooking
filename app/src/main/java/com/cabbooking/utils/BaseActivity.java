package com.cabbooking.utils;

import android.app.Dialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cabbooking.R;
import com.cabbooking.databinding.DialogNoIntenetBinding;

public abstract class BaseActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {


    private static boolean isDialogVisible = false;
    private static Dialog dialogInternet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Internet receiver register karo
        registerReceiver(
                new ConnectivityReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityReceiver.connectivityReceiverListener = this;

        boolean isConnected = ConnectivityReceiver.isConnected(this);
        onNetworkConnectionChanged(isConnected); // This will show again if needed
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            showNoInternetDialog();
        } else {
            dismissNoInternetDialog();
        }
    }
    private void dismissNoInternetDialog() {
        if (dialogInternet != null && dialogInternet.isShowing()) {
            dialogInternet.dismiss();
            isDialogVisible = false;
        }
    }


    private void showNoInternetDialog() {
        if (isDialogVisible) return;

        isDialogVisible = true;

        dialogInternet = new Dialog(this);
        dialogInternet.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogNoIntenetBinding dBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_no_intenet, null, false);
        dialogInternet.setContentView(dBinding.getRoot());
        dialogInternet.setCancelable(false); // <-- DON'T allow cancel manually
        dialogInternet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogInternet.getWindow().setGravity(Gravity.CENTER);
        dialogInternet.setCanceledOnTouchOutside(false); // <-- Important
        dialogInternet.show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Optional: unregisterReceiver here if you want
    }
}
