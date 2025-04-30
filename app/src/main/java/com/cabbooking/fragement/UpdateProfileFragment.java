package com.cabbooking.fragement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.activity.SignUpActivity;
import com.cabbooking.databinding.FragmentContactUsBinding;
import com.cabbooking.databinding.FragmentUpdateProfileBinding;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.ToastMsg;

public class UpdateProfileFragment extends Fragment {

    FragmentUpdateProfileBinding binding;
    String type="";
    Common common;

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

        common = new Common(getActivity());
        binding.btnSubmit.setEnabled(false);
        binding.btnSubmit.setBackgroundTintList(getResources().getColorStateList(R.color.grey_20));
        binding.btnSubmit.setAlpha(.4f);

        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnSubmit.setEnabled(true);
                binding.btnSubmit.setBackgroundTintList(getResources().getColorStateList(R.color.yellow));
                binding.btnSubmit.setAlpha(1f);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("name")){
                    if (!common.isValidName(binding.etName.getText().toString())) {
                        common.errorToast(getString(R.string.enter_valid_name));
                    }else {
                        common.successToast(getString(R.string.success));
                    }

                }else if (type.equalsIgnoreCase("mobile")){
//                    binding.tvHeading.setText("Update Mobile Number");
//                    binding.tvError.setText(getString(R.string.name_will_shown_to_driver));
//                    binding.tvPlaceholder.setText(getString(R.string.mobile_number));
                }else if (type.equalsIgnoreCase("email")){
                    if (!common.isValidEmailAddress(binding.etName.getText().toString())) {
                        common.errorToast(getString(R.string.enter_valid_email));
                    }else {
                        common.successToast(getString(R.string.success));
                    }
                }
            }
        });

        return binding.getRoot();
    }

}