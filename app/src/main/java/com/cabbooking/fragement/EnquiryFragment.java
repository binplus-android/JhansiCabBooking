package com.cabbooking.fragement;

import static com.cabbooking.utils.SessionManagment.KEY_ID;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.cabbooking.R;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.LoginResp;
import com.cabbooking.Response.NotificationResp;
import com.cabbooking.activity.LoginActivity;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.activity.OTPActivity;
import com.cabbooking.adapter.EnquiryAdapter;
import com.cabbooking.databinding.FragmentEnquiryBinding;
import com.cabbooking.databinding.FragmentHomeBinding;
import com.cabbooking.model.EnquiryModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.cabbooking.utils.ToastMsg;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnquiryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnquiryFragment extends Fragment {
    FragmentEnquiryBinding binding;
    Common common;
    SessionManagment sessionManagment;
    ArrayList<EnquiryModel.RecordList>list;
    EnquiryAdapter adapter;
    Repository repository;
    public EnquiryFragment() {
        // Required empty public constructor
    }


    public static EnquiryFragment newInstance(String param1, String param2) {
        EnquiryFragment fragment = new EnquiryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_enquiry, container, false);
        binding = FragmentEnquiryBinding.inflate(inflater, container, false);
        initView();
        getList();
        allClick();
       return binding.getRoot();

    }

    private void allClick() {
        String[] items = {"Select","Enquiry", "Complaint"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinData.setAdapter(adapter);

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem = binding.spinData.getSelectedItem().toString();
                String message=binding.etDes.getText().toString();
                if (selectedItem.equals("Select")) {
                    common.errorToast("Please select first");
                    }else if (message.equalsIgnoreCase("")) {
                    common.errorToast("Message required");
                    }
                else {
                    callSubmitApi(selectedItem,message);
                }
            }
        });
    }

    private void callSubmitApi(String selectedItem,String message)
       {
            JsonObject object=new JsonObject();
            object.addProperty("type",selectedItem);
            object.addProperty("description",message);
           object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));

           repository.postEnquiry(object, new ResponseService() {
                @Override
                public void onResponse(Object data) {
                    try {
                        CommonResp resp = (CommonResp) data;
                        Log.e("callenquiry ",data.toString());
                        if (resp.getStatus()==200) {
                            common.successToast(resp.getMessage ());
                            getList();

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



    private void getList() {
        list.clear();
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        repository.getEnquiryList(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    EnquiryModel resp = (EnquiryModel) data;
                    Log.e("getEnquiryresp ",data.toString());
                    if (resp.getStatus()==200) {
                        list.clear();
                        list = resp.getRecordList();
                      if(list.size()>0) {
                        adapter = new EnquiryAdapter(getActivity(), list);
                        binding.recList.setAdapter(adapter);
                    }
                    }
                    else{
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

    private void initView() {
        repository=new Repository(getActivity());
        ((MapActivity)getActivity()).setTitle("Enquiry");
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        list=new ArrayList<>();
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());

    }
}