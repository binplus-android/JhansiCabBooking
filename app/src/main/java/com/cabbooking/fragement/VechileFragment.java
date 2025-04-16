package com.cabbooking.fragement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
import com.cabbooking.activity.MainActivity;
import com.cabbooking.adapter.DestinationAdapter;
import com.cabbooking.adapter.VechicleAdapter;
import com.cabbooking.databinding.FragmentDestinationBinding;
import com.cabbooking.databinding.FragmentVechileBinding;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.RecyclerTouchListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VechileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VechileFragment extends Fragment {

    FragmentVechileBinding binding;
    Common common;
    ArrayList<VechicleModel> list;
    VechicleAdapter adapter;


    public VechileFragment() {
        // Required empty public constructor
    }


    public static VechileFragment newInstance(String param1, String param2) {
        VechileFragment fragment = new VechileFragment();
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
        //return inflater.inflate(R.layout.fragment_vechile, container, false);
        binding = FragmentVechileBinding.inflate(inflater, container, false);
        initView();
        getList();
        allClick();



        return binding.getRoot();
    }

    private void allClick() {
        binding.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.switchFragment(new PickUpFragment());
            }
        });
    }

    private void getList() {
        list.clear();
        list.add(new VechicleModel("Mini"));
        list.add(new VechicleModel("Suv"));
        list.add(new VechicleModel("Auto"));
        adapter = new VechicleAdapter(getActivity(), list, new VechicleAdapter.onTouchMethod() {
            @Override
            public void onSelection(int pos) {
                binding.btnBook.setText("Book "+list.get(pos).getName());
                adapter.notifyDataSetChanged();
            }
        });
        binding.recList.setAdapter(adapter);
    }

    public void initView() {
        common = new Common(getActivity());
        ((MainActivity) getActivity()).setTitle("");
        list = new ArrayList<>();
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}