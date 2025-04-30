package com.cabbooking.fragement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentContactUsBinding;
import com.cabbooking.databinding.FragmentUpdateProfileBinding;

public class UpdateProfileFragment extends Fragment {

    FragmentUpdateProfileBinding binding;
    String type="";

    public UpdateProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false);

        ((MapActivity)getActivity()).setTitle("Update Profile");

        type = getArguments().getString("type");
        if (type.equalsIgnoreCase("name")){
            binding.tvHeading.setText("Update name");
            binding.tvError.setText(getString(R.string.name_will_shown_to_driver));
            binding.tvPlaceholder.setText(getString(R.string.name));

        }else if (type.equalsIgnoreCase("mobile")){
            binding.tvHeading.setText("Update Mobile Number");
            binding.tvError.setText(getString(R.string.name_will_shown_to_driver));
            binding.tvPlaceholder.setText(getString(R.string.mobile_number));
        }else if (type.equalsIgnoreCase("email")){
            binding.tvHeading.setText("Update Email ID");
            binding.tvError.setVisibility(View.GONE);
            binding.tvPlaceholder.setText(getString(R.string.email_id));
        }

        return binding.getRoot();
    }

}