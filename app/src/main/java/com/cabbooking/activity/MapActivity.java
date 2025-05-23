package com.cabbooking.activity;

import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;
import static com.cabbooking.utils.SessionManagment.KEY_HOME_IMG1;
import static com.cabbooking.utils.SessionManagment.KEY_HOME_IMG2;
import static com.cabbooking.utils.SessionManagment.KEY_PRIVACY;
import static com.cabbooking.utils.SessionManagment.KEY_SHARE_LINK;
import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_EMAIL;
import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_MOBILE;
import static com.cabbooking.utils.SessionManagment.KEY_SUPPORT_SUBJ;
import static com.cabbooking.utils.SessionManagment.KEY_TERMS;
import static com.cabbooking.utils.SessionManagment.KEY_USER_IMAGE;
import static com.cabbooking.utils.SessionManagment.KEY_WHATSPP;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabbooking.R;
import com.cabbooking.adapter.MenuAdapter;
import com.cabbooking.databinding.ActivityMapBinding;
import com.cabbooking.fragement.BookingHistoryFragment;
import com.cabbooking.fragement.ContactUsFragment;
import com.cabbooking.fragement.DestinationFragment;
import com.cabbooking.fragement.EnquiryFragment;
import com.cabbooking.fragement.HomeFragment;
import com.cabbooking.fragement.PickUpAddressFragment;
import com.cabbooking.fragement.PickUpFragment;
import com.cabbooking.fragement.ProfileFragment;
import com.cabbooking.fragement.WalletHistoryFragment;
import com.cabbooking.interfaces.AddressCallback;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.model.MenuModel;
import com.cabbooking.model.NearAreaNameModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Location;
import com.cabbooking.utils.OnConfig;
import com.cabbooking.utils.SessionManagment;

