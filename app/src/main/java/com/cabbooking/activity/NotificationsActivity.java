package com.cabbooking.activity;

import android.app.Activity;
import android.os.Bundle;
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
import com.cabbooking.databinding.ActivityNotificationsBinding;
import com.cabbooking.utils.Common;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {
    Common common;
    Activity activity;
    ActivityNotificationsBinding binding;
    ArrayList<NotificationResp.Datum> list;
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications);

        initview();
    }

    public void initview() {
        activity = NotificationsActivity.this;
        common = new Common(activity);
        list=new ArrayList<>();
        binding.recList.setLayoutManager (new LinearLayoutManager(activity));
        binding.incToolbar.ivBackarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        list.clear();
//        list.add(0, NotificationResp.Datum);


        setNotificationList();
    }


    public void setNotificationList() {
        if (list.size()>0) {
            binding.recList.setVisibility (View.VISIBLE);
            binding.linNodata.setVisibility (View.GONE);
            adapter = new NotificationAdapter(activity, list);
            binding.recList.setAdapter(adapter);
        } else{
            binding.recList.setVisibility (View.GONE);
            binding.linNodata.setVisibility (View.VISIBLE);
        }

        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
}