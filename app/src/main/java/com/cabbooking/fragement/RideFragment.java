package com.cabbooking.fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabbooking.R;
import com.cabbooking.activity.MainActivity;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.RideMateAdapter;
import com.cabbooking.databinding.FragmentPickUpBinding;
import com.cabbooking.databinding.FragmentRideBinding;
import com.cabbooking.model.DestinationModel;
import com.cabbooking.utils.Common;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RideFragment extends Fragment {

    FragmentRideBinding binding;
    Common common;
    ArrayList<DestinationModel>list;
    RideMateAdapter adapter;

    public RideFragment() {
        // Required empty public constructor
    }


    public static RideFragment newInstance(String param1, String param2) {
        RideFragment fragment = new RideFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_ride, container, false);
        binding = FragmentRideBinding.inflate(inflater, container, false);
        initView();
        allClick();
        getList();


        return binding.getRoot();
    }

    public void getList() {
        list.clear();
        list.add(new DestinationModel());
        adapter=new RideMateAdapter(getActivity(), list, new RideMateAdapter.onTouchMethod() {
            @Override
            public void onSelection(int pos) {
                adapter.notifyDataSetChanged();
            }
        });
        binding.recList.setAdapter(adapter);
    }

    public void allClick() {
        binding.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.switchFragment(new PaymentFragment());
            }
        });
    }


    public void initView() {
        common = new Common(getActivity());
        ((MapActivity) getActivity()).setTitle("");
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        list=new ArrayList<>();

    }
}