import com.cabbooking.utils.locationListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener {
    Common common;
    ActivityMapBinding binding;
    public static ArrayList<NearAreaNameModel> areaList = new ArrayList<>();
    SessionManagment sessionManagment;
    public GoogleApiClient mGoogleApiClient;
    public GoogleMap mMap;
    Location location;
    public Marker riderMarket, destinationMarker;
    public Double currentLat, currentLng;
    public String mPlaceLocation, mPlaceDestination;
    public SupportMapFragment mapFragment;
    public DatabaseReference driversAvailable;
    public ArrayList<Marker> driverMarkers = new ArrayList<>();
    public boolean pickupPlacesSelected = false;
    public int radius = 1, distance = 1; // km
    public static final int LIMIT = 3;
    public String URL_BASE_API_PLACES = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
    ActionBarDrawerToggle toggle;
    TextView tvpick, tvDestination;
    String sharelink = "", share_msg = "";
    String appLink = "";
    Integer ver_code, is_forced = 0, version_code;
    Double pickupLat = 0.0, destinationLat = 0.0;
    Double pickupLng = 0.0, destinationLng = 0.0;
    String pickAddres = "", destinationAddress = "";
    public static boolean isAddressFetched = false; // Add this field
    ArrayList<MenuModel> mlist;
    MenuAdapter menuAdapter;
    public ActivityResultLauncher<String> locationPermissionLauncher;
    Activity activity;
    LatLng pickLatLng, destinationLatLng;
    String apiKey;
    private Polyline currentPolyline;
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LatLng pickUpFinalLat;
    private boolean pickupPlacesAllReadySelected = false;
    String currentFragment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map);

        initView();
        apiKey = getString(R.string.google_maps_key);
        storeDataSession();
        setImage(sessionManagment.getUserDetails().get(KEY_USER_IMAGE));
        getMenuList();
        verifyGoogleAccount();

        // Setup launchers
        setupNotificationPermissionLauncher();
        setupLocationPermissionLauncher();
        checkNotificationPermission();

        mapCode();
        mapAllClick();
        allClick();
        loadFragment(new HomeFragment());
        sharelink = sessionManagment.getUserDetails().get(KEY_SHARE_LINK);
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_framelayout);

                if (fragment != null && fragment.getClass() != null) {
                    Log.e("log_fragment_backstack",getCurrentFragmentName());//srtfyuhij
                    String frgmentName = fragment.getClass().getSimpleName();
                    Log.e("log_fragment_backstack_two",frgmentName);//srtfyuhij

                    if (frgmentName.equalsIgnoreCase("VechileFragment")) {
                        if (riderMarket != null) {
                            riderMarket.remove();
                            riderMarket = null;
                        }
                        riderMarket = mMap.addMarker(new MarkerOptions()
                            .position(pickUpFinalLat)
                                .title("You")
                                .icon(bitmapDescriptorFromVector(MapActivity.this, R.drawable.ic_pickup_location)));

                    }else if (frgmentName.equalsIgnoreCase("PickUpFragment") || frgmentName.contains("HomeFragment")){

                        clearRouteIfInHome();

                        if (pickUpFinalLat != null) {
                            getPickUpLatLng(pickUpFinalLat.latitude, pickUpFinalLat.longitude, tvpick.getText().toString(), pickUpFinalLat);

                            if (riderMarket != null) {
                                riderMarket.remove();
                                riderMarket = null;
                            }

                            riderMarket = mMap.addMarker(new MarkerOptions()
                                    .position(pickUpFinalLat)
                                    .title("You")
                                    .icon(bitmapDescriptorFromVector(MapActivity.this, R.drawable.ic_blue_loc)));
                        }


                    }

                    if (frgmentName.contains("HomeFragment")) {
//                        clearRouteIfInHome();
                      homeToolBar();

                    }
//                    else if(frgmentName.equalsIgnoreCase("BookingDetailFragment")){
//                        binding.linToolbar.setVisibility(View.GONE);
//                        binding.mytoolbar.setNavigationIcon(null);
//                        binding.mytoolbar.setVisibility(View.VISIBLE);
//                        binding.linBackMain.setVisibility(View.VISIBLE);
//                        binding.linOnlyBack.setVisibility(View.GONE);
//                        // setMap(false);
//                        binding.main.setVisibility(View.VISIBLE);
//                        common.setMap(false, true, 160, binding.mapContainer,
//                                binding.main.findViewById(R.id.lin_search));
//                        LatLng pickupLatLng = new LatLng(28.6139, 77.2090);        // Pickup Location
//                        LatLng destinationLatLng = new LatLng(28.7041, 77.1025);   // Destination Location
//                        disbaleMap(pickupLatLng,destinationLatLng);
//                    }
                    else if (frgmentName.contains("DestinationFragment") ||frgmentName.contains("PickUpAddressFragment") ||
                            frgmentName.contains("EnquiryFragment") ||
                            frgmentName.equalsIgnoreCase("WalletHistoryFragment") ||
                            frgmentName.equalsIgnoreCase("BookingHistoryFragment") ||
                            frgmentName.equalsIgnoreCase("ContactUsFragment") ||
                            frgmentName.equalsIgnoreCase("ProfileFragment") ||
                            frgmentName.equalsIgnoreCase("UpdateProfileFragment") ||
                            frgmentName.equalsIgnoreCase("BookingHistoryFragment") ||
                            frgmentName.equalsIgnoreCase("BookingDetailFragment")||
                            frgmentName.equalsIgnoreCase("AfterPaymentDoneFragment")

                    ) {
                        binding.linToolbar.setVisibility(View.GONE);
                        binding.mytoolbar.setNavigationIcon(null);
                        binding.mytoolbar.setVisibility(View.VISIBLE);
                        binding.linBackMain.setVisibility(View.VISIBLE);
                        binding.linOnlyBack.setVisibility(View.GONE);
                        // setMap(false);
                        binding.main.setVisibility(View.GONE);
                        common.setMap(false, false, 0, binding.mapContainer,
                                binding.main.findViewById(R.id.lin_search));

                    } else {


                        common.setMap(false, true, 160, binding.mapContainer,
                                binding.main.findViewById(R.id.lin_search));
                        binding.mytoolbar.setVisibility(View.GONE);
                        binding.linToolbar.setVisibility(View.GONE);
                        binding.mytoolbar.setNavigationIcon(null);
                        binding.linBackMain.setVisibility(View.GONE);
                        binding.linOnlyBack.setVisibility(View.VISIBLE);
                        // setMap(false);
                        binding.main.setVisibility(View.VISIBLE);

                    }
                }
            }
        });

        common.getAppSettingData(new OnConfig() {
            @Override
            public void getAppSettingData(AppSettingModel model) {
                try {
                    ver_code = (model.getVersion());
                    is_forced = (model.getIs_forced());
                    // sharelink = model.getShare_link ();
                    sharelink = sessionManagment.getUserDetails().get(KEY_SHARE_LINK);
                    share_msg = common.checkNullString(model.getShare_message());
                    appLink = model.getApp_link();
                    PackageInfo pInfo = getPackageManager().getPackageInfo(MapActivity.this.getPackageName(), 0);
                    version_code = pInfo.versionCode;
                    if (version_code < ver_code) {
                        callVersionDialog();
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void homeToolBar() {
        binding.mytoolbar.setVisibility(View.VISIBLE);
        binding.linToolbar.setVisibility(View.VISIBLE);
        binding.linBackMain.setVisibility(View.GONE);
        binding.linOnlyBack.setVisibility(View.GONE);
        toggle.syncState();
        binding.mytoolbar.setNavigationIcon(R.drawable.menu);
        common.setMap(true, true, 140, binding.mapContainer,
                binding.main.findViewById(R.id.lin_search));
        binding.main.setVisibility(View.VISIBLE);
    }

    private void animateCameraWithOffset(final LatLng targetLatLng) {
//        mMap.setOnMapLoadedCallback(() -> {
//            Projection projection = mMap.getProjection();
//            Point point = projection.toScreenLocation(targetLatLng);
//
//            point.y -= 250; // pt height fix hai → marker ko 300px upar shift karo
//
//            LatLng adjustedLatLng = projection.fromScreenLocation(point);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLngBounds bounds = builder.build();
        int padding = 100; // pixels (you can increase/decrease)
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
           //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLatLng, 15.0f));
        //});
//        mMap.setPadding(0, 250, 0, 0); // Top padding for fixed height map
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(targetLatLng, 15f);
//        mMap.animateCamera(cameraUpdate);
    }


    public void storeDataSession() {
        common.getAppSettingData(new OnConfig() {
            @Override
            public void getAppSettingData(AppSettingModel model) {
                sessionManagment.setValue(KEY_TERMS, model.getTerms_conditions());
                sessionManagment.setValue(KEY_PRIVACY, model.getPrivacy_policy());
                sessionManagment.setValue(KEY_SUPPORT_EMAIL, model.getSupport_email());
                sessionManagment.setValue(KEY_SUPPORT_MOBILE, model.getSupport_mobile());
                sessionManagment.setValue(KEY_WHATSPP, model.getSupport_whatsapp());
                sessionManagment.setValue(KEY_SUPPORT_SUBJ, model.getSupport_message());
                sessionManagment.setValue(KEY_HOME_IMG1, model.getHomeImage1());
                sessionManagment.setValue(KEY_HOME_IMG2, model.getHomeImage2());
            }
        });
    }

    public ActivityResultLauncher<String> notificationPermissionLauncher;

    public void setupNotificationPermissionLauncher() {
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        checkLocationPermission(); // Notification ke baad Location permission
                    } else {
                        Toast.makeText(activity, "Notification permission denied", Toast.LENGTH_SHORT).show();
                        // Even if denied, still check location (your choice, or you can skip)
                        checkLocationPermission();
                    }
                }
        );
    }

    public void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ ke liye
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                checkLocationPermission(); // Already granted, directly check location
            }
        } else {
            // Android 12 ya neeche, notification permission nahi hoti
            checkLocationPermission();
        }
    }

    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (locationPermissionLauncher != null) {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        } else {
            Toast.makeText(activity, "Permission granted (legacy device)", Toast.LENGTH_SHORT).show();
            if (!isLocationEnabled()) {
                showEnableLocationDialog();
            }
        }
    }

    public void setupLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if (isLocationEnabled()) {
                            Toast.makeText(activity, "Location is enabled", Toast.LENGTH_SHORT).show();
                            MapActivity.isAddressFetched = false;
//                            ((MapActivity)getActivity()).verifyGoogleAccount();
                            mapCode();
                            // TODO: Start using location here
                        } else {
                            showEnableLocationDialog();
                        }
                    } else {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            openAppSettings();
                        } else {
                            Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public void showCustomLocationDialog() {
        Dialog dialog = new Dialog(activity);
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

    //
    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
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

    public void showEnableLocationDialog() {
        Dialog dialog = new Dialog(activity);
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

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null &&
                (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void getMenuList() {
        mlist.clear();
        mlist.add(new MenuModel("Home", R.drawable.ic_home));
        mlist.add(new MenuModel("Wallet History", R.drawable.ic_wallet));
        mlist.add(new MenuModel("Booking History", R.drawable.ic_history));
        mlist.add(new MenuModel("Enquiry", R.drawable.ic_enquiry));
        mlist.add(new MenuModel("Notifications", R.drawable.ic_bell));
        mlist.add(new MenuModel("Contact Us", R.drawable.ic_support));
        mlist.add(new MenuModel("Terms & Conditions", R.drawable.ic_policy));
        mlist.add(new MenuModel("Privacy Policy", R.drawable.ic_policy));
        mlist.add(new MenuModel("Share App", R.drawable.ic_share));
        mlist.add(new MenuModel("Logout", R.drawable.ic_logout));


        menuAdapter = new MenuAdapter(MapActivity.this, mlist, new MenuAdapter.onTouchMethod() {
            @Override
            public void onSelection(int pos) {
                Fragment fm = null;
                String title = mlist.get(pos).getTitle();
                switch (title.toLowerCase().toString()) {
                    case "logout":
                        showLogoutDialog();
                        break;
                    case "home":
                        fm = new HomeFragment();
                        break;
                    case "terms & conditions":
                        Intent i = new Intent(MapActivity.this, PrivacyPolicyActivity.class);
                        i.putExtra("type", "terms");
                        startActivity(i);
                        break;
                    case "privacy policy":
                        Intent i2 = new Intent(MapActivity.this, PrivacyPolicyActivity.class);
                        i2.putExtra("type", "policy");
                        startActivity(i2);
                        break;
                    case "notifications":
                        Intent i3 = new Intent(MapActivity.this, NotificationsActivity.class);
                        startActivity(i3);
                        break;
                    case "enquiry":
                        fm = new EnquiryFragment();
                        break;
                    case "wallet history":
                        fm = new WalletHistoryFragment();
                        break;
                    case "booking history":
                        fm = new BookingHistoryFragment();
                        break;
                    case "contact us":
                        fm = new ContactUsFragment();
                        break;
                    case "share app":
                        common.shareLink(share_msg + "\n" + Html.fromHtml(sharelink));
                        break;
                }
                binding.drawer.closeDrawer(GravityCompat.START);
                if (fm != null) {
                    binding.drawer.closeDrawer(GravityCompat.START);
                    common.switchFragment(fm);
                }
            }
        });

        binding.recMenu.setAdapter(menuAdapter);
    }

    public void setImage(String val) {
        Picasso.get().load(IMAGE_BASE_URL + val).placeholder(R.drawable.logo).
                error(R.drawable.logo).into(binding.navHeader.civLogo);
    }


    public void getPickUpLatLng(Double Lat, Double Lng, String pickAddressValue, LatLng latLng) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            pickupLat = Lat;
            pickupLng = Lng;
            pickAddres = pickAddressValue;
//            tvpick.setText(pickAddressValue);
            pickLatLng = latLng;

            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    //String fragmentName = fragment.getClass().getSimpleName();
                    //Log.e("FragmentCheck", "Found fragment: " + fragmentName);

                    if (fragment instanceof PickUpFragment && fragment.isVisible()) {
                        Log.e("FragmentCheck", "Updating PickUpFragment");
                        tvpick.setText(pickAddressValue);
                        ((PickUpFragment) fragment).updateText(pickAddressValue);
                        break;
                    }else if (fragment instanceof PickUpAddressFragment && fragment.isVisible()) {
                        tvpick.setText(pickAddressValue);
                        break;
                    }
                }
            }
            tvpick.setText(pickAddressValue);
            showPickupMarker(latLng);


        }, 200); // 200ms delay to ensure fragment is ready
    }

    private void showPickupMarker(LatLng latLng) {
        if(latLng!=null) {
            pickUpFinalLat = latLng;
//        riderMarket = mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title("You")
//                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            if (riderMarket != null) {
                riderMarket.remove();
                riderMarket = null;
            }

            // Determine fragment name
            String fragmentName = getCurrentFragmentName(); // We'll define this function below
            int markerIconResId;

            // Decide icon based on current fragment
            if ("HomeFragment".equals(fragmentName)) {
                markerIconResId = R.drawable.ic_blue_loc; // example icon
            } else if ("PickUpFragment".equals(fragmentName)) {
                markerIconResId = R.drawable.ic_blue_loc;
            } else {
                markerIconResId = R.drawable.ic_pickup_location; // fallback

            }

            riderMarket = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("You")
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    .icon(bitmapDescriptorFromVector(this, markerIconResId)));

        }
    }

    public void setDriverLocation(String Lat, String Lng) {

    }

    public String getPickupAddress() {
        return pickAddres;
    }

    public Double getPickupLat() {
        return pickupLat;
    }

    public Double getPickupLng() {
        return pickupLng;
    }

    public void getDestinationLatLng(Double Lat, Double Lng, String destinationAddressValue, LatLng latLng) {
        destinationLat = Lat;
        destinationLng = Lng;
        destinationAddress = destinationAddressValue;
        destinationLatLng=latLng;
        tvDestination.setText(destinationAddressValue);

        if (mMap != null) {
            if (destinationMarker != null) {
                destinationMarker.remove();
            }

            destinationMarker=mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Destination")
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_destination_location)));
        } else {
            Log.e("MapError", "Google Map not ready yet. Skipping marker placement.");
        }

        // Determine fragment name
        String fragmentName = getCurrentFragmentName(); // We'll define this function below

        int markerIconResId;

        // Decide icon based on current fragment
