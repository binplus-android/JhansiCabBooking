package com.cabbooking.fragement;


import static com.cabbooking.activity.MapActivity.areaList;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.cabbooking.R;
import com.cabbooking.Response.LoginResp;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.DestinationAdapter;
import com.cabbooking.databinding.FragmentDestinationBinding;
import com.cabbooking.model.DestinationModel;
import com.cabbooking.model.nearAreaNameModel;
import com.cabbooking.utils.Common;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
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
    //ArrayList<DestinationModel> list;
    ArrayList<nearAreaNameModel>list1;
    DestinationAdapter adapter;
    Common common;
    PlacesClient placesClient ;

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
        getDestinatioList();

        EditText tvDestination = getActivity().findViewById(R.id.tv_desctination);
        Toast.makeText(getActivity(), "dest", Toast.LENGTH_SHORT).show();
        if (tvDestination != null) {
            Toast.makeText(getActivity(), "checkdest", Toast.LENGTH_SHORT).show();
            tvDestination.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty()) {
                        Toast.makeText(getActivity(), ""+s.toString(), Toast.LENGTH_SHORT).show();
                        fetchAutocompleteSuggestions(s.toString());
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
    public void clearList(){
        list1.clear();
        list1=areaList;
        adapter.notifyDataSetChanged();
    }
    public void fetchAutocompleteSuggestions(String query) {
        list1.clear();
        if (!query.isEmpty()) {
//            Double pickLat = ((MapActivity) getActivity()).getPickupLat();
//            Double pickLng = ((MapActivity) getActivity()).getPickupLng();
//            LatLng myLocation = new LatLng(pickLat, pickLng);
//
//            double lat = myLocation.latitude;
//            double lng = myLocation.longitude;
//            double delta = 0.3; // Approx. 30–40 km
//
//            RectangularBounds bounds = RectangularBounds.newInstance(
//                    new LatLng(lat - delta, lng - delta),
//                    new LatLng(lat + delta, lng + delta)
//            );
            RectangularBounds bounds = RectangularBounds.newInstance(
                    new LatLng(8.0, 68.0),    // Southwest corner of India
                    new LatLng(37.0, 97.0)    // Northeast corner of India
            );
            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setLocationRestriction(bounds)  // ✅ strict restriction, not bias
                    .setTypeFilter(TypeFilter.ADDRESS) // optional: only show addresses
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

                            // list1.add(new nearAreaNameModel(mainAddress, 0, 0, fullAddress));
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
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                String postalCode = addresses.get(0).getPostalCode();

                                // Step 3: Add to model
                                list1.add(new nearAreaNameModel(mainAddress, lat, lng, fullAddress + "  " + postalCode));
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), ""+list1.size(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        list1.add(new nearAreaNameModel(mainAddress, lat, lng, fullAddress));
//                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Log.e("Places", "Place details error", e));
    }


    public void getDestinatioList() {
        Log.d("hjhfjy", "getDestinatioList: "+list1.size());
        // ArrayList<nearAreaNameModel>list1=list1;
        adapter=new DestinationAdapter(getActivity(), list1, new DestinationAdapter.onTouchMethod() {
            @Override
            public void onSelection(int pos) {
                LatLng latLng = new LatLng(list1.get(pos).getLat(),  list1.get(pos).getLng());
                ((MapActivity)getActivity()).getDestinationLatLng(list1.get(pos).getLat(),
                        list1.get(pos).getLng(),list1.get(pos).getFormatted_address(), latLng);
                common.switchFragment(new VechileFragment());
            }
        });
        binding.recDestination.setAdapter(adapter);


    }

    public void initView() {
        common=new Common(getActivity());
        ((MapActivity)getActivity()).setTitle("Destination");
        ((MapActivity)getActivity()).showCommonPickDestinationArea(true,true);
        list1=new ArrayList<>();
        binding.recDestination.setLayoutManager(new LinearLayoutManager(getActivity()));


    }
}