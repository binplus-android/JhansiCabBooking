package com.cabbooking.activity;

import static com.cabbooking.utils.RetrofitClient.BASE_URL;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabbooking.R;
import com.cabbooking.adapter.MenuAdapter;
import com.cabbooking.databinding.ActivityMapBinding;
import com.cabbooking.fragement.EnquiryFragment;
import com.cabbooking.fragement.HomeFragment;
import com.cabbooking.fragement.WalletHistoryFragment;
import com.cabbooking.model.AppSettingModel;
import com.cabbooking.model.MenuModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.CustomInfoWindow;
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
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener {
    Common common;
    ActivityMapBinding binding;
    SessionManagment sessionManagment;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    Location location;
    private Marker riderMarket, destinationMarker;
    private Double currentLat, currentLng;
    private String mPlaceLocation, mPlaceDestination;
    private SupportMapFragment mapFragment;
    private DatabaseReference driversAvailable;
    private ArrayList<Marker> driverMarkers=new ArrayList<>();
    private boolean pickupPlacesSelected=false;
    private int radius=1, distance=1; // km
    private static final int LIMIT=3;
    private String URL_BASE_API_PLACES="https://maps.googleapis.com/maps/api/place/textsearch/json?";
    ActionBarDrawerToggle toggle;
    TextView tvpick,tvDestination;
    String sharelink=BASE_URL;
    Integer ver_code, is_forced=0, version_code;
     Double pickupLat=0.0,destinationLat=0.0;
     Double pickupLng=0.0,destinationLng=0.0;
     String pickAddres="",destinationAddress="";
    private boolean isAddressFetched = false; // Add this field
    ArrayList<MenuModel>mlist;
    MenuAdapter menuAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        initView();
        getMenuList();
        verifyGoogleAccount();
        mapCode();
        mapAllClick();
        allClick();
        loadFragment(new HomeFragment());

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_framelayout);

                if (fragment != null && fragment.getClass() != null) {
                    String frgmentName = fragment.getClass().getSimpleName();

                    if (frgmentName.contains("HomeFragment")) {
                        binding.mytoolbar.setVisibility(View.VISIBLE);
                        binding.linToolbar.setVisibility(View.VISIBLE);
                        binding.linBackMain.setVisibility(View.GONE);
                        binding.linOnlyBack.setVisibility(View.GONE);
                        toggle.syncState();
                        binding.mytoolbar.setNavigationIcon(R.drawable.menu);
                        common.setMap(true,true,140,binding.mapContainer,
                                binding.main.findViewById(R.id.lin_search));
                        binding.main.setVisibility(View.VISIBLE);

                    } else if(frgmentName.contains("DestinationFragment")||
                            frgmentName.contains("EnquiryFragment")||
                            frgmentName.equalsIgnoreCase("WalletHistoryFragment")) {
                        binding.linToolbar.setVisibility(View.GONE);
                        binding.mytoolbar.setNavigationIcon(null);
                        binding.mytoolbar.setVisibility(View.VISIBLE);
                        binding.linBackMain.setVisibility(View.VISIBLE);
                        binding.linOnlyBack.setVisibility(View.GONE);
                        // setMap(false);
                        binding.main.setVisibility(View.GONE);
                        common.setMap(false,false,0,binding.mapContainer,
                                binding.main.findViewById(R.id.lin_search));

                    } else {
                        common.setMap(false,true,160,binding.mapContainer,
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
                                ver_code = (model.getVersion ( ));
                                is_forced = (model.getIs_forced ( ));
                                sharelink = model.getShare_link ();
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

    private void getMenuList() {
       mlist.clear();
       mlist.add(new MenuModel("Home",R.drawable.ic_home));
       mlist.add(new MenuModel("Enquiry",R.drawable.ic_enquiry));
       mlist.add(new MenuModel("Wallet History",R.drawable.ic_wallet));
       menuAdapter=new MenuAdapter(MapActivity.this, mlist, new MenuAdapter.onTouchMethod() {
           @Override
           public void onSelection(int pos) {
               Fragment fm=null;
               String title=mlist.get(pos).getTitle();
               switch (title.toLowerCase().toString()){
                   case "home":
                       fm=new HomeFragment();
                       break;
                       case "enquiry":
                       fm=new EnquiryFragment();
                       break;
                       case "wallet history":
                       fm=new WalletHistoryFragment();
                       break;
               }
               if(fm!=null){
                   binding.drawer.closeDrawer(GravityCompat.START);
                   common.switchFragment(fm);
               }


           }
       });
       binding.recMenu.setAdapter(menuAdapter);
    }

    public void getPickUpLatLng(Double Lat,Double Lng,String pickAddressValue){
        pickupLat=Lat;
        pickupLng=Lng;
        pickAddres=pickAddressValue;


    }

    public  void setDriverLocation(String  Lat,String  Lng){

    }

    public String getPickupAddress() {
        return pickAddres;
    } public Double getPickupLat() {
        return pickupLat;
    }

    public Double getPickupLng() {
        return pickupLng;
    }
    public void getDestinationLatLng(Double Lat,Double Lng,String destinationAddressValue){
        destinationLat=Lat;
        destinationLng=Lng;
       destinationAddress= destinationAddressValue;

    }

    public Double getDestinationLat() {
        //remove if condition when map start working
        if(destinationLat.equals("")){
            return pickupLat;
        }
        else{
        return destinationLat;
    }}

    public Double getDestinationLng() {
        //remove if condition when map start working
        if(destinationLng.equals("")){
            return pickupLng;
        }
        else {
            return destinationLng;
        }
    }
    public String getDestionationAddress() {
        //remove if condition when map start working
        if(destinationAddress.equals("")){
            return pickAddres;
        }
        else {
            return destinationAddress;
        }
    }

    private void mapAllClick() {
    }

    public void callVersionDialog() {
        Dialog dialog;
        dialog = new Dialog (this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView (R.layout.dialog_update_version);
        Button btn_leave,btn_stay;
        TextView tv_cancel,tv_message;
        btn_stay=dialog.findViewById (R.id.update);
        btn_leave=dialog.findViewById (R.id.cancel);
        tv_cancel=dialog.findViewById (R.id.tv_cancel);
        tv_message=dialog.findViewById (R.id.tv_message);
        btn_stay.setText (getString (R.string.Update_Now));
        tv_cancel.setText (getString (R.string.update_Available));
        tv_message.setText (getString (R.string.a_new_version_of_the_app));

        if (is_forced == 0) {
            btn_leave.setText (getString (R.string.update_Later));

        } else if (is_forced == 1) {
            btn_leave.setText (getString (R.string.No_Thanks));

        }

        btn_leave.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                if (is_forced == 0) {
                    dialog.dismiss ( );

                } else if (is_forced == 1) {
                    dialog.dismiss ( );
                    finishAffinity ( );
                }
            }
        });

        btn_stay.setOnClickListener (new View.OnClickListener ( ) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent (Intent.ACTION_VIEW);
                    intent.setData (Uri.parse (sharelink));
                    startActivity (intent);
                }catch (Exception e){
                    e.printStackTrace ();
                }
            }
        });
        dialog.show ( );
        dialog.setCanceledOnTouchOutside (false);
        dialog.setCancelable (false);

        if (is_forced == 2) {
            dialog.dismiss();
        }


    }

    public void mapCode() {
        location=new Location(this, new locationListener() {
            @Override
            public void locationResponse(LocationResult response) {
                currentLat=response.getLastLocation().getLatitude();
                currentLng=response.getLastLocation().getLongitude();
                Common.currenLocation=new LatLng(currentLat,currentLng);
                displayLocation();
                getPickUpLatLng(currentLat,currentLng,tvpick.getText().toString());
            }
        });
         mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);
    }

    public void displayLocation() {
        if (currentLat != null && currentLng != null) {
            Log.d("Tag", "displayLocation: " + currentLat + "--" + currentLng);

            if (!isAddressFetched) {
                loadAllAvailableDriver(new LatLng(currentLat, currentLng));
//                fetchNearbyLocations(currentLat, currentLng); // Call to fetch 3 nearby places
                isAddressFetched = true;
            }
        }
    }

    private void loadAllAvailableDriver(final LatLng location) {
        for (Marker driverMarker:driverMarkers) {
            driverMarker.remove();
        }

        driverMarkers.clear();
        if(!pickupPlacesSelected) {
            if (riderMarket != null)
                riderMarket.remove();
            // Resize the drawable
            int height = 130; // you can adjust
            int width = 130;  // you can adjust
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_blue_loc);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


            riderMarket = mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("You")
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));
            String address = getAddressFromLatLng(MapActivity.this, currentLat, currentLng);
            binding.tvAddress.setText(address);


            tvpick.setText(address);
            getPickUpLatLng(currentLat,currentLng,address);
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

    private void verifyGoogleAccount() {
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        OptionalPendingResult<GoogleSignInResult> opr=Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()){
            GoogleSignInResult result= opr.get();

        }else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    //handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    public void setTitle(String title){
         binding.tvTitle.setText(title);
    }

    private void allClick() {
       
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

        binding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                    binding.drawer.closeDrawer(GravityCompat.START);
                }
                showLogoutDialog();
            }
        });

    }

    private void showLogoutDialog() {
        Dialog dialog;

        dialog = new Dialog (MapActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView (R.layout.dialog_logout);
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
                sessionManagment.logout(MapActivity.this);
            }
        });
        dialog.setCanceledOnTouchOutside (false);
        dialog.show ();
    }

    @Override
    public void onBackPressed() {
    super.onBackPressed();
    }

    public void loadFragment(Fragment fragment) {

        Bundle bundle=new Bundle();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().
                addToBackStack(null).add(R.id.main_framelayout,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    private void initView() {
        mlist=new ArrayList<>();
        sessionManagment=new SessionManagment(MapActivity.this);
        common=new Common(MapActivity.this);
        setSupportActionBar(binding.mytoolbar);
        binding.recMenu.setLayoutManager(new LinearLayoutManager(MapActivity.this));
        toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.mytoolbar, R.string.drawer_open, R.string.drawer_close);
        binding.drawer.addDrawerListener(toggle);
        tvpick=binding.commonAddress.findViewById(R.id.tv_pick);
        tvDestination=binding.commonAddress.findViewById(R.id.tv_desctination);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        binding.mytoolbar.setNavigationIcon(R.drawable.menu);
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
        if(!marker.getTitle().equals("You")){
//            Intent intent=new Intent(MapActivity.this, CallDriverActivity.class);
//            String ID= marker.getSnippet().replace("Driver ID: ", "");
//            intent.putExtra("driverID", ID);
//            intent.putExtra("lat", currentLat);
//            intent.putExtra("lng", currentLng);
//            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set click listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // Remove previous marker
                if (riderMarket != null) riderMarket.remove();

                // Add new marker
                riderMarket = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Selected Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                // Move camera
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                // Get address and update UI
                String address = getAddressFromLatLng(MapActivity.this, latLng.latitude, latLng.longitude);
                tvDestination.setText(address);
                getDestinationLatLng(latLng.latitude, latLng.longitude,address);
                binding.tvAddress.setText(address);
                if (tvpick != null) tvpick.setText(address);
            }
        });

        // Optional: show current location once map is ready
