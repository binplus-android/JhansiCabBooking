package com.cabbooking.fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentContactUsBinding;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.OnConfig;

import androidx.fragment.app.Fragment;

public class ContactUsFragment extends Fragment {
FragmentContactUsBinding binding;
Common common;
String email="",email_subject="Support",whatsapp_num="",whataspp_msg="Hello",mobile_num="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactUsBinding.inflate(inflater, container, false);

        initView();
        common.getAppSettingData(new OnConfig() {
            @Override
            public void getAppSettingData(AppSettingModel model) {
                email=model.getSupport_email();
                mobile_num=model.getSupport_mobile();
                binding.tvMobile.setText(mobile_num);
                whatsapp_num=model.getSupport_whatsapp();
                email_subject=model.getSupport_message();
            }
        });
        manageClicks();
        return binding.getRoot();
    }

    private void initView(){
        ((MapActivity)getActivity()).setTitle("Contact Us");
        common = new Common(getActivity());

    }

    private void manageClicks(){
        binding.linEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               common.emailIntent(email,email_subject);
            }
        });

        binding.linMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.mobileIntent(mobile_num);
            }
        });

        binding.linWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.whatsApp(whatsapp_num,whataspp_msg);
            }
        });
    }

}