package com.cabbooking.fragement;

import static com.cabbooking.utils.SessionManagment.KEY_ID;
import static com.cabbooking.utils.SessionManagment.KEY_OUTSTATION_TYPE;
import static com.cabbooking.utils.SessionManagment.KEY_TYPE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cabbooking.R;
import com.cabbooking.Response.LoginResp;
import com.cabbooking.Response.PickupResp;
import com.cabbooking.activity.LoginActivity;
import com.cabbooking.activity.MainActivity;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.activity.OTPActivity;
import com.cabbooking.adapter.DestinationAdapter;
import com.cabbooking.adapter.VechicleAdapter;
import com.cabbooking.databinding.FragmentDestinationBinding;
import com.cabbooking.databinding.FragmentVechileBinding;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.RecyclerTouchListener;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.cabbooking.utils.ToastMsg;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.sax.SAXResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VechileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VechileFragment extends Fragment {

    FragmentVechileBinding binding;
    Common common;
    ArrayList<VechicleModel.RecordList> list;
    VechicleAdapter adapter;
    SessionManagment sessionManagment;
    String trip_type="",outstation_type="";
    String date = "";
    String time = "";
    String tv_time_value="",tv_date_value;
    String sel_date="";
    Repository repository;
    int sel_pos=0;


    public VechileFragment() {
        // Required empty public constructor
    }


    public static VechileFragment newInstance(String param1, String param2) {
        VechileFragment fragment = new VechileFragment();
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
        //return inflater.inflate(R.layout.fragment_vechile, container, false);
        binding = FragmentVechileBinding.inflate(inflater, container, false);
        initView();
        binding.tvReturndate.setText(getFormattedDate());
        binding.tvReturntime.setText(getFormattedTime());
        getList();
        allClick();
        manageTripTypeClick();
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    common.switchFragment(new HomeFragment());
                    return true;
                }
                return false;
            }
        });

        // Ensure focusable behavior
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();

        return binding.getRoot();
    }

    private void allClick() {
        binding.linRetunDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectDate();
            }
        });
        binding.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new
                addTrip();

                //old
