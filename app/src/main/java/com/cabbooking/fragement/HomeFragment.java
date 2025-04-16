package com.cabbooking.fragement;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cabbooking.R;
import com.cabbooking.adapter.DestinationHomeAdapter;
import com.cabbooking.databinding.FragmentHomeBinding;
import com.cabbooking.model.DestinationModel;
import com.cabbooking.utils.Common;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    FragmentHomeBinding binding;
    ArrayList<DestinationModel> list;
    DestinationHomeAdapter adapter;
    Common common;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // return inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        allClicks();
        getDestinatioList();
        return  binding.getRoot();
    }

    private void allClicks() {
        binding.linDestination.setOnClickListener(this);
        binding.linLocal.setOnClickListener(this);
        binding.linDestination.setOnClickListener(this);
    }

    private void getDestinatioList() {
        list.clear();
        list.add(new DestinationModel());
        list.add(new DestinationModel());
        list.add(new DestinationModel());
        adapter=new DestinationHomeAdapter(getActivity(),list);
        binding.recDestination.setAdapter(adapter);
    }

    public void initView() {
        common=new Common(getActivity());
        list=new ArrayList<>();
        binding.recDestination.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.lin_destination){
            common.switchFragment(new DestinationFragment());
        } else if(v.getId()==R.id.lin_local){
            changeBackground(binding.linLocal,binding.linOutstation);
        }else if(v.getId()==R.id.lin_outstation){
            changeBackground(binding.linOutstation,binding.linLocal);
        }
    }

    private void changeBackground(LinearLayout green_lay, LinearLayout shadow_lay) {
        green_lay.setBackgroundTintList(getActivity().getColorStateList(R.color.green_500));
        shadow_lay.setBackgroundTintList(getActivity().getColorStateList(R.color.gray_edittext));
    }
}