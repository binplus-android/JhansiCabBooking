package com.cabbooking.fragement;
import static com.cabbooking.activity.MapActivity.areaList;
import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;
import static com.cabbooking.utils.SessionManagment.KEY_HOME_IMG1;
import static com.cabbooking.utils.SessionManagment.KEY_HOME_IMG2;
import static com.cabbooking.utils.SessionManagment.KEY_ID;
import static com.cabbooking.utils.SessionManagment.KEY_TYPE;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cabbooking.R;
import com.cabbooking.Response.HomeBookingResp;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.DestinationHomeAdapter;
import com.cabbooking.databinding.FragmentHomeBinding;
import com.cabbooking.model.DestinationModel;
import com.cabbooking.model.NearAreaNameModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements View.OnClickListener {

    FragmentHomeBinding binding;
    ArrayList<DestinationModel> list;
    DestinationHomeAdapter adapter;
    Common common;
    SessionManagment sessionManagment;
    Repository repository;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    String homrTripId="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefresh.setRefreshing(false);
                callCurrentBooking();

            }
        });
        allClicks();
        getDestinatioList();
        callCurrentBooking();

        // Set back key listener
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    showExitDialog();
                    return true;
                }
                return false;
            }
        });

        // Ensure focusable behavior
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        setSessionData();



        return binding.getRoot();
    }

    private void setSessionData() {
        if((common.checkNullString(sessionManagment.getValue(KEY_HOME_IMG1)).equalsIgnoreCase(""))&&
           (common.checkNullString(sessionManagment.getValue(KEY_HOME_IMG2)).equalsIgnoreCase(""))){
            binding.linAds.setVisibility(View.GONE);
        } else {
            binding.linAds.setVisibility(View.VISIBLE);
            if (!common.checkNullString(sessionManagment.getValue(KEY_HOME_IMG1)).equalsIgnoreCase("")) {
                binding.homeimg1.setVisibility(View.VISIBLE);
                Picasso.get().load(IMAGE_BASE_URL + sessionManagment.getValue(KEY_HOME_IMG1)).
                        placeholder(R.drawable.logo).error(R.drawable.logo).into(binding.homeimg1);
            } else {
                binding.homeimg1.setVisibility(View.GONE);
            }
            if (!common.checkNullString(sessionManagment.getValue(KEY_HOME_IMG2)).equalsIgnoreCase("")) {
                binding.homeimg2.setVisibility(View.VISIBLE);
                Picasso.get().load(IMAGE_BASE_URL + sessionManagment.getValue(KEY_HOME_IMG2)).placeholder(R.drawable.logo).
                        error(R.drawable.logo).into(binding.homeimg2);
            } else {
                binding.homeimg2.setVisibility(View.GONE);
            }
        }
    }

    private void callCurrentBooking() {
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        repository.getCurrentBooking(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    HomeBookingResp resp = (HomeBookingResp) data;
                    Log.e("homebook ",data.toString());
                    if (resp.getStatus()==200) {
                        homrTripId=String.valueOf(resp.getRecordList().get(0).getTripId());
                            binding.layBooking.setVisibility(View.VISIBLE);
                        TextView tv_vnum,tv_vname,tv_dname;
                        ImageView iv_vimg;
                        CircleImageView iv_dimg;
                        tv_vnum=binding.layBooking.findViewById(R.id.tv_vnum);
                        tv_vname=binding.layBooking.findViewById(R.id.tv_vname);
                        tv_dname=binding.layBooking.findViewById(R.id.tv_dname);
                        iv_vimg=binding.layBooking.findViewById(R.id.iv_vimg);
                        iv_dimg=binding.layBooking.findViewById(R.id.iv_dimg);

                        tv_vnum.setText(resp.getRecordList().get(0).getVehicleNumber());
                        tv_vname.setText(resp.getRecordList().get(0).getVehicleModelName());
                        tv_dname.setText(resp.getRecordList().get(0).getName());
                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().get(0).getVehicleImage()).
                                placeholder(R.drawable.logo).error(R.drawable.logo).into(iv_vimg);
                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().get(0).getProfileImage()).placeholder(R.drawable.logo).
                                error(R.drawable.logo).into(iv_dimg);


                    }else{
                       // common.errorToast(resp.getError());
                        binding.layBooking.setVisibility(View.GONE);
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



    private void allClicks() {
        binding.linDestination.setOnClickListener(this);
        binding.linLocal.setOnClickListener(this);
        binding.linOutstation.setOnClickListener(this);
        binding.layBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!homrTripId.equalsIgnoreCase("")) {
                    Fragment fragment = new AfterPaymentDoneFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("tripId", homrTripId);
                    fragment.setArguments(bundle);
                    common.switchFragment(fragment);
                }
            }
        });


    }

    private void getDestinatioList() {
        Log.d("hjhfjy", "getDestinatioList: "+areaList.size());
        ArrayList<NearAreaNameModel>list1=areaList;
        adapter = new DestinationHomeAdapter(getActivity(), list1, new DestinationHomeAdapter.onTouchMethod() {
            @Override
            public void onSelection(int pos) {
                if (areaList.size() > 1) {
                    LatLng latLng = new LatLng(areaList.get(pos).getLat(), areaList.get(pos).getLng());
                    ((MapActivity) getActivity()).getDestinationLatLng(areaList.get(pos).getLat(),
                            areaList.get(pos).getLng(), areaList.get(pos).getName(), latLng);
                    common.switchFragment(new VechileFragment());
                }
            }
        });
        binding.recDestination.setAdapter(adapter);
    }
    @Override
    public void onResume() {
        super.onResume();
        hideKeyboard();
       // ((MapActivity) getActivity()).moveToUserLocation();
    }
    public void initView() {
        repository=new Repository(getActivity());
        ((MapActivity)getActivity()).showCommonPickDestinationArea(false,false);
        sessionManagment=new SessionManagment(getActivity());
        sessionManagment.setValue(KEY_TYPE,"0");
        common = new Common(getActivity());
        list = new ArrayList<>();
        binding.recDestination.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
    private void hideKeyboard() {
        if (getActivity() != null && getView() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lin_destination) {
            common.switchFragment(new DestinationFragment());
        } else if (v.getId() == R.id.lin_local) {
            sessionManagment.setValue(KEY_TYPE,"0");
            changeBackground(binding.linLocal, binding.linOutstation);
            //commonDestination();
            commonVisibleAds();
        } else if (v.getId() == R.id.lin_outstation) {
            sessionManagment.setValue(KEY_TYPE,"1");
            changeBackground(binding.linOutstation, binding.linLocal);
           // commonDestination();
            commonVisibleAds();
        }
    }

    private void commonVisibleAds() {
        binding.cardSearch.setVisibility(View.VISIBLE);
        binding.linAds.setVisibility(View.GONE);
    }

    private void commonDestination() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                common.switchFragment(new DestinationFragment());
            }
        }, 200);
    }

    private void showExitDialog() {
        Dialog dialog;

        dialog = new Dialog (getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView (R.layout.dilalog_exit);
        Button btn_no,btn_yes;
        btn_yes=dialog.findViewById (R.id.btn_yes);
        btn_no=dialog.findViewById (R.id.btn_no);

        btn_no.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                dialog.dismiss ();
            }
        });

        btn_yes.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                dialog.dismiss ();
                getActivity().finishAffinity();
            }
        });
        dialog.setCanceledOnTouchOutside (false);
        dialog.show ();
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void changeBackground(LinearLayout green_lay, LinearLayout shadow_lay) {
        green_lay.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
        shadow_lay.setBackgroundTintList(getResources().getColorStateList(R.color.gray_edittext));
    }

