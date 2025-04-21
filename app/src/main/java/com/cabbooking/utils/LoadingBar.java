package com.cabbooking.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.cabbooking.R;


public class LoadingBar {

    Context context;
    Dialog dialog;

    public LoadingBar(Context context) {
        this.context = context;
try {
    dialog = new Dialog (context);
    dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
    // dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    dialog.setContentView (R.layout.progress_bar);
    dialog.setCanceledOnTouchOutside (false);

}catch(Exception e)
{e.printStackTrace ();}

    }
    public void show()

    {
        if (dialog.isShowing())
        {
            dialog.dismiss();
        }

        dialog.show();
    }

    public void dismiss()
    {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }




}