//        if ("HomeFragment".equals(fragmentName)) {
//
//        } else if ("PickUpFragment".equals(fragmentName)) {
//
//        }else {
            drawRoute(pickLatLng,destinationLatLng);
//        }

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
//        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
//        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        vectorDrawable.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);


        int height = 100; // you can adjust
        int width = 100;  // you can adjust
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(vectorResId);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }

//    private void drawRoute(LatLng origin, LatLng dest) {
//        if (origin != null && dest != null) {
//            Log.d("draww", "drawRoute: " + origin + "==" + dest);
//            String url = getDirectionsUrl(origin, dest);
//            new DownloadTask().execute(url);
//        }else {
//            Log.e("MapActivity", "Pickup  Destination location null");
//        }
//    }

    private void clearRouteIfInHome() {
        if (("HomeFragment".equals(getCurrentFragmentName())||"PickUpFragment".equals(getCurrentFragmentName())) && mMap != null) {
            mMap.clear();
            showPickupMarker(Common.currenLocation);
        }
    }

    private void drawRoute(LatLng origin, LatLng dest) {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            String currentFragment = getCurrentFragmentName();
            Log.e("log_fragment_drawRoute",currentFragment);//srtfyuhij
            Log.e("origin----dest", String.valueOf(origin) +"----"+String.valueOf(dest));
            if (origin != null && dest != null) {
                if (!"HomeFragment".equals(currentFragment) && !"PickUpFragment".equals(currentFragment)) {
                    Log.d("drawRoute", "Drawing route from " + origin + " to " + dest);
                    String url = getDirectionsUrl(origin, dest);
                    new DownloadTask().execute(url);
                } else {
                    Log.d("drawRoute", "In HomeFragment, skipping route drawing.");
                }
            } else {
                Log.e("MapActivity", "Pickup or Destination location is null.");
            }

        }, 200);  // 200ms delay gives enough time for fragment to settle

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";
        String apiKeyval = apiKey;

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + apiKeyval;
    }
    private class DownloadTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... url) {
            String data = "";
            try {
                URL myUrl = new URL(url[0]);
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();
                iStream.close();
                connection.disconnect();

            } catch (Exception e) {
                Log.d("DownloadTask", e.toString());
            }
            return data;
        }

        protected void onPostExecute(String result) {
            new ParserTask().execute(result);
        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            for (List<HashMap<String, String>> path : result) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    points.add(new LatLng(lat, lng));
                }

                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true); // optional: smoother lines
            }

            if (currentPolyline != null) {
                currentPolyline.remove();
            }

            if (lineOptions != null && mMap != null) {
                currentPolyline = mMap.addPolyline(lineOptions);
            }
        }

    }
    public Double getDestinationLat() {

        return destinationLat;

    }

    public Double getDestinationLng() {

        return destinationLng;

    }

    public String getDestionationAddress() {
        return destinationAddress;
    }


    public void mapAllClick() {

    }

    public void callVersionDialog() {
        Dialog dialog;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_update_version);
        Button btn_leave, btn_stay;
        TextView tv_cancel, tv_message;
        btn_stay = dialog.findViewById(R.id.update);
        btn_leave = dialog.findViewById(R.id.cancel);
        tv_cancel = dialog.findViewById(R.id.tv_cancel);
        tv_message = dialog.findViewById(R.id.tv_message);
        btn_stay.setText(getString(R.string.Update_Now));
        tv_cancel.setText(getString(R.string.update_Available));
        tv_message.setText(getString(R.string.a_new_version_of_the_app));

        if (is_forced == 0) {
            btn_leave.setText(getString(R.string.update_Later));

        } else if (is_forced == 1) {
            btn_leave.setText(getString(R.string.No_Thanks));

        }

        btn_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (is_forced == 0) {
                    dialog.dismiss();

                } else if (is_forced == 1) {
                    dialog.dismiss();
                    finishAffinity();
                }
            }
        });

        btn_stay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(appLink));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        if (is_forced == 2) {
            dialog.dismiss();
        }
    }

    public void mapCode() {
        Log.e("dfcvghnjk", "vgbhjnmkl");
        location = new Location(this, new locationListener() {
            @Override
            public void locationResponse(LocationResult response) {
                currentLat = response.getLastLocation().getLatitude();
                currentLng = response.getLastLocation().getLongitude();
                Common.currenLocation = new LatLng(currentLat, currentLng);
                Log.e("sxdcfgvbhjnk", String.valueOf(currentLat));
                displayLocation();
                if (!pickupPlacesSelected) {
                    getPickUpLatLng(currentLat, currentLng, tvpick.getText().toString(),Common.currenLocation);
                }

            }
        });

        location.inicializeLocation();
        mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);
    }

    public void displayLocation() {
        Log.e("gvbhnjkl", String.valueOf(currentLat));
        if (currentLat != null && currentLng != null) {
            Log.d("Tag", "displayLocation: " + currentLat + "--" + currentLng);

            if (!isAddressFetched) {
                loadAllAvailableDriver(new LatLng(currentLat, currentLng));
                fetchNearbyLocations(currentLat, currentLng); // Call to fetch 3 nearby places
                isAddressFetched = true;
            }
        }
    }

    public void loadAllAvailableDriver(final LatLng location) {
        for (Marker driverMarker : driverMarkers) {
            driverMarker.remove();
        }

        driverMarkers.clear();
        if (!pickupPlacesSelected) {
            if (riderMarket != null)
                riderMarket.remove();


           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));

            String address = getAddressFromLatLng(MapActivity.this, currentLat, currentLng);
            binding.tvAddress.setText(address);


            tvpick.setText(address);
            getPickUpLatLng(currentLat, currentLng, address,location);

        }
    }

    public String getAddressFromLatLng(Activity context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // Full address
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address not found";
    }

    public void verifyGoogleAccount() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();

        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    //handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    public void setTitle(String title) {
        binding.tvTitle.setText(title);
    }

