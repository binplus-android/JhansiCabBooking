package com.cabbooking.fragement;

import static com.cabbooking.utils.SessionManagment.KEY_OUTSTATION_TYPE;
import static com.cabbooking.utils.SessionManagment.KEY_TYPE;

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
import com.cabbooking.utils.SessionManagment;

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
    String trip_type="",outstation_type="";
    SessionManagment sessionManagment;

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
        adapter=new RideMateAdapter("",getActivity(), list, new RideMateAdapter.onTouchMethod() {
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
        sessionManagment = new SessionManagment(getActivity());
        trip_type=sessionManagment.getValue(KEY_TYPE);
        outstation_type=sessionManagment.getValue(KEY_OUTSTATION_TYPE);
       if(trip_type.equalsIgnoreCase("0")){
            binding.relReturnDate.setVisibility(View.GONE);
        }
        else {
            if(outstation_type.equalsIgnoreCase("1")){
                binding.relReturnDate.setVisibility(View.VISIBLE);
            }
            else {
                binding.relReturnDate.setVisibility(View.GONE);
            }

        }

    }
}