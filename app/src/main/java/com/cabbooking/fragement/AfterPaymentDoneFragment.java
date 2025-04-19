package com.cabbooking.fragement;

import static com.cabbooking.utils.SessionManagment.KEY_OUTSTATION_TYPE;
import static com.cabbooking.utils.SessionManagment.KEY_TYPE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
import com.cabbooking.databinding.FragmentAfterPaymentDoneBinding;
import com.cabbooking.databinding.FragmentPaymentBinding;
import com.cabbooking.utils.SessionManagment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AfterPaymentDoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfterPaymentDoneFragment extends Fragment {

    String trip_type="",outstation_type="";
    SessionManagment sessionManagment;
    FragmentAfterPaymentDoneBinding binding;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAfterPaymentDoneBinding.inflate(inflater, container, false);
        initView();
        allClick();

        return binding.getRoot();
    }

    private void allClick() {
    }

    private void initView() {
        sessionManagment=new SessionManagment(getActivity());
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

}