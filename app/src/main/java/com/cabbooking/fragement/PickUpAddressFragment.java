package com.cabbooking.fragement;




import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

    /* =============================  MAIN METHOD  ============================= */
    public void fetchAutocompleteSuggestions(String query) {

        list1.clear();
        adapter.notifyDataSetChanged();

        if (TextUtils.isEmpty(query)) return;

        // 1. bounds JSON से TempBound list निकालो
        String boundsJson = sessionManagment.getValue("BOUND_LIST");
        if (boundsJson == null) return;

        Type listType = new TypeToken<List<TempBound>>(){}.getType();
        List<TempBound> boundList = new Gson().fromJson(boundsJson, listType);
        if (boundList == null || boundList.isEmpty()) return;

        // 2. Google‑Places का session‑token (better quota usage)
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        // 3. Duplicate place‑ids रोकने के लिए set
        Set<String> seenPlaceIds = Collections.synchronizedSet(new HashSet<>());

        for (TempBound bound : boundList) {

            RectangularBounds bounds = RectangularBounds.newInstance(
                    new LatLng(bound.getSlat(), bound.getSlng()),
                    new LatLng(bound.getNlat(), bound.getNlon())
            );

            FindAutocompletePredictionsRequest request =
                    FindAutocompletePredictionsRequest.builder()
                            .setSessionToken(token)
                            .setLocationRestriction(bounds)
                            .setTypeFilter(TypeFilter.ADDRESS)
                            .setCountry("IN")
                            .setQuery(query)
                            .build();

            placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener(response -> {
                        for (AutocompletePrediction prediction :
                                response.getAutocompletePredictions()) {

                            // सिर्फ नई जगहें आगे भेजो
                            if (seenPlaceIds.add(prediction.getPlaceId())) {
                                fetchPlaceDetails(
                                        prediction.getPlaceId(),
                                        prediction.getPrimaryText(null).toString(),
                                        prediction.getFullText(null).toString(),
                                        boundList        // ⬅ bounds पास करो
                                );
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Log.e("Places", "Autocomplete error", e));
        }
    }

    /* =======================  PLACE‑DETAILS + BOUNDS‑FILTER  ======================= */
    private void fetchPlaceDetails(String placeId,
                                   String mainAddress,
                                   String fullAddress,
                                   List<TempBound> boundList) {

        List<Place.Field> fields = Collections.singletonList(Place.Field.LAT_LNG);
        FetchPlaceRequest req = FetchPlaceRequest.builder(placeId, fields).build();

        placesClient.fetchPlace(req)
                .addOnSuccessListener(resp -> {
                    LatLng ll = resp.getPlace().getLatLng();
                    if (ll == null) return;

                    double lat = ll.latitude;
                    double lng = ll.longitude;

                    /* 🔒  CLIENT‑SIDE BOUNDS CHECK  */
                    if (!isInsideAnyBound(lat, lng, boundList)) {
                        return;   // बाहर है ⇒ ignore
                    }

                    // Geocoder से pin‑code वग़ैरह
                    try {
                        if (isAdded() && getContext() != null) {
                            Geocoder g = new Geocoder(getActivity(), Locale.getDefault());
                            List<Address> adds = g.getFromLocation(lat, lng, 1);
                            String pin = (!adds.isEmpty()) ? adds.get(0).getPostalCode() : "";

                            list1.add(new PickupAdressModel(
                                    mainAddress,
                                    lat,
                                    lng,
                                    fullAddress + "  " + pin));

                            adapter.notifyDataSetChanged();
                        }
                    } catch (IOException io) {
                        Log.e("Places", "Geocoder error", io);
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("Places", "Place details error", e));
    }

    /* =========================  POINT‑IN‑RECT TEST  ========================= */
    private boolean isInsideAnyBound(double lat, double lng, List<TempBound> bounds) {
        for (TempBound b : bounds) {
            double minLat = Math.min(b.getSlat(), b.getNlat());
            double maxLat = Math.max(b.getSlat(), b.getNlat());
            double minLng = Math.min(b.getSlng(), b.getNlon());
            double maxLng = Math.max(b.getSlng(), b.getNlon());

            if (lat >= minLat && lat <= maxLat && lng >= minLng && lng <= maxLng) {
                return true;
            }
        }
        return false;
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
                ((MapActivity)getActivity()).setHomeAddress(list1.get(pos).getFormatted_address().toString());
                ((MapActivity)getActivity()).getPickUpLatLng(list1.get(pos).getLat(),
                        list1.get(pos).getLng(),list1.get(pos).getFormatted_address(), latLng);

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