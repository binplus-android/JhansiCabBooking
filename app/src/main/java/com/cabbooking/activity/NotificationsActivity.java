package com.cabbooking.activity;

import static com.cabbooking.utils.SessionManagment.KEY_ID;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabbooking.R;
import com.cabbooking.Response.NotificationResp;
import com.cabbooking.adapter.NotificationAdapter;
import com.cabbooking.adapter.VechicleAdapter;
import com.cabbooking.databinding.ActivityNotificationsBinding;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {
    Common common;
    Activity activity;
    ActivityNotificationsBinding binding;
    ArrayList<NotificationResp.Datum> list;
    NotificationAdapter adapter;
    Repository repository;
    SessionManagment sessionManagment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications);

        initview();
        setNotificationList();
    }

    public void initview() {
        repository=new Repository(this);
        sessionManagment=new SessionManagment(this);
        activity = NotificationsActivity.this;
        common = new Common(activity);
        list=new ArrayList<>();
        binding.recList.setLayoutManager (new LinearLayoutManager(activity));
        binding.incToolbar.tvTitle.setText("Notifications");
        binding.incToolbar.ivBackarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }


    public void setNotificationList() {
        list.clear();
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        repository.getNotification(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    NotificationResp resp = (NotificationResp) data;
                    Log.e("getNotificationResp ",data.toString());
//                    if (resp.getStatus()==200) {
//                        list.clear();
//                        list = resp.getRecordList();
//                        if (list.size()>0) {
//                            binding.recList.setVisibility (View.VISIBLE);
//                            binding.linNodata.setVisibility (View.GONE);
//                            adapter = new NotificationAdapter(activity, list);
//                            binding.recList.setAdapter(adapter);
//                        } else{
//                            binding.recList.setVisibility (View.GONE);
//                            binding.linNodata.setVisibility (View.VISIBLE);
//                        }
//
//                        if (adapter!=null){
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                    else{
//                        common.errorToast(resp.getError());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onServerError(String errorMsg) {
                Log.e("errorMsg",errorMsg);
            }
        }, true);


    }
}