//    public void setTitleWithSize(String title,int size){
//         binding.tvTitle.setText(title);
//
//        binding.tvTitle.setTextSize(size);
//    }

    public void allClick() {
        binding.main.findViewById(R.id.lin_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.switchFragment(new PickUpFragment());
            }
        });
        tvpick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_framelayout);
                if (fragment == null || !(fragment instanceof PickUpAddressFragment)) {
                    common.switchFragment(new PickUpAddressFragment());
                }
            }
        });
//        tvpick.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!s.toString().isEmpty()) {
//                    try {
//                        // Find your fragment instance
//                        PickUpAddressFragment fragment = (PickUpAddressFragment) getSupportFragmentManager()
//                                .findFragmentById(R.id.main_framelayout);
//
//                        if (fragment != null) {
//                            fragment.fetchAutocompleteSuggestions(s.toString());
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                } else {
//                    // You can clear list too, by calling method in fragment or via interface
//                    PickUpAddressFragment fragment = (PickUpAddressFragment) getSupportFragmentManager()
//                            .findFragmentById(R.id.main_framelayout);
//
//                    if (fragment != null) {
//                        fragment.clearList();
//                    }
//                }
//            }
//
//            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override public void afterTextChanged(Editable s) {}
//        });
        tvDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_framelayout);
                if (fragment == null || !(fragment instanceof DestinationFragment)) {
                    common.switchFragment(new DestinationFragment());
                }
            }
        });


