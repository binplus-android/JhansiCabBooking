package com.cabbooking.fragement;




import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.DestinationAdapter;
import com.cabbooking.databinding.FragmentDestinationBinding;
import com.cabbooking.model.DestinationModel;
import com.cabbooking.utils.Common;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationFragment extends Fragment {
    FragmentDestinationBinding binding;
    ArrayList<DestinationModel> list;
    DestinationAdapter adapter;
    Common common;
    PlacesClient placesClient ;
    EditText tvDestination;
    private String lastValidAddress = "";
    private boolean userSelectedFromList = false;

    public DestinationFragment() {
        // Required empty public constructor
    }

    public static DestinationFragment newInstance(String param1, String param2) {
        DestinationFragment fragment = new DestinationFragment();
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
        tvDestination = getActivity().findViewById(R.id.tv_desctination);
        getDestinatioList();
        lastValidAddress=tvDestination.getText().toString();


        if (tvDestination != null) {
            if(!tvDestination.getText().toString().isEmpty()){
                binding.recDestination.setVisibility(View.VISIBLE);
                fetchAutocompleteSuggestions(tvDestination.getText().toString());
            }
            tvDestination.addTextChangedListener(new TextWatcher() {
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
        tvDestination = getActivity().findViewById(R.id.tv_desctination);
        // Restore last valid address if user didn't select from list
        if (!userSelectedFromList) {
            tvDestination.setText(lastValidAddress);
            tvDestination.setSelection(lastValidAddress.length());
        }
    }
    public void fetchAutocompleteSuggestions(String query) {
        list.clear();
        if (!query.isEmpty()) {
            LatLng jhansiCenter = new LatLng(25.4484, 78.5685);

// 50 km radius bounds (approx 0.45 degrees)
            RectangularBounds bounds = RectangularBounds.newInstance(
                    new LatLng(jhansiCenter.latitude - 0.45, jhansiCenter.longitude - 0.45),
                    new LatLng(jhansiCenter.latitude + 0.45, jhansiCenter.longitude + 0.45)
            );

            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setLocationRestriction(bounds) // ✅ Hard limit: only within 50 km box
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setCountry("IN")
                    .setQuery(query)
                    .build();


            placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener(response -> {


                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            String placeId = prediction.getPlaceId();
                            String mainAddress = prediction.getPrimaryText(null).toString();  // e.g., MP Nagar
                            String fullAddress = prediction.getFullText(null).toString();     // e.g., MP Nagar, Zone II, Bhopal

                            // prediction.getFullText(null).toString()
                            fetchPlaceDetails(placeId, mainAddress, fullAddress);  //

                            // list.add(new nearAreaNameModel(mainAddress, 0, 0, fullAddress));
                        }
                        adapter.notifyDataSetChanged();

                    })
                    .addOnFailureListener(e -> Log.e("Places", "Autocomplete error", e));
        }
        else {
           // clearList();
        }
    }

    private void fetchPlaceDetails(String placeId, String mainAddress, String fullAddress) {
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.LAT_LNG
        );

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

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
                                    list.add(new DestinationModel(mainAddress, lat, lng, fullAddress + "  " + postalCode));
                                    adapter.notifyDataSetChanged();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Places", "Place details error", e));
    }


    public void getDestinatioList() {
        Log.d("hjhfjy", "getDestinatioList: "+list.size());
        // ArrayList<nearAreaNameModel>list=list;
        //if(list.size()>1){
        adapter=new DestinationAdapter(getActivity(), list, new DestinationAdapter.onTouchMethod() {
            @Override
            public void onSelection(int pos) {
                String selected = list.get(pos).getFormatted_address();
                userSelectedFromList = true;

                lastValidAddress = selected;
                LatLng latLng = new LatLng(list.get(pos).getLat(),  list.get(pos).getLng());
                ((MapActivity)getActivity()).getDestinationLatLng(list.get(pos).getLat(),
                        list.get(pos).getLng(),list.get(pos).getFormatted_address(), latLng);
                tvDestination = getActivity().findViewById(R.id.tv_desctination);
                tvDestination.setText(selected);
                common.switchFragment(new VechileFragment());

            }
        });
        binding.recDestination.setAdapter(adapter);

  //}

    }

    public void initView() {
        common=new Common(getActivity());
        ((MapActivity)getActivity()).setTitle("Destination");
        ((MapActivity)getActivity()).showCommonPickDestinationArea(true,true);
        list=new ArrayList<>();
        binding.recDestination.setLayoutManager(new LinearLayoutManager(getActivity()));


    }
}