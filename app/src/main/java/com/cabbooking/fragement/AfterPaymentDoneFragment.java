package com.cabbooking.fragement;

import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;
import static com.cabbooking.utils.SessionManagment.KEY_ID;
import static com.cabbooking.utils.SessionManagment.KEY_OUTSTATION_TYPE;
import static com.cabbooking.utils.SessionManagment.KEY_TYPE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cabbooking.R;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.DriverLocationResp;
import com.cabbooking.Response.PaymentResp;
import com.cabbooking.Response.TripDetailRes;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentAfterPaymentDoneBinding;
import com.cabbooking.databinding.FragmentPaymentBinding;
import com.cabbooking.interfaces.WalletCallBack;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.core.Repo;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AfterPaymentDoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfterPaymentDoneFragment extends Fragment implements OnMapReadyCallback {

    String trip_type="",outstation_type="",tripId="",driver_Number="";
    SessionManagment sessionManagment;
    FragmentAfterPaymentDoneBinding binding;
    Repository repository;
    Common common;
    private Handler handler;
    private Runnable apiRunnable;
    private boolean isFragmentVisible = false;

    private static final long API_REFRESH_INTERVAL = 8000; //  8 seconds
    String amount_pay="0";
    int wallet_amount=0;
    private GoogleMap mMap;
    private LatLng pickupLatLng = null;
    private LatLng destinationLatLng = null;
    private Polyline currentPolyline; // add this at class level


    //public void setLocations(double pickupLat, double pickupLng, double destLat, double destLng) {
        public void setLocations(double pickupLat, double pickupLng,double destLat, double destLng) {
            common=new Common(getActivity());
        pickupLatLng = new LatLng(pickupLat, pickupLng);
            LatLng updatedPickupLatLng = new LatLng(pickupLat, pickupLng);
        destinationLatLng = new LatLng(destLat, destLng);
            updateRoute(pickupLatLng,destinationLatLng);
        if (mMap != null) {
            updateMapMarkers();

        }

    }
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
    private void updateRoute(LatLng pickupLatLng, LatLng dropLatLng) {
        String url = getDirectionsUrl(pickupLatLng, dropLatLng);
        new FetchURL().execute(url);
    }


    // 3. FetchURL class to get directions
    private class FetchURL extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                data = buffer.toString();
                reader.close();
                inputStream.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    // 4. ParserTask to parse the route and draw polyline
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
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

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            if (currentPolyline != null) currentPolyline.remove(); // remove old line

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    points.add(new LatLng(lat, lng));
                }

                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);
            }

            // draw on map
            if (lineOptions != null && mMap != null) {
                currentPolyline = mMap.addPolyline(lineOptions);
            }
        }
    }

    // 5. DirectionsJSONParser class inside AfterPaymentDoneFragment
    private class DirectionsJSONParser {
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
            List<List<HashMap<String, String>>> routes = new ArrayList<>();
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;

            try {
                jRoutes = jObject.getJSONArray("routes");

                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List<HashMap<String, String>> path = new ArrayList<>();

                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            for (LatLng latLng : list) {
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString(latLng.latitude));
                                hm.put("lng", Double.toString(latLng.longitude));
                                path.add(hm);
                            }
                        }
                    }
                    routes.add(path);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=driving";
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String apiKey = getActivity().getString(R.string.google_maps_key); // replace with your actual key
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + apiKey;
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
        common.getWalletAmount(getActivity(), new WalletCallBack() {
            @Override
            public void onWalletAmountReceived(int walletAmount) {
                wallet_amount=walletAmount;
            }

            @Override
            public void onError(String error) {

            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        }

        mapFragment.getMapAsync(this);
        return binding.getRoot();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Disable all gestures
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        // Disable marker clicks
        mMap.setOnMarkerClickListener(marker -> true);
        if (pickupLatLng != null && destinationLatLng != null) {
            updateMapMarkers();
        }
    }

    private void updateMapMarkers() {
           // check this aftervreal updation mMap.clear(); to off blink comment this
       // mMap.clear();

        // Add pickup marker with custom icon
        mMap.addMarker(new MarkerOptions()
                .position(pickupLatLng)
                .title("Pickup")
                .icon(resizeMapIcon(R.drawable.ic_local, 120, 110)));

        // Add destination marker with custom icon
        mMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .title("Destination")
                .icon(resizeMapIcon(R.drawable.ic_pickup_location, 100, 100)));

        // Adjust camera to show both points
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pickupLatLng);
        builder.include(destinationLatLng);
        LatLngBounds bounds = builder.build();

        int padding = 100; // pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);

        // 👉 Ensure minimum zoom level so map doesn't look too far
        float currentZoom = mMap.getCameraPosition().zoom;
        float minZoom = 13.5f; // Adjust as needed (13-15 is street level)

        if (currentZoom < minZoom) {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(minZoom));
        }
    }

    private BitmapDescriptor resizeMapIcon(int drawableRes, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), drawableRes);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
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
                        TextView tvpick=binding.commonAddress.findViewById(R.id.tv_pick);
                        EditText et_des=binding.commonAddress.findViewById(R.id.tv_desctination);
                        et_des.setEnabled(false);
                        et_des.setFocusable(false);
                        tvpick.setText(resp.getRecordList().getPickup());
                        et_des.setText(resp.getRecordList().getDestination());
                        pickupLatLng = new LatLng(Double.parseDouble(resp.getRecordList().getPickupLat()), Double.parseDouble(resp.getRecordList().getPickupLng()));
                        destinationLatLng = new LatLng(Double.parseDouble(resp.getRecordList().getDestinationLat()), Double.parseDouble(resp.getRecordList().getDestinationLng()));

                        binding.commonAddress.findViewById(R.id.tv_desctination).setEnabled(false);

                        String payment_mode="cash";
                        showPaymentMode(payment_mode);

                        binding.tvBookinhgDate.setText(getActivity().getString(R.string.booking_date)+" "+common.changeDateFormate(resp.getRecordList().getCreated_at()));
                        binding.tvReturnDate.setText(getActivity().getString(R.string.return_date)+" "+resp.getRecordList().getReturnDate());
                        binding.tvOtp.setText(getActivity().getString(R.string.otp)+" "+resp.getRecordList().getPickupOtp());
                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getProfileImage()).
                                placeholder(R.drawable.logo).error(R.drawable.logo).into(binding.ivRimg);
                        binding.tvRidername.setText(resp.getRecordList().getName());
                        binding.tvNum.setText(resp.getRecordList().getContactNo());
                        driver_Number=resp.getRecordList().getContactNo();
                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getVehicleTypeImage()).
                                placeholder(R.drawable.logo).error(R.drawable.logo).into(binding.ivVimg);
                        binding.tvVname.setText(resp.getRecordList().getVehicleModelName());
