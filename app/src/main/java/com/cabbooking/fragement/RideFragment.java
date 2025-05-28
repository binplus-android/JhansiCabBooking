package com.cabbooking.fragement;

import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;
import static com.cabbooking.utils.SessionManagment.KEY_ID;
import static com.cabbooking.utils.SessionManagment.KEY_OUTSTATION_TYPE;
import static com.cabbooking.utils.SessionManagment.KEY_TYPE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabbooking.R;
import com.cabbooking.Response.CancleRideResp;
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
import com.squareup.picasso.Picasso;

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
    private boolean isFragmentVisible = true;
    private int currentStatus = 0; // 0 = keep refreshing, 1 = stop refreshing

    private static final long API_REFRESH_INTERVAL = 5000; // example 5 seconds
    private Dialog exitDialog;
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
    public boolean onBackPressed() {
        if (tripId > 0) {
            showExitCancelDialog(getActivity(), String.valueOf(tripId));
            return true;
        }
        return false;
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
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                showExitCancelDialog(getActivity(), String.valueOf(tripId));
                return false;
            }
        });

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        return binding.getRoot();
    }

    public void showExitCancelDialog(Activity activity, String tripId) {
        if (exitDialog != null && exitDialog.isShowing()) return;

        exitDialog = new Dialog(activity);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        exitDialog.getWindow().setGravity(Gravity.CENTER);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        exitDialog.setContentView(R.layout.dilalog_exit);

        TextView tvCancel = exitDialog.findViewById(R.id.tv_cancel);
        TextView tvMessage = exitDialog.findViewById(R.id.tv_message);
        Button btnYes = exitDialog.findViewById(R.id.btn_yes);
        Button btnNo = exitDialog.findViewById(R.id.btn_no);

        tvCancel.setText(getString(R.string.goback));
        tvMessage.setText(getString(R.string.goback_cancle));

        btnNo.setOnClickListener(v -> exitDialog.dismiss());
        btnYes.setOnClickListener(v -> {
            exitDialog.dismiss();
            common.callCancleDialog(activity, tripId);
        });

        exitDialog.setCanceledOnTouchOutside(false);
        exitDialog.show();
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
                            onStatusReceivedFromApi(tripStatus);
                       if(tripStatus>=2){

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
    @Override
    public void onResume() {
        super.onResume();
        isFragmentVisible = true;
        startApiRefresh();
    }
    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;
        stopApiRefresh();
    }
    private void startApiRefresh() {
        if (handler == null) {
            handler = new Handler();
        }
        if (apiRunnable == null) {
            apiRunnable = new Runnable() {
                @Override
                public void run() {
                    //if (isFragmentVisible && currentStatus == 0) { if (currentStatus>2)
                        if (isFragmentVisible) {
                        getRiderStatus();
                        handler.postDelayed(this, API_REFRESH_INTERVAL);
                    }
                }
            };
        }
        handler.postDelayed(apiRunnable, API_REFRESH_INTERVAL);
    }
    private void stopApiRefresh() {
        if (handler != null && apiRunnable != null) {
            handler.removeCallbacks(apiRunnable);
        }
    }

    // Call this inside your API response
    private void onStatusReceivedFromApi(int status) {
        currentStatus = status;
        if (currentStatus>2) {
            stopApiRefresh();
        }
    }


    public void callGetRiderDetail(int tripId) {
        list.clear();
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        object.addProperty("tripId",tripId);
        repository.getDriverDetail(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    DriverDetailResp resp = (DriverDetailResp) data;
                    Log.e("RiderResp ",data.toString());
                    if (resp.getStatus()==200) {
                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getProfileImage()).
                                placeholder(R.drawable.logo).error(R.drawable.logo).into(binding.ivRimg);
                        binding.tvRidername.setText(resp.getRecordList().getName());
                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getVehicleTypeImage()).
                                placeholder(R.drawable.logo).error(R.drawable.logo).into(binding.ivVimg);
                        binding.tvVname.setText(resp.getRecordList().getVehicleModelName());
                        binding.tvVnum.setText(resp.getRecordList().getVehicleNumber());
                        if(!common.checkNullString(String.valueOf(resp.getRecordList().getSeats())).equalsIgnoreCase("")){
                            binding.tvVdesc.setText("(" +resp.getRecordList().getVehicleColor()+" | "+String.valueOf(resp.getRecordList().getSeats())+" Seater ) ");
                        }
                       else{
                            binding.tvVdesc.setText("(" +resp.getRecordList().getVehicleColor()+")");
                        }
                        binding.tvRate.setText("Rs. "+resp.getRecordList().getAmount());
                        binding.tvReturnDate.setText(getActivity().getString(R.string.return_date)+resp.getRecordList().getReturnDate());




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




    public void allClick() {
        binding.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new PaymentFragment();
                Bundle bundle=new Bundle();
                bundle.putString("tripId",String.valueOf(tripId));
                fragment.setArguments(bundle);
                common.switchFragment(fragment);
            }
        });
        binding.btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.callCancleDialog(getActivity(),String.valueOf(tripId));
            }
        }); binding.btnBottomCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.callCancleDialog(getActivity(),String.valueOf(tripId));
            }
        });
    }





    public void initView() {
        repository=new Repository(getActivity());
        common = new Common(getActivity());
        ((MapActivity) getActivity()).setTitle("");
        ((MapActivity)getActivity()).showCommonPickDestinationArea(true,false);
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