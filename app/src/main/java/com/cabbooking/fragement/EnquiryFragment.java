package com.cabbooking.fragement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
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
       return binding.getRoot();

    }

    private void getList() {
        list.clear();
        list.add(new EnquiryModel());
        list.add(new EnquiryModel());

        adapter=new EnquiryAdapter(getActivity(),list);
        binding.recList.setAdapter(adapter);
    }

    private void initView() {
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        list=new ArrayList<>();
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());

    }
}