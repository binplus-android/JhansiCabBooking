package com.cabbooking.fragement;

import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;
import static com.cabbooking.utils.SessionManagment.KEY_ID;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cabbooking.R;
import com.cabbooking.Response.BookingDetailResp;
import com.cabbooking.Response.TripDetailRes;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentBookingDetailBinding;
import com.cabbooking.databinding.FragmentBookingHistoryBinding;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingDetailFragment extends Fragment {
    FragmentBookingDetailBinding binding;
    Common common;
    SessionManagment sessionManagment;
    String book_id="";
    Repository repository;

    public BookingDetailFragment() {
        // Required empty public constructor
    }


    public static BookingDetailFragment newInstance(String param1, String param2) {
        BookingDetailFragment fragment = new BookingDetailFragment();
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
        binding = FragmentBookingDetailBinding.inflate(inflater, container, false);
        initView();
        getAllData();
        allClick();
        return binding.getRoot();
    }

    private void getAllData()
    {
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        object.addProperty("bookId",book_id);
        repository.getBookingDetail(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    BookingDetailResp resp = (BookingDetailResp) data;
                    Log.e("BookingDetail ",data.toString());
                    if (resp.getStatus()==200) {



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

    private void allClick() {
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.etFeed.getText().toString().equals("")){
                    common.errorToast("Feedback required");
                }
                else {
                    binding.etAfterfeed.setText(binding.etFeed.getText().toString());
                    common.successToast("Feedback submitted successfully.");
                    binding.etFeed.setText("");
                    binding.linFill.setVisibility(View.GONE);
                    binding.linData.setVisibility(View.VISIBLE);
                }


            }
        });
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.etAfterfeed.getText().equals("")){
                    common.errorToast("Feedback required");
                }
                else {
                    binding.btnEdit.setVisibility(View.GONE);
                    common.successToast("Feedback edit successfully.");
                    binding.linFill.setVisibility(View.GONE);
                    binding.linData.setVisibility(View.VISIBLE);
                }


            }
        });

        binding.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnEdit.setVisibility(View.VISIBLE);
                binding.etAfterfeed.setEnabled(true);
                binding.etAfterfeed.setFocusable(true);
                binding.etAfterfeed.setFocusableInTouchMode(true);
                binding.etAfterfeed.setClickable(true);
                binding.etAfterfeed.setCursorVisible(true);
                binding.etAfterfeed.requestFocus();
            }
        });  binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              callConfirmationDialog();
            }
        });
    }

    private void callConfirmationDialog(){
         Dialog dialog;

        dialog = new Dialog (getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView (R.layout.dialog_delete_confirm);
        Button btn_no,btn_yes;
        TextView tv_message=dialog.findViewById(R.id.tv_message);
        tv_message.setText(getActivity().getString(R.string.are_you_sure_you_want_to_delete_feedbck));
        btn_yes=dialog.findViewById (R.id.btn_yes);
        btn_no=dialog.findViewById (R.id.btn_no);

        btn_no.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                dialog.dismiss ();
            }
        });

        btn_yes.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                common.successToast("Feedback deleted successfully.");
                binding.linFill.setVisibility(View.VISIBLE);
                binding.linData.setVisibility(View.GONE);

            }
        });
        dialog.setCanceledOnTouchOutside (false);
        dialog.show ();


    }

    private void initView() {
        repository=new Repository(getActivity());
        ((MapActivity)getActivity()).setTitle("#ID 12345\n20-09-2024 | 09:30 PM");
        //((MapActivity)getActivity()).setTitleWithSize("#ID 12345\n20-09-2024 | 09:30 PM",11);
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
    }
}
