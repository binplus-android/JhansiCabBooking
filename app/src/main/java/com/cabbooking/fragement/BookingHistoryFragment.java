package com.cabbooking.fragement;

import static com.cabbooking.utils.SessionManagment.KEY_ID;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabbooking.Response.CommonResp;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.adapter.BookingAdapter;
import com.cabbooking.adapter.WalletHistoryAdapter;
import com.cabbooking.databinding.FragmentBookingHistoryBinding;
import com.cabbooking.model.BookingHistoryModel;
import com.cabbooking.model.WalletHistoryModel;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.firebase.database.core.Repo;
import com.google.gson.JsonObject;

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
    ArrayList<BookingHistoryModel.RecordList> list;
    Repository repository;


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

    private void getList()
    {
            list.clear();
            JsonObject object = new JsonObject();
        object.addProperty("userId", sessionManagment.getUserDetails().get(KEY_ID));
        repository.getBookingHistory(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    BookingHistoryModel resp = (BookingHistoryModel) data;
                    Log.e("BookingHistoryModel ", data.toString());
                    if (resp.getStatus() == 200) {
                        list.clear();
                        list = resp.getRecordList();
                        if(list.size()>0) {
                            adapter=new BookingAdapter(getActivity(), list, new BookingAdapter.onTouchMethod() {
                                @Override
                                public void onSelection(int pos) {
                                    Fragment fm=new BookingDetailFragment();
                                    Bundle bundle=new Bundle();
                                    bundle.putString("book_id",String.valueOf(list.get(pos).getTripId()));
                                    bundle.putString("book_date",String.valueOf(list.get(pos).getCreatedAt()));

                                    fm.setArguments(bundle);
                                    common.switchFragment(fm);
                                }
                            });
                            binding.recList.setVisibility(View.VISIBLE);
                            binding.layNoadata.setVisibility(View.GONE);
                            binding.recList.setAdapter(adapter);
                        }
                        else {
                            binding.recList.setVisibility(View.GONE);
                            binding.layNoadata.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
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
            }, true);

        }


    private void allClick() {
    }

    private void initView() {
        repository=new Repository(getActivity());
        ((MapActivity)getActivity()).setTitle("Booking History");
        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        list=new ArrayList<>();
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
    }
}