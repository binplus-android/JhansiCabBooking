package com.cabbooking.fragement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.activity.MainActivity;
import com.cabbooking.databinding.FragmentPickUpBinding;
import com.cabbooking.utils.Common;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PickUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickUpFragment extends Fragment {

    FragmentPickUpBinding binding;
    Common common;

    public PickUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PickUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PickUpFragment newInstance(String param1, String param2) {
        PickUpFragment fragment = new PickUpFragment();
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
       // return inflater.inflate(R.layout.fragment_pick_up, container, false);
        binding = FragmentPickUpBinding.inflate(inflater, container, false);
        initView();
        allClick();


        return binding.getRoot();
    }

    private void allClick() {
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.switchFragment(new RideFragment());
            }
        });
    }


    public void initView() {
        common = new Common(getActivity());
        ((MainActivity) getActivity()).setTitle("");

    }

}