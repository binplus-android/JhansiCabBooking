package com.cabbooking.fragement;

import static com.cabbooking.utils.SessionManagment.KEY_ID;
import static com.cabbooking.utils.SessionManagment.KEY_OUTSTATION_TYPE;
import static com.cabbooking.utils.SessionManagment.KEY_TYPE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentAfterPaymentDoneBinding;
import com.cabbooking.databinding.FragmentPaymentBinding;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.firebase.database.core.Repo;
import com.google.gson.JsonObject;

import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AfterPaymentDoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfterPaymentDoneFragment extends Fragment {

    String trip_type="",outstation_type="",tripId="";
    SessionManagment sessionManagment;
    FragmentAfterPaymentDoneBinding binding;
    Repository repository;
    Common common;
    private Runnable apiRunnable;
    private Handler handler;
    private static final long API_REFRESH_INTERVAL = 10000;

    public AfterPaymentDoneFragment() {
        // Required empty public constructor
    }

    public static AfterPaymentDoneFragment newInstance(String param1, String param2) {
        AfterPaymentDoneFragment fragment = new AfterPaymentDoneFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        binding = FragmentAfterPaymentDoneBinding.inflate(inflater, container, false);
        initView();
        driverLocation();
        startApiRefresh();
        getDetailApi();
        allClick();

        return binding.getRoot();
    }

    public void getDetailApi()    {
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        object.addProperty("tripId",tripId);
        repository.getDetailTrip(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    CommonResp resp = (CommonResp) data;
                    Log.e("tripDetail ",data.toString());
                    if (resp.getStatus()==200) {
                        common.successToast(resp.getMessage());


                    }else{
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
        }, false);

    }

    private void allClick() {
    }

    private void initView() {
        ((MapActivity)getActivity()).showCommonPickDestinationArea(true,false);
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
        repository=new Repository(getActivity());
        trip_type=getArguments().getString("tripId");
        trip_type=sessionManagment.getValue(KEY_TYPE);
        outstation_type=sessionManagment.getValue(KEY_OUTSTATION_TYPE);
        if(trip_type.equalsIgnoreCase("1")){
            binding.tvTripType.setVisibility(View.VISIBLE);
            if(outstation_type.equalsIgnoreCase("0")){
                binding.tvTripType.setText(getActivity().getString(R.string.one_way_trip));
                binding.tvReturnDate.setVisibility(View.GONE);
            }
            else {
                binding.tvTripType.setText(getActivity().getString(R.string.round_trip));
                binding.tvReturnDate.setVisibility(View.VISIBLE);
            }

        }else{
            binding.tvReturnDate.setVisibility(View.GONE);
            binding.tvTripType.setVisibility(View.GONE);
        }
    }
    private void startApiRefresh() {
        apiRunnable = new Runnable() {
            @Override
            public void run() {
                // Call the API
                driverLocation();
                // Schedule the next run
                handler.postDelayed(this, API_REFRESH_INTERVAL);
            }
        };
        handler.postDelayed(apiRunnable, API_REFRESH_INTERVAL);
    }
    public void driverLocation()
    {
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        object.addProperty("tripId",tripId);
        repository.driverLocation(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    CommonResp resp = (CommonResp) data;
                    Log.e("driverLocation ",data.toString());
                    if (resp.getStatus()==200) {
                        common.successToast(resp.getMessage());


                    }else{
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
        }, false);

    }

}