//        tvDestination.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!s.toString().isEmpty()) {
//                    try {
//                        // Find your fragment instance
//                        DestinationFragment fragment = (DestinationFragment) getSupportFragmentManager()
//                                .findFragmentById(R.id.main_framelayout);
//
//                        if (fragment != null) {
//                            fragment.fetchAutocompleteSuggestions(s.toString());
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                } else {
//                    // You can clear list too, by calling method in fragment or via interface
//                    DestinationFragment fragment = (DestinationFragment) getSupportFragmentManager()
//                            .findFragmentById(R.id.main_framelayout);
//
//                    if (fragment != null) {
//                        fragment.clearList();
//                    }
//                }
//            }
//
//            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override public void afterTextChanged(Editable s) {}
//        });


        binding.commonAddress.findViewById(R.id.iv_pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvpick.setText("");

            }
        }); binding.commonAddress.findViewById(R.id.iv_destination).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDestination.setText("");
            }
        });
        binding.ivWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.switchFragment(new WalletHistoryFragment());
            }
        });
        binding.ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i3 = new Intent(MapActivity.this, NotificationsActivity.class);
                startActivity(i3);
            }
        });
        binding.ivBackarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.ivBackOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        binding.navHeader.linMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.switchFragment(new ProfileFragment());
                binding.drawer.closeDrawer(GravityCompat.START);
            }
        });


    }

    public void showLogoutDialog() {
        Dialog dialog;

        dialog = new Dialog(MapActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_logout);
        Button btn_no, btn_yes;
        btn_yes = dialog.findViewById(R.id.btn_yes);
        btn_no = dialog.findViewById(R.id.btn_no);

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sessionManagment.logout(MapActivity.this);
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_framelayout);

        if (fragment != null && fragment.getClass() != null) {
            String frgmentName = fragment.getClass().getSimpleName();

            if (frgmentName.contains("AfterPaymentDoneFragment")) {
                Intent intent = new Intent(this, MapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }


    }

    public void loadFragment(Fragment fragment) {

        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().
                addToBackStack(null).add(R.id.main_framelayout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    public void initView() {
        activity = MapActivity.this;
        mlist = new ArrayList<>();
        sessionManagment = new SessionManagment(MapActivity.this);
        common = new Common(MapActivity.this);
        setSupportActionBar(binding.mytoolbar);
        binding.recMenu.setLayoutManager(new LinearLayoutManager(MapActivity.this));
        toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.mytoolbar, R.string.drawer_open, R.string.drawer_close);
        binding.drawer.addDrawerListener(toggle);
        tvpick = binding.commonAddress.findViewById(R.id.tv_pick);
        tvDestination = binding.commonAddress.findViewById(R.id.tv_desctination);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        binding.mytoolbar.setNavigationIcon(R.drawable.menu);
        binding.mytoolbar.setContentInsetsAbsolute(0, 0);
        binding.mytoolbar.setNavigationOnClickListener(view -> {
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START);
            } else {
                binding.drawer.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (!marker.getTitle().equals("You")) {
//            Intent intent=new Intent(MapActivity.this, CallDriverActivity.class);
//            String ID= marker.getSnippet().replace("Driver ID: ", "");
//            intent.putExtra("driverID", ID);
//            intent.putExtra("lat", currentLat);
//            intent.putExtra("lng", currentLng);
//            startActivity(intent);
        }
    }

    private boolean isMapClicked = false;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set click listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                String fragmentName = getCurrentFragmentName();
                if ("HomeFragment".equals(fragmentName) || "PickUpFragment".equals(fragmentName)) {
                    pickupPlacesSelected = true; // ✅ Stop auto-updating pickup
                    isMapClicked = true; // ✅ Set flag
                    String address = getAddressFromLatLng(MapActivity.this, latLng.latitude, latLng.longitude);

                    getPickUpLatLng(latLng.latitude, latLng.longitude, address, latLng);

                    if (riderMarket != null) riderMarket.remove();
                    showPickupMarker(latLng);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                   // animateCameraWithOffset(latLng);
                     binding.tvAddress.setText(address);
                    if (tvpick != null)
                        tvpick.setText(address);
                    drawRoute(latLng, destinationLatLng);

                    // ✅ Reset flag after small delay (e.g. 1 sec)
                    new Handler().postDelayed(() -> isMapClicked = false, 1000);
                }
            }
        });


        // Optional: show current location once map is ready
