package com.cabbooking.fragement;

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
import com.cabbooking.Response.CommonResp;
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
        adapter=new RideMateAdapter("payment",getActivity(), list, new RideMateAdapter.onTouchMethod() {
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
                int selectedId = binding.rdGrp.getCheckedRadioButtonId();

                if (selectedId == -1) {
                   common.errorToast("Please select a payment method");
                } else {
                    RadioButton selectedRadio = binding.getRoot().findViewById(selectedId);
                    String selectedText = selectedRadio.getText().toString();

                    callPayment(selectedText);
                }


            }
        });
    }

    public void callPayment(String paymentTypeVal)
        {
            JsonObject object=new JsonObject();
            object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
            object.addProperty("tripId",tripId);
            object.addProperty("paymentType",paymentTypeVal);
            repository.paymentApi(object, new ResponseService() {
                @Override
                public void onResponse(Object data) {
                    try {
                        CommonResp resp = (CommonResp) data;
                        Log.e("paymentApi ",data.toString());
                        if (resp.getStatus()==200) {
                            common.successToast(resp.getMessage());
                            infoDialog();

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





    private void infoDialog()   {
        Dialog dialog = new Dialog (getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView (R.layout.dialog_booking);
        TextView tv_trip=dialog.findViewById(R.id.tv_trip);
        ImageView img_close = dialog.findViewById(R.id.img_close);
        TextView tv_call_ride,tv_location,tv_cancle,tv_returndate;
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
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager ( );
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction ( );
        fragmentTransaction.replace (R.id.main_framelayout, fragment);
        fragmentTransaction.addToBackStack (null);
        fragmentTransaction.commit ( );
    }


    public void initView() {
        repository=new Repository(getActivity());
        common = new Common(getActivity());
        ((MapActivity) getActivity()).setTitle("");
        tripId=getArguments().getString("tripId");
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
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
}