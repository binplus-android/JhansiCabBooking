package com.cabbooking.fragement;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.cabbooking.R;
import com.cabbooking.activity.MainActivity;
import com.cabbooking.adapter.RideMateAdapter;
import com.cabbooking.databinding.FragmentPaymentBinding;
import com.cabbooking.databinding.FragmentRideBinding;
import com.cabbooking.model.DestinationModel;
import com.cabbooking.utils.Common;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PaymentFragment extends Fragment {
    FragmentPaymentBinding binding;
    Common common;
    ArrayList<DestinationModel>list;
    RideMateAdapter adapter;
    public static PaymentFragment newInstance(String param1, String param2) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PaymentFragment() {
        // Required empty public constructor
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
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
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

    private void allClick() {
        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              infoDialog();
            }
        });
    }

    private void infoDialog()   {
        Dialog dialog = new Dialog (getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView (R.layout.dialog_booking);
        dialog.setCanceledOnTouchOutside (false);
        dialog.show ();
    }


    public void initView() {
        common = new Common(getActivity());
        ((MainActivity) getActivity()).setTitle("");
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        list=new ArrayList<>();

    }
}