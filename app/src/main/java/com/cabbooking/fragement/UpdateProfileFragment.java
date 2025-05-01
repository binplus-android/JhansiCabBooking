package com.cabbooking.fragement;

import static com.cabbooking.utils.SessionManagment.KEY_ID;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.LoginResp;
import com.cabbooking.activity.LoginActivity;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.activity.OTPActivity;
import com.cabbooking.activity.SignUpActivity;
import com.cabbooking.databinding.FragmentContactUsBinding;
import com.cabbooking.databinding.FragmentUpdateProfileBinding;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.cabbooking.utils.ToastMsg;
import com.google.gson.JsonObject;

public class UpdateProfileFragment extends Fragment {

    FragmentUpdateProfileBinding binding;
    String type="";
    Common common;
    SessionManagment sessionManagment;
    Repository repository;
    public UpdateProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false);

        ((MapActivity)getActivity()).setTitle("Update Profile");
        repository=new Repository(getActivity());
        sessionManagment=new SessionManagment(getActivity());
        common = new Common(getActivity());

        type = getArguments().getString("type");
        if (type.equalsIgnoreCase("name")){
            binding.etName.setText(getArguments().getString("name"));
            binding.tvHeading.setText("Update name");
            binding.tvError.setText(getString(R.string.name_will_shown_to_driver));
            binding.tvPlaceholder.setText(getString(R.string.name));

        }else if (type.equalsIgnoreCase("mobile")){
            binding.tvHeading.setText("Update Mobile Number");
            binding.tvError.setText(getString(R.string.name_will_shown_to_driver));
            binding.tvPlaceholder.setText(getString(R.string.mobile_number));
        }else if (type.equalsIgnoreCase("email")){
            binding.etName.setText(getArguments().getString("email"));
            binding.tvHeading.setText("Update Email ID");
            binding.tvError.setVisibility(View.GONE);
            binding.tvPlaceholder.setText(getString(R.string.email_id));
        }


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
                        callPostApi("name",binding.etName.getText().toString());
                    }

                }else if (type.equalsIgnoreCase("mobile")){
//                    binding.tvHeading.setText("Update Mobile Number");
//                    binding.tvError.setText(getString(R.string.name_will_shown_to_driver));
//                    binding.tvPlaceholder.setText(getString(R.string.mobile_number));
                }else if (type.equalsIgnoreCase("email")){
                    if (!common.isValidEmailAddress(binding.etName.getText().toString())) {
                        common.errorToast(getString(R.string.enter_valid_email));
                    }else {
                        callPostApi("email",binding.etName.getText().toString());
                    }
                }
            }
        });

        return binding.getRoot();
    }

    private void callPostApi(String key,String value) {
            JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
            object.addProperty(key,value);
            repository.postProfile(object, new ResponseService() {
                @Override
                public void onResponse(Object data) {
                    try {
                        CommonResp resp = (CommonResp) data;
                        Log.e("CommonResp ",data.toString());
                        if (resp.getStatus()==200) {
                            common.successToast(resp.getMessage ());

                        }else{
                            common.errorToast(resp.getError());
                        }
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