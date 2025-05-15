package com.cabbooking.fragement;

import static com.cabbooking.activity.MapActivity.areaList;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
import com.cabbooking.activity.MainActivity;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.DestinationAdapter;
import com.cabbooking.databinding.FragmentDestinationBinding;
import com.cabbooking.model.DestinationModel;
import com.cabbooking.model.nearAreaNameModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.RecyclerTouchListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationFragment extends Fragment {
    FragmentDestinationBinding binding;
    ArrayList<DestinationModel> list;
    DestinationAdapter adapter;
    Common common;


    public DestinationFragment() {
        // Required empty public constructor
    }

    public static DestinationFragment newInstance(String param1, String param2) {
        DestinationFragment fragment = new DestinationFragment();
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
        //return inflater.inflate(R.layout.fragment_destination, container, false);
        binding = FragmentDestinationBinding.inflate(inflater, container, false);
        initView();
        getDestinatioList();



        return  binding.getRoot();
    }

    public void getDestinatioList() {
        Log.d("hjhfjy", "getDestinatioList: "+areaList.size());
       ArrayList<nearAreaNameModel>list1=areaList;
        adapter=new DestinationAdapter(getActivity(), list1, new DestinationAdapter.onTouchMethod() {
            @Override
            public void onSelection(int pos) {
                ((MapActivity)getActivity()).getDestinationLatLng(areaList.get(pos).getLat(),
                        areaList.get(pos).getLng(),areaList.get(pos).getName());
                common.switchFragment(new VechileFragment());
            }
        });
        binding.recDestination.setAdapter(adapter);


    }

    public void initView() {
        common=new Common(getActivity());
        ((MapActivity)getActivity()).setTitle("Destination");
        ((MapActivity)getActivity()).showCommonPickDestinationArea(true,true);
        list=new ArrayList<>();
        binding.recDestination.setLayoutManager(new LinearLayoutManager(getActivity()));


    }
}