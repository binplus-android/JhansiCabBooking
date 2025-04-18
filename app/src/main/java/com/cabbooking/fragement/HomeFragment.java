package com.cabbooking.fragement;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabbooking.R;
import com.cabbooking.adapter.DestinationHomeAdapter;
import com.cabbooking.databinding.FragmentHomeBinding;
import com.cabbooking.model.DestinationModel;
import com.cabbooking.utils.Common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment implements View.OnClickListener {

    FragmentHomeBinding binding;
    ArrayList<DestinationModel> list;
    DestinationHomeAdapter adapter;
    Common common;
    String date = "";
    String time = "";
    String sel_date="";

    private ActivityResultLauncher<String> locationPermissionLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        allClicks();
        getDestinatioList();
        setupLocationPermissionLauncher();
        checkLocationPermission();

        binding.tvTime.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                openSelectTime(binding.tvTime);            }
        });

        binding.tvDate.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                openSelectDate(binding.tvDate, binding.tvTime);
            }
        });

        return binding.getRoot();
    }

    private void setupLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if (isLocationEnabled()) {
                            Toast.makeText(getContext(), "Location is enabled", Toast.LENGTH_SHORT).show();
                            // TODO: Start using location here
                        } else {
                            showEnableLocationDialog();
                        }
                    } else {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            openAppSettings();
                        } else {
                            Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            showCustomLocationDialog();
        } else {
            if (isLocationEnabled()) {
                // Location services are on
                Toast.makeText(getContext(), "Location permission and GPS both are enabled", Toast.LENGTH_SHORT).show();
                // TODO: Start using location here
            } else {
                showEnableLocationDialog();
            }
        }
    }

    private void showCustomLocationDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_permission);
        dialog.setCanceledOnTouchOutside(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        Button btn_no = dialog.findViewById(R.id.btn_no);
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView tv_des = dialog.findViewById(R.id.tv_message);

        tv_des.setText("Allow \"Google Maps\" to access \nyour location while you use the app?");
        btn_yes.setText(getString(R.string.allow));
        btn_no.setText(getString(R.string.do_not_allow));

        btn_no.setOnClickListener(v -> dialog.dismiss());
        btn_yes.setOnClickListener(v -> {
            dialog.dismiss();
            requestLocationPermission();
        });

        dialog.show();

        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.space_5) * 2;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int finalWidth = screenWidth - horizontalMargin;

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(finalWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void showEnableLocationDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_permission);
        dialog.setCanceledOnTouchOutside(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        Button btn_no = dialog.findViewById(R.id.btn_no);
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView tv_des = dialog.findViewById(R.id.tv_message);

        tv_des.setText("Please enable your device location (GPS) to continue.");
        btn_yes.setText("Turn On");
        btn_no.setText(getString(R.string.do_not_allow));

        btn_no.setOnClickListener(v -> dialog.dismiss());
        btn_yes.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        });

        dialog.show();

        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.space_5) * 2;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int finalWidth = screenWidth - horizontalMargin;

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(finalWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null &&
                (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            Toast.makeText(getContext(), "Permission granted (legacy device)", Toast.LENGTH_SHORT).show();
            if (!isLocationEnabled()) {
                showEnableLocationDialog();
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void allClicks() {
        binding.linDestination.setOnClickListener(this);
        binding.linLocal.setOnClickListener(this);
        binding.linOutstation.setOnClickListener(this);
    }

    private void getDestinatioList() {
        list.clear();
        list.add(new DestinationModel());
        list.add(new DestinationModel());
        list.add(new DestinationModel());
        adapter = new DestinationHomeAdapter(getActivity(), list);
        binding.recDestination.setAdapter(adapter);
    }

    public void initView() {
        common = new Common(getActivity());
        list = new ArrayList<>();
        binding.recDestination.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lin_destination) {
            common.switchFragment(new DestinationFragment());
        } else if (v.getId() == R.id.lin_local) {
            changeBackground(binding.linLocal, binding.linOutstation);
        } else if (v.getId() == R.id.lin_outstation) {
            changeBackground(binding.linOutstation, binding.linLocal);
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void changeBackground(LinearLayout green_lay, LinearLayout shadow_lay) {
        green_lay.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
        shadow_lay.setBackgroundTintList(getResources().getColorStateList(R.color.gray_edittext));
    }

    @SuppressLint("NewApi")
    private void openSelectTime(TextView tvTime) {
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

                // Convert to 12hr format (without AM/PM)
                String formattedHour = String.format("%02d", (hour % 12 == 0) ? 12 : hour % 12);
                String formattedMinute = String.format("%02d", minute);
                String time = formattedHour + ":" + formattedMinute + ":00";

                tvTime.setText(common.timeConversion12hrs(time));
                dialog.dismiss();
            }
        });

        iv_close.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void openSelectDate(TextView tvDate, TextView tvTime) {
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

        // Add Listener in calendar
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                // Apply selected date text color
                view.setDateTextAppearance(R.style.SelectedDateTextStyle);

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
                tvDate.setText(date);
                time = common.getCurrentTime();
                tvTime.setText(common.timeConversion12hrs(time));
                dialog.dismiss();
            }
        });

        iv_close.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.setCancelable(false);
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
