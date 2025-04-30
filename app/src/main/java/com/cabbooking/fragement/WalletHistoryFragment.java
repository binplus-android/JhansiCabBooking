package com.cabbooking.fragement;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.WalletHistoryAdapter;
import com.cabbooking.databinding.FragmentEnquiryBinding;
import com.cabbooking.databinding.FragmentWalletHistoryBinding;
import com.cabbooking.model.WalletHistoryModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.SessionManagment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletHistoryFragment extends Fragment {
    FragmentWalletHistoryBinding binding;
    Common common;
    SessionManagment sessionManagment;
    WalletHistoryAdapter adapter;
    ArrayList<WalletHistoryModel>list;

    public WalletHistoryFragment() {
        // Required empty public constructor
    }

   
    public static WalletHistoryFragment newInstance(String param1, String param2) {
        WalletHistoryFragment fragment = new WalletHistoryFragment();
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
//        return inflater.inflate(R.layout.fragment_wallet_history, container, false);
        binding = FragmentWalletHistoryBinding.inflate(inflater, container, false);
        initView();
        allClick();
        callViewlineClick ( binding.vReferral,binding.vWinning);
        callCommonClick (binding.tvReferral,binding.tvWinning);
        iniList ("0");
        return binding.getRoot();
    }

    private void allClick() {
        binding.relReferral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callViewlineClick ( binding.vReferral,binding.vWinning);
                callCommonClick (binding.tvReferral,binding.tvWinning);
                iniList ("0");
            }
        });
        binding.relRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callViewlineClick ( binding.vWinning,binding.vReferral);
                callCommonClick (binding.tvWinning,binding.tvReferral);
                iniList ("1");
            }
        });
    }

    private void iniList(String number)  {
        list.clear();
        if(number.equalsIgnoreCase("0")) {
            list.add(new WalletHistoryModel("Referral by User"));
            list.add(new WalletHistoryModel("Referral by User"));
            list.add(new WalletHistoryModel("Referral by User"));
        }
        else{
            list.add(new WalletHistoryModel("Refund by booking"));
            list.add(new WalletHistoryModel("Refund by booking"));
            list.add(new WalletHistoryModel("Refund by booking"));
        }
        adapter=new WalletHistoryAdapter(getActivity(),list);
        binding.recList.setAdapter(adapter);

    }


    private void callCommonClick(TextView tvblack, TextView tv2) {
        tvblack.setTextColor (getActivity().getColor(R.color.colorAccent));
        tv2.setTextColor (getActivity().getColor(R.color.gray));

        // initWalletList(tvblack.getText ().toString ());
    }

    private void callViewlineClick(View vblack, View v2) {
        vblack.setBackgroundColor (getActivity().getColor(R.color.colorAccent));
        vblack.setVisibility (View.VISIBLE);
        v2.setVisibility (View.GONE);
    }

    private void initView() {
        ((MapActivity)getActivity()).setTitle("Wallet History");
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        list=new ArrayList<>();
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
    }
}