package com.cabbooking.fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentContactUsBinding;
import com.cabbooking.utils.Common;

import androidx.fragment.app.Fragment;

public class ContactUsFragment extends Fragment {
FragmentContactUsBinding binding;
Common common;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactUsBinding.inflate(inflater, container, false);

        initView();
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
//                common.emailIntent(email,email_subject);
            }
        });

        binding.linMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                common.mobileIntent(mobile_num);
            }
        });

        binding.linWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                common.whatsApp(whatsapp_num,whataspp_msg);
            }
        });
    }

}