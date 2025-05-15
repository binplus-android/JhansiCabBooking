package com.cabbooking.fragement;

import static com.cabbooking.activity.MapActivity.areaList;
import static com.cabbooking.utils.SessionManagment.KEY_ID;
import static com.cabbooking.utils.SessionManagment.KEY_OUTSTATION_TYPE;
import static com.cabbooking.utils.SessionManagment.KEY_TYPE;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.PickupResp;
import com.cabbooking.activity.MainActivity;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.VechicleAdapter;
import com.cabbooking.databinding.FragmentPickUpBinding;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PickUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickUpFragment extends Fragment {

    FragmentPickUpBinding binding;
    Common common;
    Repository repository;
SessionManagment sessionManagment;
String  trip_type="",outstation_type="";
int vechicle_pos;
    ArrayList<VechicleModel.RecordList> vec_list;
    public PickUpFragment() {
        // Required empty public constructor
    }

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
    public void updateText(String text) {
        if (binding.tvAddress != null) {
            binding.tvAddress.setText(text);
        }
    }

    private void allClick() {
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // addTrip();
                common.popFragment();
            }
        });
    }


    public void initView() {
        sessionManagment=new SessionManagment(getActivity());
        trip_type=sessionManagment.getValue(KEY_TYPE);
        outstation_type=sessionManagment.getValue(KEY_OUTSTATION_TYPE);

        repository=new Repository(getActivity());
        common = new Common(getActivity());
        ((MapActivity) getActivity()).setTitle("");
        ((MapActivity)getActivity()).showCommonPickDestinationArea(false,false);
//        vechicle_pos=Integer.parseInt(getArguments().getString("pos"));
//        vec_list=new ArrayList<>();
//        vec_list=getArguments() != null ?
//                getArguments().getParcelableArrayList("myList") : null;
//        Log.d("ggghj", "initView: "+vec_list.get(vechicle_pos).getName());
//
//        binding.tvAddress.setText(((MapActivity) getActivity()).getPickupAddress());

    }

//    public void addTrip() {
//        String distance="10";
//
//        JsonObject object=new JsonObject();
//        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
//        object.addProperty("isOutstation",trip_type);
//        object.addProperty("isRound",outstation_type);
//        common.addLocationData(object);
//         object.addProperty("vehicleType",vec_list.get(vechicle_pos).getId());
//         object.addProperty("distance",distance);
//         object.addProperty("amount",vec_list.get(vechicle_pos).getFare()*Double.parseDouble(distance));
//        if(outstation_type.equalsIgnoreCase("1")) {
//            object.addProperty("returnDate",getArguments().getString("returnDate"));  }
//        else{
//            object.addProperty("returnDate","");
//        }
//
//        repository.addTrip(object, new ResponseService() {
//            @Override
//            public void onResponse(Object data) {
//                try {
//                    PickupResp resp = (PickupResp) data;
//                    Fragment fragment=new RideFragment();
//                    Bundle bundle=new Bundle();
//                    bundle.putParcelable("pickupResp", resp);
//                    fragment.setArguments(bundle);
//                    Log.e("addTrip ",data.toString());
//                    if (resp.getStatus()==200) {
//                        common.successToast(resp.getMessage());
//                    Log.d("datts", "onResponse: "+resp.getRecordList());
//                   common.switchFragment(fragment);
//
//                    }else{
//                        common.errorToast(resp.getError());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onServerError(String errorMsg) {
//                Log.e("errorMsg",errorMsg);
//            }
//        }, true);
//
//    }

}