//        displayLocation();

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (isMapClicked) return; // ✅ Ignore if just clicked

                String fragmentName = getCurrentFragmentName();
                if ("HomeFragment".equals(fragmentName) || "PickUpFragment".equals(fragmentName)) {

                    LatLng center = mMap.getCameraPosition().target;
                    String address = getAddressFromLatLng(MapActivity.this, center.latitude, center.longitude);
                 //  Common.currenLocation = new LatLng(center.latitude, center.longitude);
                  //  getPickUpLatLng(center.latitude, center.longitude, tvpick.getText().toString(),center);
                    getPickUpLatLng(center.latitude, center.longitude, address,center);



                    binding.tvAddress.setText(address);
                    if (tvpick != null)
                        tvpick.setText(address);

                    if (riderMarket != null) riderMarket.remove();
                    showPickupMarker(center);
                    drawRoute(center, destinationLatLng);
//                    if(fragmentName.equalsIgnoreCase("HomeFragment")){
//                        common.switchFragment(new PickUpFragment());
//                    }
                }
            }
        });
       showPickupAndDropMarkers();
    }
    private void showPickupAndDropMarkers() {
        if (pickupLng != null && pickupLat != null && destinationLat != null && destinationLng != null) {
            LatLng pickupLatLng = new LatLng(pickupLat, pickupLng);
            LatLng dropLatLng = new LatLng(destinationLat, destinationLng);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(pickupLatLng);
            builder.include(dropLatLng);
            LatLngBounds bounds = builder.build();

            int padding = 150;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
    }

    public void setHomeAddress(String address){
        binding.tvAddress.setText(address);
        tvpick.setText(address);

    }

    private String getCurrentFragmentName() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_framelayout); // replace with your container ID
        if (currentFragment != null) {
            return currentFragment.getClass().getSimpleName(); // e.g., "HomeFragment"
        }
        return null;
    }

    public void showCommonPickDestinationArea(boolean status, boolean is_close) {
        if (status) {
            binding.commonAddress.setVisibility(View.VISIBLE);
            if (is_close) {
                binding.commonAddress.findViewById(R.id.iv_pick).setVisibility(View.VISIBLE);
                binding.commonAddress.findViewById(R.id.iv_destination).setVisibility(View.VISIBLE);
            } else {
                binding.commonAddress.findViewById(R.id.iv_pick).setVisibility(View.GONE);
                binding.commonAddress.findViewById(R.id.iv_destination).setVisibility(View.GONE);
            }
        } else {
            binding.commonAddress.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //displayLocation();
        if (location != null) {
            location.inicializeLocation();
        }

    }

    private void fetchFullAddressFromPlaceId(String placeId, AddressCallback callback) {
        String apiKey = getString(R.string.google_maps_key);
        String url = "https://maps.googleapis.com/maps/api/place/details/json?"
                + "place_id=" + placeId
                + "&fields=formatted_address"
                + "&key=" + apiKey;

        new Thread(() -> {
            try {
                URL requestUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    java.util.Scanner scanner = new java.util.Scanner(inputStream);
                    scanner.useDelimiter("\\A");
                    String result = scanner.hasNext() ? scanner.next() : "";

                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject resultObj = jsonObject.getJSONObject("result");

                    String fullAddress = resultObj.optString("formatted_address", "Not available");

                    runOnUiThread(() -> callback.onAddressFetched(fullAddress));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void fetchNearbyLocations(double latitude, double longitude) {
       // Make sure this is defined in your strings.xml
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + latitude + "," + longitude
                + "&radius=2500" // meters
                + "&type=tourist_attraction" // Change type if needed: restaurant, cafe, etc.
                + "&key=" + apiKey;

        Log.d("NearbyURLcvbjnkml,;.", url);

        new Thread(() -> {
            try {
                java.net.URL requestUrl = new java.net.URL(url);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.InputStream inputStream = connection.getInputStream();
                    java.util.Scanner scanner = new java.util.Scanner(inputStream);
                    scanner.useDelimiter("\\A");
                    String result = scanner.hasNext() ? scanner.next() : "";

                    runOnUiThread(() -> parseNearbyPlaces(result));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void parseNearbyPlaces(String json) {
        try {
            areaList.clear();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray results = jsonObject.getJSONArray("results");

            int limit = Math.min(results.length(), 3); // Limit to 3 results

            for (int i = 0; i < limit; i++) {
                JSONObject place = results.getJSONObject(i);
                String name = place.getString("name");
                String placeId = place.getString("place_id");
                JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");

                fetchFullAddressFromPlaceId(placeId, address -> {
                    NearAreaNameModel model = new NearAreaNameModel(name, lat, lng, address);
                    areaList.add(model);
                    Log.d("NearbyPlace", "Name: " + name + " LatLng: " + lat + "," + lng + " Address: " + address);

//                    mMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(lat, lng))
//                            .title(name)
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                    Log.d("hjhfjy2", "getDestinatioList: " + areaList.size());
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}