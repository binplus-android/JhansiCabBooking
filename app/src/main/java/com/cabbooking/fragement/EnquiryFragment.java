package com.cabbooking.fragement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.EnquiryAdapter;
import com.cabbooking.databinding.FragmentEnquiryBinding;
import com.cabbooking.databinding.FragmentHomeBinding;
import com.cabbooking.model.EnquiryModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.SessionManagment;

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
    ArrayList<EnquiryModel>list;
    EnquiryAdapter adapter;
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
        String[] items = {"Select Reason","Reason 1", "Reason 2", "Reason 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinData.setAdapter(adapter);

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem = binding.spinData.getSelectedItem().toString();
                String message=binding.etDes.getText().toString();
                if (selectedItem.equals("Select Reason")) {
                    common.errorToast("Please select a valid reason");
                    }else if (message.equalsIgnoreCase("")) {
                    common.errorToast("Enquiry message required");
                    }
                else {
                    callSubmitApi(selectedItem,message);
                }
            }
        });
    }

    private void callSubmitApi(String selectedItem,String message) {
        common.successToast("Enquiry send succesfully");
    }

    private void getList() {
        list.clear();
        list.add(new EnquiryModel());
        list.add(new EnquiryModel());

        adapter=new EnquiryAdapter(getActivity(),list);
        binding.recList.setAdapter(adapter);
    }

    private void initView() {
        ((MapActivity)getActivity()).setTitle("Enquiry");
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        list=new ArrayList<>();
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());

    }
}