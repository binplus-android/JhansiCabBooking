package com.cabbooking.fragement;

import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;
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
import com.cabbooking.Response.TripDetailRes;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentAfterPaymentDoneBinding;
import com.cabbooking.databinding.FragmentPaymentBinding;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.firebase.database.core.Repo;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AfterPaymentDoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfterPaymentDoneFragment extends Fragment {

    String trip_type="",outstation_type="",tripId="",driver_Number="";
    SessionManagment sessionManagment;
    FragmentAfterPaymentDoneBinding binding;
    Repository repository;
    Common common;
    private Handler handler;
    private Runnable apiRunnable;
    private boolean isFragmentVisible = false;

    private static final long API_REFRESH_INTERVAL = 5000; // example 5 seconds

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
                    TripDetailRes resp = (TripDetailRes) data;
                    Log.e("tripDetail ",data.toString());
                    if (resp.getStatus()==200) {

                        binding.tvBookinhgDate.setText(getActivity().getString(R.string.booking_date)+" "+resp.getRecordList().getCreated_at());
                        binding.tvReturnDate.setText(getActivity().getString(R.string.return_date)+" "+resp.getRecordList().getReturnDate());
                        binding.tvOtp.setText(getActivity().getString(R.string.otp)+" "+"-");
                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getProfileImage()).
                                placeholder(R.drawable.logo).error(R.drawable.logo).into(binding.ivRimg);
                        binding.tvRidername.setText(resp.getRecordList().getName());
                        binding.tvNum.setText(resp.getRecordList().getContactNo());
                        driver_Number=resp.getRecordList().getContactNo();
                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getVehicleImage()).
                                placeholder(R.drawable.logo).error(R.drawable.logo).into(binding.ivVimg);
                        binding.tvVname.setText(resp.getRecordList().getVehicleModelName());
                        if(!common.checkNullString(resp.getRecordList().getSeat()).equalsIgnoreCase("")){
                            binding.tvVdesc.setText("(" +resp.getRecordList().getVehicleColor()+" | "+resp.getRecordList().getSeat()+" Seater ) ");
                        }
                        else{
                            binding.tvVdesc.setText("(" +resp.getRecordList().getVehicleColor()+")");
                        }
                        binding.tvPrice.setText("Rs. "+resp.getRecordList().getAmount());
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

    private void allClick() {
        binding.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.calling(driver_Number);
            }
        });

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
                    if (isFragmentVisible) {
                        driverLocation();
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
//        currentStatus = status;
//        if (currentStatus == 1) {
//            stopApiRefresh();
//        }
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