//                        binding.tvVnum.setText(resp.getRecordList().getv());
                        if(!common.checkNullString(String.valueOf(resp.getRecordList().getSeats())).equalsIgnoreCase("")){
                            binding.tvVdesc.setText("(" +resp.getRecordList().getVehicleColor()+" | "+String.valueOf(resp.getRecordList().getSeats())+" Seater ) ");
                        }
                        else{
                            binding.tvVdesc.setText("(" +resp.getRecordList().getVehicleColor()+")");
                        }
                        binding.tvPrice.setText("Rs. "+resp.getRecordList().getAmount());
                        amount_pay=resp.getRecordList().getAmount();
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

    private void showPaymentMode(String payment_mode) {
        if(payment_mode.equalsIgnoreCase("upi")){
            binding.rdUpi.setChecked(true);
        }
        else if(payment_mode.equalsIgnoreCase("credit card")){
            binding.rdCredit.setChecked(true);
        }else if(payment_mode.equalsIgnoreCase("debit card")){
            binding.rdDebit.setChecked(true);
        }else if(payment_mode.equalsIgnoreCase("cash")){
            binding.rdCash.setChecked(true);
        }else if(payment_mode.equalsIgnoreCase("wallet")){
            binding.rdWallet.setChecked(true);
        }
    }

    private void allClick() {
        binding.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.calling(driver_Number);
            }
        });
        binding.btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.callCancleDialog(getActivity(),tripId);
            }
        });
        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
                {
                    int selectedId = binding.rdGrp.getCheckedRadioButtonId();

                    if (selectedId == -1) {
                        common.errorToast("Please select a payment method");
                    } else {
                        RadioButton selectedRadio = binding.getRoot().findViewById(selectedId);
                        String selectedText = selectedRadio.getText().toString();
                        String paymentStatus="";
                        if(selectedText.equalsIgnoreCase("cash")||
                                selectedText.equalsIgnoreCase("wallet")){
                            paymentStatus="Success";
                        }
                        else{
                            paymentStatus="";
                        }
                        if(selectedText.equalsIgnoreCase("wallet")){
                            if(Double.parseDouble(String.valueOf(wallet_amount))<Double.parseDouble(amount_pay)){
                                common.errorToast("Insufficient amount");
                            }
                            else {
                                callPayment(selectedText, paymentStatus);
                            }
                        }
                        else {
                            callPayment(selectedText, paymentStatus);
                        }
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
                    if (resp.getStatus()==200) {
                        common.successToast(resp.getMessage());

                        //getDetailApi();
                        common.popFragment();


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
    private void initView() {
        binding.commonAddress.findViewById(R.id.iv_pick).setVisibility(View.GONE);
        binding.commonAddress.findViewById(R.id.iv_destination).setVisibility(View.GONE);
        ((MapActivity)getActivity()).showCommonPickDestinationArea(true,false);
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
        repository=new Repository(getActivity());
        tripId=getArguments().getString("tripId");
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
                    DriverLocationResp resp = (DriverLocationResp) data;
                    Log.e("driverLocation ",data.toString());
                    if (resp.getStatus()==200) {
//                        ((MapActivity)getActivity()).setDriverLocation(resp.getRecordList().getLat(),
//                                resp.getRecordList().getLng());
                        String status="";//started,running
                        if(!status.equalsIgnoreCase("started")||!status.equalsIgnoreCase("running")) {
                            //car-pickup,user -destination(trip not started)
                            setLocations(Double.parseDouble(resp.getRecordList().getLat()), Double.parseDouble(resp.getRecordList().getLng()),
                                    pickupLatLng.latitude,pickupLatLng.longitude );
                        }
                        else{
                            //car-pickup,actual destination-destination(trip started)
                            setLocations(Double.parseDouble(resp.getRecordList().getLat()), Double.parseDouble(resp.getRecordList().getLng()),
                                    destinationLatLng.latitude,destinationLatLng.longitude );
                        }


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