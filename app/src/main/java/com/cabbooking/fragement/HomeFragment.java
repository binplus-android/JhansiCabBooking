package com.cabbooking.fragement;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabbooking.R;
import com.cabbooking.activity.MainActivity;
import com.cabbooking.adapter.DestinationAdapter;
import com.cabbooking.adapter.DestinationHomeAdapter;
import com.cabbooking.adapter.VechicleAdapter;
import com.cabbooking.databinding.FragmentHomeBinding;
import com.cabbooking.fragement.DestinationFragment;
import com.cabbooking.model.DestinationModel;
import com.cabbooking.model.VechicleModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.RecyclerTouchListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements View.OnClickListener {

    FragmentHomeBinding binding;
    ArrayList<DestinationModel> list;
    DestinationHomeAdapter adapter;
    Common common;
    ArrayList<DestinationModel> deslist;
    DestinationAdapter desadapter;
    ArrayList<VechicleModel> vechlist;
    VechicleAdapter vadapter;


    private ActivityResultLauncher<String> locationPermissionLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        allClicks();
        getDestinatioList();
        setupLocationPermissionLauncher();
        checkLocationPermission();
      setMap(false);
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
               // Toast.makeText(getContext(), "Location permission and GPS both are enabled", Toast.LENGTH_SHORT).show();
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
        deslist=new ArrayList<>();
        vechlist=new ArrayList<>();
        common = new Common(getActivity());
        list = new ArrayList<>();
        binding.recDestination.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lin_destination) {
            //common.switchFragment(new DestinationFragment());
            openBottomDestination();
        } else if (v.getId() == R.id.lin_local) {
            changeBackground(binding.linLocal, binding.linOutstation);
        } else if (v.getId() == R.id.lin_outstation) {
            changeBackground(binding.linOutstation, binding.linLocal);
        }
    }
    public void setMap(Boolean home){
        ViewGroup.LayoutParams params = binding.ivMap.getLayoutParams();

// Always keep width as MATCH_PARENT
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        if (home) {
            // Set height to 180dp
            int heightInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PT, 100,  binding.ivMap.getResources().getDisplayMetrics());
            params.height = heightInPx;
        } else {
            // Set height to MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        binding.ivMap.setLayoutParams(params);
    }

    private void openBottomDestination() {
        setMap(false);
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_destination, null);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().setDimAmount(0f);
        mBottomSheetDialog.setCancelable(true);
        FrameLayout bottomSheet = mBottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParams);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        }
        RecyclerView recDestination=bottomSheet.findViewById(R.id.rec_destination);
        recDestination.setLayoutManager(new LinearLayoutManager(getActivity()));
        getMainDestinatioList(recDestination);

        recDestination.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recDestination,
                new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mBottomSheetDialog.dismiss();
                openVechileFragment();
            
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        mBottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mBottomSheetDialog.show();


    }

    private void openVechileFragment() {
       setMap(false);
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
        mBottomSheetDialog.setContentView(R.layout.fragment_vechile);
        mBottomSheetDialog.show();
        mBottomSheetDialog.getWindow().setDimAmount(0f);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);

                behavior.setHideable(false); // prevents swipe to dismiss
                behavior.setDraggable(false); // disables dragging entirely (optional)
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // opens fully
            }
        });
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        RecyclerView recDestination=mBottomSheetDialog.findViewById(R.id.rec_list);
        Button btnBook=mBottomSheetDialog.findViewById(R.id.btn_book);
        recDestination.setLayoutManager(new LinearLayoutManager(getActivity()));
        getVechicleList(recDestination,btnBook);

    }

    private void getVechicleList(RecyclerView recDestination,Button btnBook) {
        vechlist.clear();
        vechlist.add(new VechicleModel("Mini"));
        vechlist.add(new VechicleModel("Suv"));
        vechlist.add(new VechicleModel("Auto"));
        vechlist.add(new VechicleModel("Mini"));
        vechlist.add(new VechicleModel("Suv"));
        vechlist.add(new VechicleModel("Auto"));
        btnBook.setText("Book "+vechlist.get(0).getName());
        vadapter = new VechicleAdapter(getActivity(), vechlist, new VechicleAdapter.onTouchMethod() {
            @Override
            public void onSelection(int pos) {
                btnBook.setText("Book "+vechlist.get(pos).getName());
                vadapter.notifyDataSetChanged();
            }
        });
        recDestination.setAdapter(vadapter);
    }

    private void getMainDestinatioList(RecyclerView rec) {
        deslist.clear();
        deslist.add(new DestinationModel());
        deslist.add(new DestinationModel());
        deslist.add(new DestinationModel());
        desadapter=new DestinationAdapter(getActivity(),deslist);
        rec.setAdapter(desadapter);
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void changeBackground(LinearLayout green_lay, LinearLayout shadow_lay) {
        green_lay.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
        shadow_lay.setBackgroundTintList(getResources().getColorStateList(R.color.gray_edittext));
    }
}