//        displayLocation();

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center = mMap.getCameraPosition().target;

                // Update the address from camera's center location
                String address = getAddressFromLatLng(MapActivity.this, center.latitude, center.longitude);
                binding.tvAddress.setText(address);
                if (tvpick != null) tvpick.setText(address);

                // Optional: move marker to center
                if (riderMarket != null) riderMarket.remove();
                riderMarket = mMap.addMarker(new MarkerOptions()
                        .position(center)
                        .title("Selected Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }
        });
//                displayLocation();

    }



    //    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.getUiSettings().setZoomGesturesEnabled(true);
//        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));
//        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style_map));
//
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                if(destinationMarker!=null)
//                    destinationMarker.remove();
//
//                int height = 70; // you can adjust
//                int width = 50;  // you can adjust
//
//                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_location);
//                Bitmap b = bitmapdraw.getBitmap();
//                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//                destinationMarker=mMap.addMarker(new MarkerOptions().position(latLng)
//                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
//                        .title("Destination"));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
//                String address = getAddressFromLatLng(MapActivity.this, latLng.latitude, latLng.longitude);
//                tvDestination.setText(address);
//            }
//        });
//        mMap.setOnInfoWindowClickListener(this);
//        displayLocation();
//    }
    public void showCommonPickDestinationArea(boolean status,boolean is_close){
       if(status){
           binding.commonAddress.setVisibility(View.VISIBLE);
           if(is_close){
               binding.commonAddress.findViewById(R.id.iv_pick).setVisibility(View.VISIBLE);
               binding.commonAddress.findViewById(R.id.iv_destination).setVisibility(View.VISIBLE);
           }
           else{
               binding.commonAddress.findViewById(R.id.iv_pick).setVisibility(View.GONE);
               binding.commonAddress.findViewById(R.id.iv_destination).setVisibility(View.GONE);
           }
       }
       else{
           binding.commonAddress.setVisibility(View.GONE);
       }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //displayLocation();
        location.inicializeLocation();
    }

    private void fetchNearbyLocations(double latitude, double longitude) {
        String apiKey = getString(R.string.google_maps_key); // Make sure this is defined in your strings.xml
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + latitude + "," + longitude
                + "&radius=1500" // meters
                + "&type=restaurant" // Change type if needed: restaurant, cafe, etc.
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

    private void parseNearbyPlaces(String json) {
        try {
            Log.e("josn",json);

            JSONObject jsonObject = new JSONObject(json);
            JSONArray results = jsonObject.getJSONArray("results");
            Log.e("result",results.toString());
            int limit = Math.min(results.length(), 3); // Limit to 3 results
            for (int i = 0; i < limit; i++) {
                JSONObject place = results.getJSONObject(i);
                String name = place.getString("name");
                JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");

                Log.d("NearbyPlace", "Name: " + name + " LatLng: " + lat + "," + lng);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}