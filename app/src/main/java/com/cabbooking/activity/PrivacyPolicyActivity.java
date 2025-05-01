package com.cabbooking.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cabbooking.R;
import com.cabbooking.databinding.ActivityPrivacyPolicyBinding;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.OnConfig;
import com.google.android.gms.common.api.CommonStatusCodes;

public class PrivacyPolicyActivity extends AppCompatActivity {
    ActivityPrivacyPolicyBinding binding;
    String type="";
    Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        common=new Common(this);
        type=getIntent().getStringExtra("type");
        common.getAppSettingData(new OnConfig() {
            @Override
            public void getAppSettingData(AppSettingModel model) {
                if (type.equals("terms")){
                    binding.tvHeading.setText(getResources().getString(R.string.terms));
                    binding.tvDescription.setText(model.getTerms_conditions());

                }else {
                    binding.tvHeading.setText(getResources().getString(R.string.privacy_policy));
                    binding.tvDescription.setText(model.getPrivacy_policy());
                }
            }
        });


        binding.incToolbar.ivBackarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}