package com.cabbooking.fragement;

import static com.cabbooking.utils.RetrofitClient.BASE_URL;
import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;
import static com.cabbooking.utils.SessionManagment.KEY_ID;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.cabbooking.Response.CommonResp;
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
    String book_id="",book_date="",tripId="";
    Repository repository;
    JsonObject feedobject=new JsonObject();

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
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefresh.setRefreshing(false);
                getAllData();
            }
        });
        return binding.getRoot();
    }

    private void getAllData()
    {   disbaleEditText();
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        object.addProperty("tripId",book_id);
        repository.getBookingDetail(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    BookingDetailResp resp = (BookingDetailResp) data;
                    Log.e("BookingDetail ",data.toString());
                    if (resp.getStatus()==200) {
                        if(resp.getRecordList().getTripStatus().equalsIgnoreCase("scheduled")||
                           resp.getRecordList().getTripStatus().equalsIgnoreCase("running")){
                           binding.linTrack.setVisibility(View.VISIBLE);
                           binding.relPay.setVisibility(View.GONE);
                           binding.linData.setVisibility(View.GONE);
                           binding.linFill.setVisibility(View.GONE);
                         //  binding.linInvoice.setVisibility(View.GONE);

                        }
                        else{
                            binding.linInvoice.setVisibility(View.VISIBLE);
                            binding.linTrack.setVisibility(View.GONE);
                            binding.relPay.setVisibility(View.VISIBLE);
                            if(common.checkNullString(resp.getRecordList().getUserFeedback()).equalsIgnoreCase("")){
                                binding.linFill.setVisibility(View.VISIBLE);
                                binding.etFeed.setText("");
                                binding.etAfterfeed.setText("");
                                binding.linData.setVisibility(View.GONE);
                            } else {
                                binding.etFeed.setText(resp.getRecordList().getUserFeedback());
                                binding.etAfterfeed.setText(resp.getRecordList().getUserFeedback());
                                binding.linFill.setVisibility(View.GONE);
                                binding.linData.setVisibility(View.VISIBLE);
                            }
                        }
                        tripId= String.valueOf(resp.getRecordList().getId());

                        Picasso.get().load(IMAGE_BASE_URL + resp.getRecordList().getVehicleImage()).placeholder(R.drawable.logo).
                                error(R.drawable.logo).into(binding.vImg);
                        Picasso.get().load(IMAGE_BASE_URL + resp.getRecordList().getProfileImage()).placeholder(R.drawable.logo).
                                error(R.drawable.logo).into(binding.dImg);

                        binding.tvVnum.setText(resp.getRecordList().getVehicleNumber());
                        binding.tvDname.setText(resp.getRecordList().getName());
                        binding.tvVname.setText(resp.getRecordList().getVehicleModelName()+"("+
                                resp.getRecordList().getVehicleColor()+")");
                        binding.tvStatus.setText(resp.getRecordList().getTripStatus());

                        binding.tvAmt.setText("-Rs."+resp.getRecordList().getAmount());
                        binding.tvDist.setText("Distance: "+resp.getRecordList().getDistance()+"KM");
                        binding.tvPayment.setText("Payment by "+resp.getRecordList().getPaymentMode());

                        binding.tvPick.setText(resp.getRecordList().getPickup());
                        binding.tvDesctination.setText(resp.getRecordList().getDestination());



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
        binding.tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.callCancleDialog(getActivity(),tripId);
            }
        });binding.tvTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new AfterPaymentDoneFragment();
                Bundle bundle=new Bundle();
                bundle.putString("tripId",tripId);
                fragment.setArguments(bundle);
                common.switchFragment(fragment);
            }
        });
        binding.linInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                  //  String pdfUrl = BASE_URL+model.getReceipt_url() ;
                    String pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf" ;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
                    getActivity().startActivity(browserIntent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.etFeed.getText().toString().equals("")){
                    common.errorToast("Feedback required");
                }
                else {
                    feedobject.addProperty("feedback",binding.etFeed.getText().toString());
                    feedBack(feedobject);
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
                    feedobject.addProperty("feedback",binding.etAfterfeed.getText().toString());
                    feedBack(feedobject);
                }


            }
        });

        binding.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnEdit.setVisibility(View.VISIBLE);
               enableEditText();
            }
        });  binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              callConfirmationDialog();
            }
        });
    }

    private void enableEditText() {
        binding.etAfterfeed.setEnabled(true);
        binding.etAfterfeed.setFocusable(true);
        binding.etAfterfeed.setFocusableInTouchMode(true);
        binding.etAfterfeed.setClickable(true);
        binding.etAfterfeed.setCursorVisible(true);
        binding.etAfterfeed.requestFocus();
    }
    private void disbaleEditText() {
        binding.etAfterfeed.setEnabled(false);
        binding.etAfterfeed.setFocusable(false);
        binding.etAfterfeed.setFocusableInTouchMode(false);
        binding.etAfterfeed.setClickable(false);
        binding.etAfterfeed.setCursorVisible(false);
        binding.etAfterfeed.requestFocus();
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
                feedobject.addProperty("feedback","");
                feedBack(feedobject);
            }
        });
        dialog.setCanceledOnTouchOutside (false);
        dialog.show ();


    }

    private void initView() {
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
        book_id=getArguments().getString("book_id");
        book_date=getArguments().getString("book_date");
        String[] date=book_date.split(" ");
        String date_val=date[0]+" | "+common.convertToAmPm(date[1]);
        repository=new Repository(getActivity());
        ((MapActivity)getActivity()).setTitle("#ID "+book_id+"\n"+date_val);
        //((MapActivity)getActivity()).setTitleWithSize("#ID 12345\n20-09-2024 | 09:30 PM",11);
    }

    private void feedBack(JsonObject feedobject)
    {

        feedobject.addProperty("userId", sessionManagment.getUserDetails().get(KEY_ID));
        feedobject.addProperty("tripId", tripId);
        repository.feedBack(feedobject, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    CommonResp resp = (CommonResp) data;
                    Log.e("feedback ", data.toString());
                    if (resp.getStatus() == 200) {
                        common.successToast(resp.getMessage());
                        getAllData();

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
}