//                String returnDateval=binding.tvReturndate.getText().toString() + " " + binding.tvReturntime.getText().toString();
//                Fragment fm=new PickUpFragment();
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList("myList", list);
//                bundle.putString("pos", String.valueOf(sel_pos));
//                bundle.putString("returnDate",returnDateval);
//                fm.setArguments(bundle);
//                common.switchFragment(fm);
            }
        });
    }
    public void addTrip() {
        String distance= String.valueOf(common.getDistanceInKm(getActivity()));
        String amount=String.format(Locale.US, "%.0f", list.get(sel_pos).getFare()*Double.parseDouble(distance));

        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        object.addProperty("isOutstation",trip_type);
        object.addProperty("isRound",outstation_type);
        common.addLocationData(object);
        object.addProperty("vehicleType",list.get(sel_pos).getId());

      //make it .3
        if(outstation_type.equalsIgnoreCase("1")) {
            String returndistance= String.valueOf(common.getReturnDistanceInKm(getActivity()));
            String returnamount=String.format(Locale.US, "%.0f", list.get(sel_pos).getFare()*Double.parseDouble(returndistance));

            String returnDateval=binding.tvReturndate.getText().toString() + " " + binding.tvReturntime.getText().toString();
            object.addProperty("returnDate",returnDateval);
            object.addProperty("distance",String.valueOf(Double.parseDouble(distance)+Double.parseDouble(returndistance)));
            object.addProperty("amount",String.valueOf(Double.parseDouble(amount)+Double.parseDouble(returnamount)));
        }
        else{
            object.addProperty("returnDate","");
            object.addProperty("distance",distance);
            object.addProperty("amount",amount);

        }

        repository.addTrip(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    PickupResp resp = (PickupResp) data;
                    Fragment fragment=new RideFragment();
                    Bundle bundle=new Bundle();
                    bundle.putParcelable("pickupResp", resp);
                    fragment.setArguments(bundle);
                    Log.e("addTrip ",data.toString());
                    if (resp.getStatus()==200) {
                        common.successToast(resp.getMessage());
                        Log.d("datts", "onResponse: "+resp.getRecordList());
                        common.switchFragment(fragment);

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
        }, true);

    }
    public static String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    public static String getFormattedTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        return timeFormat.format(new Date());
    }

    public void getList() {
             list.clear();
            JsonObject object=new JsonObject();
         object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
         object.addProperty("isOutstation",trip_type);
         object.addProperty("isRound",outstation_type);
         common.addLocationData(object);

        if(outstation_type.equalsIgnoreCase("1")) {
            object.addProperty("returnDate", binding.tvReturndate.getText().toString() + " " + binding.tvReturntime.getText().toString());
        }
        else{
            object.addProperty("returnDate","");
        }
            repository.getVechicleData(object, new ResponseService() {
                @Override
                public void onResponse(Object data) {
                    try {
                        VechicleModel resp = (VechicleModel) data;
                        Log.e("getVechicleData ",data.toString());
                        if (resp.getStatus()==200) {
                            list.clear();
                            list = resp.getRecordList();

                            binding.btnBook.setText("Book "+list.get(0).getName());
                            sel_pos=0;
                            adapter = new VechicleAdapter(outstation_type,getActivity(), list, new VechicleAdapter.onTouchMethod() {
                                @Override
                                public void onSelection(int pos) {
                                    sel_pos=pos;
                                    binding.btnBook.setText("Book "+list.get(pos).getName());
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            binding.recList.setAdapter(adapter);
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
            }, true);

        }



    public void initView() {
        repository=new Repository(getActivity());
        sessionManagment=new SessionManagment(getActivity());
        sessionManagment.setValue(KEY_OUTSTATION_TYPE,"0");
        trip_type=sessionManagment.getValue(KEY_TYPE);
        outstation_type=sessionManagment.getValue(KEY_OUTSTATION_TYPE);

        common = new Common(getActivity());
        ((MapActivity) getActivity()).setTitle("");
        ((MapActivity)getActivity()).showCommonPickDestinationArea(true,false);
        list = new ArrayList<>();
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(trip_type.equalsIgnoreCase("1")){
           binding.linOutstationData.setVisibility(View.VISIBLE);
        }
        else {
            binding.linOutstationData.setVisibility(View.GONE);
        }
    }

    public void manageTripTypeClick() {

        binding.linOneWay.setOnClickListener(v -> {
            // One-way active
            binding.linOneWay.setBackgroundResource(R.drawable.bg_outline_box_blue);
            binding.imgOneWay.setVisibility(View.VISIBLE);

            // Round-trip inactive - remove background
            binding.linRoundTrip.setBackground(null); // No background
            binding.imgRoundTrip.setVisibility(View.GONE);
            sessionManagment.setValue(KEY_OUTSTATION_TYPE,"0");
            outstation_type=sessionManagment.getValue(KEY_OUTSTATION_TYPE);
            binding.linRetunDate.setVisibility(View.GONE);
            getList();
        });

        binding.linRoundTrip.setOnClickListener(v -> {
            // Round-trip active
            binding.linRoundTrip.setBackgroundResource(R.drawable.bg_outline_box_blue);
            binding.imgRoundTrip.setVisibility(View.VISIBLE);

            // One-way inactive - remove background
            binding.linOneWay.setBackground(null); // No background
            binding.imgOneWay.setVisibility(View.GONE);
            sessionManagment.setValue(KEY_OUTSTATION_TYPE,"1");
            outstation_type=sessionManagment.getValue(KEY_OUTSTATION_TYPE);

           openSelectDate();
        });

    }

    @SuppressLint("NewApi")
    private void openSelectTime() {
        if (date.isEmpty()) {
            date = common.getCurrentDate();
        }

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_timer);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(false);

        TimePicker timee = dialog.findViewById(R.id.timee);

//        ViewGroup timePickerViewGroup = (ViewGroup) timee.getChildAt(0);
//        for (int i = 0; i < timePickerViewGroup.getChildCount(); i++) {
//            View child = timePickerViewGroup.getChildAt(i);
//            Log.d("TAG", "Child: " + child.getClass().getName());
//        }


        timee.setIs24HourView(true); // 00-23 dial

        timee.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            try {
                // Change numbers color
                int clockId = getResources().getIdentifier("radial_picker", "id", "android");
                View radialPicker = timee.findViewById(clockId);
                if (radialPicker instanceof ViewGroup) {
                    ViewGroup radialGroup = (ViewGroup) radialPicker;
                    for (int i = 0; i < radialGroup.getChildCount(); i++) {
                        View child = radialGroup.getChildAt(i);
                        if (child instanceof TextView) {
                            ((TextView) child).setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        }
                    }
                }

                // Change hour number color
                int hourTextId = getResources().getIdentifier("hour", "id", "android");
                View hourView = timee.findViewById(hourTextId);
                if (hourView instanceof TextView) {
                    ((TextView) hourView).setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                }

                // Change rotating selector/clock hand color
                int clockHandId = getResources().getIdentifier("clock_hand", "id", "android");
                View clockHand = timee.findViewById(clockHandId);
                if (clockHand != null) {
                    Drawable bg = clockHand.getBackground();
                    if (bg != null) {
                        bg.setTint(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    } else {
                        clockHand.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
                    }
                }

                //  Fallback for older/newer Android versions
                int selectorId = getResources().getIdentifier("selector", "id", "android");
                View selectorView = timee.findViewById(selectorId);
                if (selectorView != null) {
                    selectorView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        //  Hide AM/PM layout from UI
        int amPmId = Resources.getSystem().getIdentifier("ampm_layout", "id", "android");
        if (amPmId != 0) {
            View amPmView = timee.findViewById(amPmId);
            if (amPmView != null) {
                amPmView.setVisibility(View.GONE);
            }
        }

        //  Force change top time size, color, and font
        try {
            int timeHeaderId = Resources.getSystem().getIdentifier("time_header", "id", "android");
            View timeHeaderView = timee.findViewById(timeHeaderId);

            if (timeHeaderView instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) timeHeaderView;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    if (child instanceof TextView) {
                        TextView tv = (TextView) child;
                        tv.setTextSize(30); // Increase size here
                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        tv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_medium));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        Button btn_apply = dialog.findViewById(R.id.btn_apply);

        timee.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            if (date.equalsIgnoreCase(common.getCurrentDate())) {
                Calendar selected = Calendar.getInstance();
                selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selected.set(Calendar.MINUTE, minute);

                Calendar now = Calendar.getInstance();

                if (selected.before(now)) {
                    // Reset to current time if user selects past time
                    timee.setHour(now.get(Calendar.HOUR_OF_DAY));
                    timee.setMinute(now.get(Calendar.MINUTE));
                    common.errorToast("Past time is not allowed");
                }
            }
        });


        // Apply button logic
        btn_apply.setOnClickListener(v -> {
            if (date.equalsIgnoreCase("")) {
                common.errorToast("Please select date first");
            } else {
                int hour = timee.getHour();
                int minute = timee.getMinute();

                // Create Calendar to handle AM/PM
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
                String formattedTime = sdf.format(calendar.getTime());

                tv_time_value = formattedTime;
                binding.tvReturntime.setText(tv_time_value);
                binding.linRetunDate.setVisibility(View.VISIBLE);
                dialog.dismiss();

                getList();
            }
        });


        iv_close.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void openSelectDate() {
        if (time.isEmpty()){
            time = common.getCurrentTime();
        }

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_calender);

        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        Button btn_apply = dialog.findViewById(R.id.btn_apply);
        CalendarView calendarView = dialog.findViewById(R.id.calendarView);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String dayStr = day < 10 ? "0" + day : String.valueOf(day);
        String monStr = month < 10 ? "0" + month : String.valueOf(month);

        sel_date = dayStr + "-" + monStr + "-" + year;
        // Add Listener in calendar
        Log.d("selec_date", "openSelectDate: "+sel_date);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                // Apply selected date text color
//                view.setDateTextAppearance(R.style.SelectedDateTextStyle);

                // Format the selected date
                String day = "", mon = "";
                if (month < 10) {
                    mon = "0" + (month + 1);
                } else {
                    mon = String.valueOf(month + 1);
                }

                if (dayOfMonth < 10) {
                    day = "0" + dayOfMonth;
                } else {
                    day = String.valueOf(dayOfMonth);
                }

                sel_date = day + "-" + mon + "-" + year;

                // Log the selected date
                Log.e("Selected Date", sel_date);
            }
        });
        calendarView.setMinDate(System.currentTimeMillis() - 1000);
        btn_apply.setOnClickListener(v -> {
            if (sel_date.isEmpty()) {
                common.errorToast("Please select date");
            } else {
                date = common.convertDateFormat(sel_date);
                tv_date_value=(date);
                binding.tvReturndate.setText(tv_date_value);
                time = common.getCurrentTime();
                tv_time_value=common.timeConversion12hrs(time);
                binding.tvReturntime.setText(tv_time_value);
                binding.linRetunDate.setVisibility(View.VISIBLE);
                openSelectTime();
                dialog.dismiss();
            }
        });

        iv_close.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.setCancelable(false);
    }
    @Override
    public void onResume() {
        super.onResume();
        hideKeyboard();
        // ((MapActivity) getActivity()).moveToUserLocation();
    }
    private void hideKeyboard() {
        if (getActivity() != null && getView() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

}