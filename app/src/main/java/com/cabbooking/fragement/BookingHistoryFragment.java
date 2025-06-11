package com.cabbooking.fragement;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.cabbooking.utils.SessionManagment.KEY_ID;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
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
import com.cabbooking.utils.LoadingBar;
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
    LoadingBar progressbar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    SessionManagment sessionManagment;
    BookingAdapter adapter;
    ArrayList<BookingHistoryModel.RecordList> list;
    Repository repository;
    private int startIndex = 0;
    private final int fetchRecord = 15;




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

        binding.swipeRefresh.setOnRefreshListener(() -> {
            startIndex = 0;
            isLastPage = false;
            list.clear();
            adapter.notifyDataSetChanged();
            getList();
            binding.swipeRefresh.setRefreshing(false);
        });
        // Global flag to prevent multiple loads



            binding.recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) { // only when scrolling down
                        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recList.getLayoutManager();
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                        if (!isLoading && !isLastPage && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            isLoading = true;
                            startIndex += 15;
                            getList(); // Load next page
                        }
                    }
                }
            });



        return binding.getRoot();
    }

    private void getList() {
        if (startIndex == 0) {
            showHideProgressBar(true); // only first time
        }
        JsonObject object = new JsonObject();
        object.addProperty("userId", sessionManagment.getUserDetails().get(KEY_ID));
        object.addProperty("startIndex", startIndex);
        object.addProperty("fetchRecord", fetchRecord);

        repository.getBookingHistory(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    showHideProgressBar(false);
                    isLoading = false;
                    BookingHistoryModel resp = (BookingHistoryModel) data;
                    Log.e("BookingHistoryModel ", data.toString());

                    if (resp.getStatus() == 200) {
                        ArrayList<BookingHistoryModel.RecordList> newList = resp.getRecordList();

                        if (newList != null && newList.size() > 0) {
                            if (startIndex == 0) {
                                list.clear(); // Clear only for the first load
                                list.addAll(newList);

                                adapter = new BookingAdapter(getActivity(), list, new BookingAdapter.onTouchMethod() {
                                    @Override
                                    public void onSelection(int pos) {
                                        Fragment fm = new BookingDetailFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("book_id", String.valueOf(list.get(pos).getTripId()));
                                        bundle.putString("book_date", String.valueOf(list.get(pos).getCreatedAt()));
                                        fm.setArguments(bundle);
                                        common.switchFragment(fm);
                                    }
                                });

                                binding.recList.setVisibility(View.VISIBLE);
                                binding.layNoadata.setVisibility(View.GONE);
                                binding.recList.setAdapter(adapter);
                            } else {
                                list.addAll(newList);
                                adapter.notifyDataSetChanged();
                            }

                            startIndex += newList.size(); // increment for next page
                        } else {
                            isLastPage = true;
                            if (startIndex == 0) {
                                binding.recList.setVisibility(View.GONE);
                                binding.layNoadata.setVisibility(View.VISIBLE);
                            }
                            // Else: no more data to load, don't do anything
                        }

                    } else {
                        showHideProgressBar(false);
                        isLoading = false;
                        common.errorToast(resp.getError());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServerError(String errorMsg) {
                Log.e("errorMsg", errorMsg);
            }
        }, true);
    }




    private void allClick() {

    }



    private void initView() {
        ((MapActivity)getActivity()).showCommonPickDestinationArea(false,false);
        repository=new Repository(getActivity());
        ((MapActivity)getActivity()).setTitle("Booking History");
//        binding.recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);

        binding.recList.setLayoutManager(layoutManager);
        list=new ArrayList<>();
        progressbar=new LoadingBar(getActivity());
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
    }
    private void showHideProgressBar(boolean showStatus) {
        if (showStatus) {
            progressbar.show();
        } else {
            progressbar.dismiss();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        startIndex = 0;
        list.clear();
        getList();
    }

}