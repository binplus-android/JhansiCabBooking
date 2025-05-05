package com.cabbooking.fragement;

import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_EMAIL;
import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_MOBILE;
import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_SUBJ;
import static com.cabbooking.utils.SessionManagment.KEY_WHATSPP;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentContactUsBinding;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.OnConfig;
import com.cabbooking.utils.SessionManagment;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ContactUsFragment extends Fragment {
    FragmentContactUsBinding binding;
    Common common;
    String email = "", email_subject = "Support", whatsapp_num = "", whataspp_msg = "Hello", mobile_num = "";
    SessionManagment sessionManagment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactUsBinding.inflate(inflater, container, false);

        initView();

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefresh.setRefreshing(false);
                common.getAppSettingData(new OnConfig() {
                    @Override
                    public void getAppSettingData(AppSettingModel model) {
                        sessionManagment.setValue(KEY_SUPPORT_EMAIL,model.getSupport_email());
                        sessionManagment.setValue(KEY_SUPPORT_MOBILE,model.getSupport_mobile());
                        sessionManagment.setValue(KEY_WHATSPP,model.getSupport_whatsapp());
                        sessionManagment.setValue(KEY_SUPPORT_SUBJ,model.getSupport_message());
                        setData();
                    }
                });
            }
        });
        setData();
        manageClicks();
        return binding.getRoot();
    }

    private void setData() {
        email = sessionManagment.getValue(SessionManagment.KEY_SUPPORT_EMAIL);
        mobile_num = sessionManagment.getValue(SessionManagment.KEY_SUPPORT_MOBILE);
        binding.tvMobile.setText(mobile_num);
        whatsapp_num = sessionManagment.getValue(SessionManagment.KEY_WHATSPP);
        email_subject = common.checkNullString(sessionManagment.getValue(SessionManagment.KEY_SUPPORT_SUBJ));
    }

    private void initView() {
        sessionManagment = new SessionManagment(getActivity());
        ((MapActivity) getActivity()).setTitle("Contact Us");
        common = new Common(getActivity());



    }

    private void manageClicks() {
        binding.linEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.emailIntent(email, email_subject);
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
                common.whatsApp(whatsapp_num, whataspp_msg);
            }
        });
    }

}