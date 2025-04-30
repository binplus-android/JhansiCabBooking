package com.cabbooking.fragement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentContactUsBinding;
import com.cabbooking.databinding.FragmentProfileBinding;
import com.cabbooking.utils.Common;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    Common common;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        initView();
        manageClicks();
        return binding.getRoot();
    }

    private void initView(){
        ((MapActivity)getActivity()).setTitle("Profile");
        common = new Common(getActivity());

    }

    private void manageClicks(){
        binding.linName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                fragment = new UpdateProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type","name");
                fragment.setArguments(bundle);

                common.switchFragment(fragment);
            }
        });

        binding.linEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment;
                fragment = new UpdateProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type","email");
                fragment.setArguments(bundle);

                common.switchFragment(fragment);
            }
        });

    }
}