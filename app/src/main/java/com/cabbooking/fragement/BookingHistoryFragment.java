package com.cabbooking.fragement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.BookingAdapter;
import com.cabbooking.adapter.WalletHistoryAdapter;
import com.cabbooking.databinding.FragmentBookingHistoryBinding;
import com.cabbooking.databinding.FragmentWalletHistoryBinding;
import com.cabbooking.model.BookingHistoryModel;
import com.cabbooking.model.WalletHistoryModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.SessionManagment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingHistoryFragment extends Fragment {

    FragmentBookingHistoryBinding binding;
    Common common;
    SessionManagment sessionManagment;
    BookingAdapter adapter;
    ArrayList<BookingHistoryModel> list;

    public BookingHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingHistoryFragment newInstance(String param1, String param2) {
        BookingHistoryFragment fragment = new BookingHistoryFragment();
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
        binding = FragmentBookingHistoryBinding.inflate(inflater, container, false);
        initView();
        allClick();
        getList();

        return binding.getRoot();
    }

    private void getList() {
        list.clear();
        list.add(new BookingHistoryModel());
        list.add(new BookingHistoryModel());
        list.add(new BookingHistoryModel());
        adapter=new BookingAdapter(getActivity(), list, new BookingAdapter.onTouchMethod() {
            @Override
            public void onSelection(int pos) {
                common.switchFragment(new BookingDetailFragment());
            }
        });
        binding.recList.setAdapter(adapter);

    }

    private void allClick() {
    }

    private void initView() {
        ((MapActivity)getActivity()).setTitle("History");
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        list=new ArrayList<>();
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
    }
}