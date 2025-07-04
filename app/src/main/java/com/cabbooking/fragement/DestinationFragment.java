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
import android.text.TextUtils;
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
import com.cabbooking.model.TempBound;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.SessionManagment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
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
 * Use the {@link DestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationFragment extends Fragment {
    FragmentDestinationBinding binding;
    ArrayList<DestinationModel> list;
    DestinationAdapter adapter;
    Common common;
    SessionManagment sessionManagment;
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
        adapter.notifyDataSetChanged();

        if (TextUtils.isEmpty(query)) return;

        String boundsJson = sessionManagment.getValue("BOUND_LIST");
        if (boundsJson == null) return;

        Type listType = new TypeToken<List<TempBound>>() {}.getType();
        List<TempBound> boundList = new Gson().fromJson(boundsJson, listType);
        if (boundList == null || boundList.isEmpty()) return;

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        for (TempBound bound : boundList) {
            LatLng southwest = new LatLng(bound.getSlat(), bound.getSlng());
            LatLng northeast = new LatLng(bound.getNlat(), bound.getNlon());
            RectangularBounds bounds = RectangularBounds.newInstance(southwest, northeast);

            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setSessionToken(token)
                    .setLocationRestriction(bounds)
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setCountry("IN")
                    .setQuery(query)
                    .build();

            placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener(response -> {
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            String placeId = prediction.getPlaceId();
                            String mainAddress = prediction.getPrimaryText(null).toString();
                            String fullAddress = prediction.getFullText(null).toString();

                            fetchPlaceDetails(placeId, mainAddress, fullAddress, boundList); // 👈 pass bounds here
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Places", "Autocomplete error", e));
        }
    }



    private void fetchPlaceDetails(String placeId, String mainAddress, String fullAddress, List<TempBound> boundList) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request)
                .addOnSuccessListener(response -> {
                    Place place = response.getPlace();
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        double lat = latLng.latitude;
                        double lng = latLng.longitude;

                        if (isInsideAnyBound(lat, lng, boundList)) {  // ✅ Only add if within bounds
                            if (isAdded() && getContext() != null) {
                                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                                    if (addresses != null && !addresses.isEmpty()) {
                                        String postalCode = addresses.get(0).getPostalCode();
                                        list.add(new DestinationModel(mainAddress, lat, lng, fullAddress + "  " + postalCode));
                                        adapter.notifyDataSetChanged();
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Places", "Place details error", e));
    }

    private boolean isInsideAnyBound(double lat, double lng, List<TempBound> bounds) {
        for (TempBound bound : bounds) {
            double minLat = Math.min(bound.getSlat(), bound.getNlat());
            double maxLat = Math.max(bound.getSlat(), bound.getNlat());
            double minLng = Math.min(bound.getSlng(), bound.getNlon());
            double maxLng = Math.max(bound.getSlng(), bound.getNlon());

            if (lat >= minLat && lat <= maxLat && lng >= minLng && lng <= maxLng) {
                return true;
            }
        }
        return false;
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
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
        ((MapActivity)getActivity()).setTitle("Destination");
        ((MapActivity)getActivity()).showCommonPickDestinationArea(true,true);
        list=new ArrayList<>();
        binding.recDestination.setLayoutManager(new LinearLayoutManager(getActivity()));


    }
}