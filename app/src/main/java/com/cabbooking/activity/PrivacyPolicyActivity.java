package com.cabbooking.activity;

import static com.cabbooking.utils.SessionManagment.KEY_HOME_IMG1;
import static com.cabbooking.utils.SessionManagment.KEY_HOME_IMG2;
import static com.cabbooking.utils.SessionManagment.KEY_PRIVACY;
import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_EMAIL;
import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_MOBILE;
import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_SUBJ;
import static com.cabbooking.utils.SessionManagment.KEY_TERMS;
import static com.cabbooking.utils.SessionManagment.KEY_WHATSPP;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cabbooking.R;
import com.cabbooking.databinding.ActivityPrivacyPolicyBinding;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.OnConfig;
import com.cabbooking.utils.SessionManagment;
import com.google.android.gms.common.api.CommonStatusCodes;

public class PrivacyPolicyActivity extends AppCompatActivity {
    ActivityPrivacyPolicyBinding binding;
    String type="";
    Common common;
    SessionManagment sessionManagment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        common=new Common(this);
        sessionManagment=new SessionManagment(this);

        type=getIntent().getStringExtra("type");
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefresh.setRefreshing(false);
                common.getAppSettingData(new OnConfig() {
                    @Override
                    public void getAppSettingData(AppSettingModel model) {
                        sessionManagment.setValue(KEY_TERMS, model.getTerms_conditions());
                        sessionManagment.setValue(KEY_PRIVACY, model.getPrivacy_policy());
                        setData();
                    }
                });
            }});
        setData();





        binding.incToolbar.ivBackarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setData() {
        try {
            if (type.equals("terms")) {
                binding.tvHeading.setText(getResources().getString(R.string.terms));
                binding.tvDescription.setText(sessionManagment.getValue(KEY_TERMS));

            } else {
                binding.tvHeading.setText(getResources().getString(R.string.privacy_policy));
                binding.tvDescription.setText(sessionManagment.getValue(KEY_PRIVACY));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}