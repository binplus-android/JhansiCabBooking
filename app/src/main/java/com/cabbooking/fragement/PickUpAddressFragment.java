package com.cabbooking.fragement;




import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.PickUpAddressAdapter;
import com.cabbooking.databinding.FragmentDestinationBinding;
import com.cabbooking.model.PickupAdressModel;
import com.cabbooking.model.TempBound;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.SessionManagment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PickUpAddressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickUpAddressFragment extends Fragment {
    FragmentDestinationBinding binding;
    SessionManagment sessionManagment;
    ArrayList<PickupAdressModel>list1;
    PickUpAddressAdapter adapter;
    Common common;
    PlacesClient placesClient ;
    EditText tv_pick ;
     String lastValidAddress = "";
     boolean userSelectedFromList = false;

    public PickUpAddressFragment() {
        // Required empty public constructor
    }

    public static PickUpAddressFragment newInstance(String param1, String param2) {
        PickUpAddressFragment fragment = new PickUpAddressFragment();
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
        //return inflater.inflate(R.layout.fragment_destination, container, false);
        binding = FragmentDestinationBinding.inflate(inflater, container, false);
        initView();

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), getString(R.string.google_maps_key), Locale.getDefault());
        }
        placesClient = Places.createClient(getActivity());
        tv_pick = getActivity().findViewById(R.id.tv_pick);
        getPIckUpList();
        lastValidAddress=tv_pick.getText().toString();
        if (tv_pick != null) {
            if(!tv_pick.getText().toString().isEmpty()){
                binding.recDestination.setVisibility(View.VISIBLE);
                fetchAutocompleteSuggestions(tv_pick.getText().toString());
            }
            tv_pick.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    userSelectedFromList = false;
                    if (!s.toString().isEmpty()) {
                        binding.recDestination.setVisibility(View.VISIBLE);
                        fetchAutocompleteSuggestions(s.toString());
                    }
                    else{
                        binding.recDestination.setVisibility(View.GONE);
                    }
//                    else {
//                        clearList();
//                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }

            });
        }
        return  binding.getRoot();
    }
    @Override
    public void onPause() {
        super.onPause();
        tv_pick = getActivity().findViewById(R.id.tv_pick);
        // Restore last valid address if user didn't select from list
        if (!userSelectedFromList) {
            tv_pick.setText(lastValidAddress);
            tv_pick.setSelection(lastValidAddress.length());
        }
    }

