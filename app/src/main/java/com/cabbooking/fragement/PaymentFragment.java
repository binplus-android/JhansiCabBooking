package com.cabbooking.fragement;

import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;
import static com.cabbooking.utils.SessionManagment.KEY_ID;
import static com.cabbooking.utils.SessionManagment.KEY_OUTSTATION_TYPE;
import static com.cabbooking.utils.SessionManagment.KEY_TYPE;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cabbooking.R;
import com.cabbooking.Response.CancleRideResp;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.PaymentResp;
import com.cabbooking.Response.TripDetailRes;
import com.cabbooking.activity.MainActivity;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.RideMateAdapter;
import com.cabbooking.databinding.FragmentPaymentBinding;
import com.cabbooking.databinding.FragmentRideBinding;
import com.cabbooking.model.DestinationModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.firebase.database.core.Repo;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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
    String trip_type="",outstation_type="",tripId="",driver_Number="999999999";
    SessionManagment sessionManagment;
    Repository repository;
    String amount_pay="0";
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

        getDetailApi(tripId);


        return binding.getRoot();
    }

    private void allClick() {
        binding.btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCancleRide();
            }
        });
        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = binding.rdGrp.getCheckedRadioButtonId();

                if (selectedId == -1) {
                   common.errorToast("Please select a payment method");
                } else {
                    RadioButton selectedRadio = binding.getRoot().findViewById(selectedId);
                    String selectedText = selectedRadio.getText().toString();
                    String paymentStatus="";
                    if(selectedText.equalsIgnoreCase("cash")){
                        paymentStatus="Success";
                    }
                    else{
                        paymentStatus="";
                    }
                    callPayment(selectedText,paymentStatus);
                }


            }
        });
    }

    public void callPayment(String paymentTypeVal,String payment_status)
        {
            JsonObject object=new JsonObject();
            object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
            object.addProperty("tripId",tripId);
            object.addProperty("paymentMode",paymentTypeVal);
            object.addProperty("amount", amount_pay);
            object.addProperty("gst","");
            object.addProperty("paymentReference","");
             object.addProperty("paymentStatus",payment_status);

            object.addProperty("signature","");
            object.addProperty("description","");
            repository.paymentApi(object, new ResponseService() {
                @Override
                public void onResponse(Object data) {
                    try {
                        PaymentResp resp = (PaymentResp) data;
                        Log.e("paymentApi ",data.toString());
//                        if (resp.getStatus()==200) {
//                            common.successToast(resp.getMessage());
                            infoDialog(resp);


//                        }else{
//                            common.errorToast(resp.getError());
//                        }
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

    public void getDetailApi(String tripId)    {
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
                         amount_pay=resp.getRecordList().getAmount();

                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getProfileImage()).
                                placeholder(R.drawable.logo).error(R.drawable.logo).into(binding.ivRimg);
                        binding.tvRidername.setText(resp.getRecordList().getName());
                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getVehicleImage()).
                                placeholder(R.drawable.logo).error(R.drawable.logo).into(binding.ivVimg);
                        binding.tvVname.setText(resp.getRecordList().getVehicleModelName());
                        if(!common.checkNullString(resp.getRecordList().getSeat()).equalsIgnoreCase("")){
                            binding.tvVdesc.setText("(" +resp.getRecordList().getVehicleColor()+" | "+resp.getRecordList().getSeat()+" Seater ) ");
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



    public void infoDialog(PaymentResp resp)
    {
        Dialog dialog = new Dialog (getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView (R.layout.dialog_booking);
        ImageView iv_loc,iv_car;
        TextView tv_ridername,tv_vnumb,tv_vname,tv_vdesc,tv_bookdate,tv_returndate,tv_otp;
        tv_ridername=dialog.findViewById(R.id.tv_ridername);
        tv_otp=dialog.findViewById(R.id.tv_otp);
        tv_bookdate=dialog.findViewById(R.id.tv_bookdate);
        tv_vnumb=dialog.findViewById(R.id.tv_vnumb);
        tv_vname=dialog.findViewById(R.id.tv_vname);
        tv_vdesc=dialog.findViewById(R.id.tv_vdesc);
        iv_loc=dialog.findViewById(R.id.iv_loc);
        iv_car=dialog.findViewById(R.id.iv_car);
        TextView tv_trip=dialog.findViewById(R.id.tv_trip);
        ImageView img_close = dialog.findViewById(R.id.img_close);
        TextView tv_call_ride,tv_location,tv_cancle;
        tv_call_ride = dialog.findViewById(R.id.tv_call_ride);
        tv_returndate = dialog.findViewById(R.id.tv_returndate);
        img_close = dialog.findViewById(R.id.img_close);
        tv_location = dialog.findViewById(R.id.tv_location);
        tv_cancle = dialog.findViewById(R.id.tv_cancle);
        img_close.setOnClickListener(v -> dialog.dismiss());



        if(trip_type.equalsIgnoreCase("1")){
            tv_trip.setVisibility(View.VISIBLE);
            if(outstation_type.equalsIgnoreCase("0")){
              tv_returndate.setVisibility(View.GONE);
               tv_trip.setText(getActivity().getString(R.string.one_way_trip));
            }
            else{
                tv_returndate.setVisibility(View.VISIBLE);
                tv_trip.setText(getActivity().getString(R.string.round_trip));
            }
        }
        else{
            tv_returndate.setVisibility(View.GONE);
            tv_trip.setVisibility(View.GONE);
        }

//        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getProfileImage()).
//                placeholder(R.drawable.logo).error(R.drawable.logo).into(iv_loc);
//        tv_ridername.setText(resp.getRecordList().getName());
//        tv_vnumb.setText(resp.getRecordList().getContactNo());
 //      tv_otp.setText(getActivity().getString(R.string.otp)+" "+"-");
//        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getVehicleImage()).
//                placeholder(R.drawable.logo).error(R.drawable.logo).into(iv_car);
//
//        tv_vname.setText(resp.getRecordList().getVehicleModelName());
//        tv_bookdate.setText(getActivity().getString(R.string.booking_date)+resp.recordList.getCreated_at());
//        if(!common.checkNullString(resp.getRecordList().getSeat()).equalsIgnoreCase("")){
//            tv_vdesc.setText("(" +resp.getRecordList().getVehicleColor()+" | "+resp.getRecordList().getSeat()+" Seater ) ");
//        }
//        else{
//            tv_vdesc.setText("(" +resp.getRecordList().getVehicleColor()+")");
//        }
//
//        tv_returndate.setText(getActivity().getString(R.string.return_date)+resp.getRecordList().getReturnDate());


        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                commonIntent();

            }
        });
        tv_call_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               callDriver();
            }
        });


        dialog.setCanceledOnTouchOutside (false);
        dialog.show ();
    }

    public void callDriver() {
        common.calling(driver_Number);
    }

    private void commonIntent() {
        Fragment fragment=new AfterPaymentDoneFragment();
        Bundle bundle=new Bundle();
        bundle.putString("tripId",tripId);
        fragment.setArguments(bundle);
        common.switchFragment(fragment);
    }


    public void initView() {
        repository=new Repository(getActivity());
        common = new Common(getActivity());
        ((MapActivity) getActivity()).setTitle("");
        tripId=getArguments().getString("tripId");
        list=new ArrayList<>();
        sessionManagment=new SessionManagment(getActivity());
        trip_type=sessionManagment.getValue(KEY_TYPE);
        outstation_type=sessionManagment.getValue(KEY_OUTSTATION_TYPE);
        if(trip_type.equalsIgnoreCase("1")){
            ((MapActivity)getActivity()).showCommonPickDestinationArea(true,false);
            if(outstation_type.equalsIgnoreCase("1")){
                binding.relReturnDate.setVisibility(View.VISIBLE);
            }
            else {
                binding.relReturnDate.setVisibility(View.GONE);
            }
        }else{
            ((MapActivity)getActivity()).showCommonPickDestinationArea(false,false);
            binding.relReturnDate.setVisibility(View.GONE);
        }


    }

    public void callCancleRide() {
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        object.addProperty("tripId",tripId);
        repository.cancleRide(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    CancleRideResp resp = (CancleRideResp) data;
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
}