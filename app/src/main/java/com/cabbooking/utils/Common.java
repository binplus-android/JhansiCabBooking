package com.cabbooking.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.cabbooking.R;
import com.cabbooking.databinding.DialogNoIntenetBinding;

public class Common {
    Context context;
    ToastMsg toastMsg;
    public Common(Context context) {
        this.context = context;
        toastMsg = new ToastMsg(context);
        //sessionManagment=new SessionManagment(context);
    }

    public boolean isValidMobileNumber(String mobileNumber) {
        String pattern = "^[6-9][0-9]{9}$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(mobileNumber);
        return m.matches();
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
}