//    public void fetchAutocompleteSuggestions(String query) {
//        if (!query.isEmpty()) {
////            LatLng jhansiCenter = new LatLng(25.4484, 78.5685);
////
////// 10 km radius bounds (~0.09 degrees latitude/longitude)
////            RectangularBounds bounds = RectangularBounds.newInstance(
////                    new LatLng(jhansiCenter.latitude - 0.09, jhansiCenter.longitude - 0.09),
////                    new LatLng(jhansiCenter.latitude + 0.09, jhansiCenter.longitude + 0.09)
////            );
//
//            LatLng southwest = new LatLng(25.3900, 78.5000); // Bottom-left corner (SW)
//            LatLng northeast = new LatLng(25.5200, 78.6500); // Top-right corner (NE)
//            RectangularBounds bounds = RectangularBounds.newInstance(southwest, northeast);
//
//            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
//                    .setLocationRestriction(bounds) // ✅ Hard limit: only within 10 km box
//                    .setTypeFilter(TypeFilter.ADDRESS)
//                    .setCountry("IN")
//                    .setQuery(query)
//                    .build();
//
//
//            placesClient.findAutocompletePredictions(request)
//                    .addOnSuccessListener(response -> {
//                        list1.clear();
//
//                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
//                            String placeId = prediction.getPlaceId();
//                            String mainAddress = prediction.getPrimaryText(null).toString();  // e.g., MP Nagar
//                            String fullAddress = prediction.getFullText(null).toString();     // e.g., MP Nagar, Zone II, Bhopal
//
//                            // prediction.getFullText(null).toString()
//                            fetchPlaceDetails(placeId, mainAddress, fullAddress);  //
//
//                            // list1.add(new PickupAdressModel(mainAddress, 0, 0, fullAddress));
//                        }
//                        adapter.notifyDataSetChanged();
//                    })
//                    .addOnFailureListener(e -> Log.e("Places", "Autocomplete error", e));
//        }
//    }

    public void fetchAutocompleteSuggestions(String query) {
        list1.clear();
        adapter.notifyDataSetChanged();

        if (!query.isEmpty()) {
            String boundsJson = sessionManagment.getValue("BOUND_LIST");
            if (boundsJson == null) return;

            Type listType = new TypeToken<List<TempBound>>() {}.getType();
            List<TempBound> boundList = new Gson().fromJson(boundsJson, listType);

            for (TempBound bound : boundList) {
                LatLng southwest = new LatLng(bound.getSlat(), bound.getSlng());
                LatLng northeast = new LatLng(bound.getNlat(), bound.getNlon());
                RectangularBounds bounds = RectangularBounds.newInstance(southwest, northeast);

                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setLocationRestriction(bounds)
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setCountry("IN")
                        .setQuery(query)
                        .build();

                placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener(response -> {
                            list1.clear();
                            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                                String placeId = prediction.getPlaceId();
                                String mainAddress = prediction.getPrimaryText(null).toString();
                                String fullAddress = prediction.getFullText(null).toString();

                                fetchPlaceDetails(placeId, mainAddress, fullAddress);
                            }
                            adapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> Log.e("Places", "Autocomplete error", e));
            }
        }
    }

    private void fetchPlaceDetails(String placeId, String mainAddress, String fullAddress) {
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.LAT_LNG
        );

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
        try {
            placesClient.fetchPlace(request)
                    .addOnSuccessListener(response -> {


                        Place place = response.getPlace();
                        LatLng latLng = place.getLatLng();
                        if (latLng != null) {
                            double lat = latLng.latitude;
                            double lng = latLng.longitude;
                            Activity activity = getActivity();
                            if (activity != null) {
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                                    if (addresses != null && !addresses.isEmpty()) {
                                        String postalCode = addresses.get(0).getPostalCode();

                                        // Step 3: Add to model
                                        list1.add(new PickupAdressModel(mainAddress, lat, lng, fullAddress + "  " + postalCode));
                                        adapter.notifyDataSetChanged();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
//                        list1.add(new PickupAdressModel(mainAddress, lat, lng, fullAddress));
//                        adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Places", "Place details error", e));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void getPIckUpList() {
        Log.d("hjhfjy", "getPIckUpList: "+list1.size());
        // ArrayList<PickupAdressModel>list1=list1;
        adapter=new PickUpAddressAdapter(getActivity(), list1, new PickUpAddressAdapter.onTouchMethod() {
            @Override
            public void onSelection(int pos) {
                String selected = list1.get(pos).getFormatted_address();
                userSelectedFromList = true;

                lastValidAddress = selected;
                LatLng latLng = new LatLng(list1.get(pos).getLat(),  list1.get(pos).getLng());
                Log.d("gfgjj", "onSelection: "+list1.get(pos).getFormatted_address());
                ((MapActivity)getActivity()).getPickUpLatLng(list1.get(pos).getLat(),
                        list1.get(pos).getLng(),list1.get(pos).getFormatted_address(), latLng);
                ((MapActivity)getActivity()).setHomeAddress(list1.get(pos).getFormatted_address());
                tv_pick = getActivity().findViewById(R.id.tv_pick);
                tv_pick.setText(selected);
                if(((MapActivity)getActivity()).getDestinationLat()==0.0||((MapActivity)getActivity()).getDestinationLng()==0.0){
                    common.switchFragment(new HomeFragment());
                }
                else{
                    common.switchFragment(new VechileFragment());
              }
            }
        });
        binding.recDestination.setAdapter(adapter);


    }

    public void initView() {
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
        ((MapActivity)getActivity()).setTitle("Pickup");
        ((MapActivity)getActivity()).showCommonPickDestinationArea(true,true);
        list1=new ArrayList<>();
        binding.recDestination.setLayoutManager(new LinearLayoutManager(getActivity()));


    }
}