//    private void openSelectDate(TextView tvDate, TextView tvTime) {
//
//        if (time.isEmpty()){
//            time = common.getCurrentTime();
//        }
//
//        Dialog dialog;
//        dialog = new Dialog (getActivity ());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow();
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setGravity(Gravity.CENTER);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable (0));
//        dialog.setContentView (R.layout.dialog_calender );
//        ImageView iv_close=dialog.findViewById (R.id.iv_close);
//        Button btn_apply=dialog.findViewById (R.id.btn_apply);
//        CalendarView calendarView = dialog.findViewById (R.id.calendarView);
//
//        // Add Listener in calendar
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//
//            // In this Listener have one method
//            // and in this method we will
//            // get the value of DAYS, MONTH, YEARS
//            public void onSelectedDayChange(
//                    @NonNull CalendarView view,
//                    int year,
//                    int month,
//                    int dayOfMonth)
//            {
//
//                // Customize the selected day text color here
//                view.setDateTextAppearance(R.style.SelectedDateTextStyle);
//
////                // Access the internal children of CalendarView and set text color
////                ViewGroup calendarViewGroup = (ViewGroup) calendarView.getChildAt(0);
////                if (calendarViewGroup != null) {
////                    for (int i = 0; i < calendarViewGroup.getChildCount(); i++) {
////                        View child = calendarViewGroup.getChildAt(i);
////                        if (child instanceof TextView) {
////                            TextView tv = (TextView) child;
////                            // Check if it's today's date or the selected one (if you can identify)
////                            tv.setTextColor(Color.WHITE); // Set the selected date text color
////                        }
////                    }
////                }
//
//
//                // Store the value of date with
//                // format in String type Variable
//                // Add 1 in month because month
//                // index is start with 0
////                                String Date="";
//
//                String day="",mon="";
////                                Date = dayOfMonth + "-"
////                                        + "0"+(month + 1) + "-" + year;
//                if (month<10){
//                    mon = "0"+(month + 1);
//                }else {
//                    mon = String.valueOf(month + 1);
//                }
//
//                if (dayOfMonth<10){
//                    day = "0"+dayOfMonth ;
//                }else {
//                    day = String.valueOf(dayOfMonth);
//                }
//
//                sel_date = day + "-"
//                        + mon + "-" + year;
//                // set this date in TextView for Display
////                                sel_date= Date;
//                Log.e("sxdcfvgb",sel_date);
//            }
//        });
//        calendarView.setMinDate(System.currentTimeMillis() - 1000);
//
//        btn_apply.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                if (sel_date.isEmpty()){
//                    common.errorToast("Please select date");
//                }else {
//                    date = common.convertDateFormat(sel_date);
//                    tvDate.setText(date);
//                    time = common.getCurrentTime();
//                    tvTime.setText(common.timeConversion12hrs(time));
//                    dialog.dismiss ( );
//                }
//            }
//        });
//        iv_close.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss ();
//            }
//        });
//
//        dialog.show ( );
//        dialog.setCancelable(false);
//    }

}

