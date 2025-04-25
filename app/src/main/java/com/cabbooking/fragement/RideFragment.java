package com.cabbooking.fragement;

import static com.cabbooking.utils.SessionManagment.KEY_ID;
import static com.cabbooking.utils.SessionManagment.KEY_OUTSTATION_TYPE;
import static com.cabbooking.utils.SessionManagment.KEY_TYPE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabbooking.R;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.DriverDetailResp;
import com.cabbooking.Response.PickupResp;
import com.cabbooking.Response.TripRiderResp;
import com.cabbooking.activity.MainActivity;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.activity.OTPActivity;
import com.cabbooking.adapter.RideMateAdapter;
import com.cabbooking.databinding.FragmentPickUpBinding;
import com.cabbooking.databinding.FragmentRideBinding;
import com.cabbooking.model.DestinationModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.gson.JsonObject;

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
    int tripId;
    SessionManagment sessionManagment;
    PickupResp pickupResp;
    Repository repository;
    private Handler handler;
    private Runnable apiRunnable;
    private static final long API_REFRESH_INTERVAL = 10000;

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
        getRiderStatus();
        startApiRefresh();

        return binding.getRoot();
    }

    public void getRiderStatus()
        {
            JsonObject object=new JsonObject();
            object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
            object.addProperty("tripId",tripId);
            repository.getTripRiderStatus(object, new ResponseService() {
                @Override
                public void onResponse(Object data) {
                    try {
                        TripRiderResp resp = (TripRiderResp) data;
                        Log.e("TripRiderResp ",data.toString());
                        if (resp.getStatus()==200) {
                        int tripStatus=resp.getRecordList().getTripStatus();
                       if(tripStatus==1){
                         binding.linSearch.setVisibility(View.GONE); 
                         binding.linRide.setVisibility(View.VISIBLE);
                          callGetRiderDetail(resp.getRecordList().getTripId());
                           tripId=resp.getRecordList().getTripId();
                       }
                       else{
                           binding.linSearch.setVisibility(View.VISIBLE);
                           binding.linRide.setVisibility(View.GONE);

                       }
                       
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
    private void startApiRefresh() {
        handler = new Handler();
        apiRunnable = new Runnable() {
            @Override
            public void run() {
                // Call the API
                getRiderStatus();
                // Schedule the next run
                handler.postDelayed(this, API_REFRESH_INTERVAL);
            }
        };
        handler.postDelayed(apiRunnable, API_REFRESH_INTERVAL);
    }

    public void callGetRiderDetail(int tripId) {

        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        object.addProperty("tripId",tripId);
        repository.getDriverDetail(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    DriverDetailResp resp = (DriverDetailResp) data;
                    Log.e("RiderResp ",data.toString());
                    //if (resp.getStatus()==200) {
                    list.clear();
                    list.add(new DestinationModel());
                    adapter=new RideMateAdapter("",getActivity(), list, new RideMateAdapter.onTouchMethod() {
                        @Override
                        public void onSelection(int pos) {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    binding.recList.setAdapter(adapter);
//                    }else{
//                        common.errorToast(resp.getError());
//                    }
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




    public void allClick() {
        binding.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new PaymentFragment();
                Bundle bundle=new Bundle();
                bundle.putString("tripId",String.valueOf(tripId));
                common.switchFragment(fragment);
            }
        });
        binding.btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCancleRide();
            }
        }); binding.btnBottomCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCancleRide();
            }
        });
    }

    public void callCancleRide() {
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        object.addProperty("tripId",tripId);
        repository.cancleRide(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    CommonResp resp = (CommonResp) data;
                    Log.e("rideCancle ",data.toString());
                    if (resp.getStatus()==200) {
                        common.successToast(resp.getMessage());
                        Intent intent = new Intent(getActivity(), MapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        getActivity().finish();

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


    public void initView() {
        repository=new Repository(getActivity());
        common = new Common(getActivity());
        ((MapActivity) getActivity()).setTitle("");
        ((MapActivity)getActivity()).showCommonPickDestinationArea(true,false);
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        pickupResp = getArguments().getParcelable("pickupResp");
        tripId=pickupResp.getRecordList().getId();

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