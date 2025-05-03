package com.cabbooking.fragement;

import static com.cabbooking.utils.SessionManagment.KEY_ID;

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
import com.cabbooking.adapter.EnquiryAdapter;
import com.cabbooking.adapter.WalletHistoryAdapter;
import com.cabbooking.databinding.FragmentEnquiryBinding;
import com.cabbooking.databinding.FragmentWalletHistoryBinding;
import com.cabbooking.model.EnquiryModel;
import com.cabbooking.model.WalletHistoryModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.firebase.database.core.Repo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    ArrayList<WalletHistoryModel.RecordList>list;
    Repository repository;

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
        iniList ("referral");
        return binding.getRoot();
    }

    private void allClick() {
        binding.relReferral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callViewlineClick ( binding.vReferral,binding.vWinning);
                callCommonClick (binding.tvReferral,binding.tvWinning);
                iniList ("referral");
            }
        });
        binding.relRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callViewlineClick ( binding.vWinning,binding.vReferral);
                callCommonClick (binding.tvWinning,binding.tvReferral);
                iniList ("refund");
            }
        });
    }

    private void iniList(String type_val)
    {
            list.clear();

            JsonObject object=new JsonObject();
            object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
            object.addProperty("type",type_val);
            repository.getWalletHistory(object, new ResponseService() {
                @Override
                public void onResponse(Object data) {
                    try {
                        WalletHistoryModel resp = (WalletHistoryModel) data;
                        Log.e("WalletHistoryModel ",data.toString());
                    if (resp.getStatus()==200) {
                        binding.tvAmt.setText("Rs."+resp.getWalletAmount());
                        list.clear();
                        list = resp.getRecordList();
                        if(list.size()>0) {
                            adapter=new WalletHistoryAdapter(getActivity(),list);
                            binding.recList.setAdapter(adapter);
                        }
                    }
                    else{
                        common.errorToast(resp.getError());
                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onServerError(String errorMsg) {
                    Log.e("errorMsg",errorMsg);
                }
            }, true);

        }






    private void callCommonClick(TextView tvblack, TextView tv2) {
        tvblack.setTextColor (getActivity().getColor(R.color.colorAccent));
        tv2.setTextColor (getActivity().getColor(R.color.gray));

        // initWalletList(tvblack.getText ().toString ());
    }

    private void callViewlineClick(View vblack, View v2) {
        vblack.setBackgroundColor (getActivity().getColor(R.color.colorAccent));
        vblack.setVisibility (View.VISIBLE);
        v2.setVisibility (View.INVISIBLE);
    }

    private void initView() {
        repository=new Repository(getActivity());
        ((MapActivity)getActivity()).setTitle("Wallet History");
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        list=new ArrayList<>();
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
    }
}