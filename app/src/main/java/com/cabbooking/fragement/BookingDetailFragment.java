package com.cabbooking.fragement;


import static android.content.Context.DOWNLOAD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.cabbooking.utils.RetrofitClient.BASE_URL;
import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;
import static com.cabbooking.utils.SessionManagment.KEY_ID;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cabbooking.R;
import com.cabbooking.Response.BookingDetailResp;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.TripDetailRes;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentBookingDetailBinding;
import com.cabbooking.databinding.FragmentBookingHistoryBinding;
import com.cabbooking.model.BookingHistoryModel;
import com.cabbooking.utils.Apis;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.LoadingBar;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.RetrofitClient;
import com.cabbooking.utils.SessionManagment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingDetailFragment extends Fragment implements OnMapReadyCallback {
    FragmentBookingDetailBinding binding;
    private String pendingFileUrl;
    String trip_type = "", outstation_type = "";
    String pdfUrl = "";
    String lastDownloadedFilePath = "";
    Common common;
    SessionManagment sessionManagment;
    String book_id = "", book_date = "", tripId = "";
    Repository repository;
    JsonObject feedobject = new JsonObject();
    private GoogleMap mMap;
    private LatLng pickupLatLng = null;
    private LatLng destinationLatLng = null;
    LoadingBar progressbar;


    public void setLocations(double pickupLat, double pickupLng, double destLat, double destLng) {
        pickupLatLng = new LatLng(pickupLat, pickupLng);
        destinationLatLng = new LatLng(destLat, destLng);
        if (mMap != null) {
            updateMapMarkers();
        }
    }

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
        //use this if need map visiulization
//        SupportMapFragment mapFragment = (SupportMapFragment)
//                getChildFragmentManager().findFragmentById(R.id.map);
//
//        if (mapFragment == null) {
//            mapFragment = SupportMapFragment.newInstance();
//            getChildFragmentManager().beginTransaction()
//                    .replace(R.id.map_container, mapFragment)
//                    .commit();
//        }
//
//        mapFragment.getMapAsync(this);
        //end
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefresh.setRefreshing(false);
                getAllData();

            }
        });
        return binding.getRoot();
    }
    private void showHideProgressBar(boolean showStatus) {
        if (showStatus) {
            progressbar.show();
        } else {
            progressbar.dismiss();
        }
    }

    public void downloadInvoiceFile(String tripId) {
        showHideProgressBar(true);
        Apis apiInterface = RetrofitClient.getFileDownloadRetrofitInstance(getActivity()).create(Apis.class);

        Call<ResponseBody> call = apiInterface.downloadInvoice(
                tripId
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showHideProgressBar(false);
                if (response.isSuccessful() && response.body() != null) {

                    boolean success = saveFileToGallery(response.body());
                    if (success) {

                        showDownloadNotification();
                    }
                } else {
                    Log.e("API_FAIL", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                showHideProgressBar(false);
            }
        });
    }

    public boolean saveFileToGallery(ResponseBody body) {
        try {
            String fileName = "Trip_Invoice_" + System.currentTimeMillis() + ".pdf";
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) downloadsDir.mkdirs();

            File file = new File(downloadsDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = body.byteStream();
            byte[] buffer = new byte[4096];
            int len;

            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

            fos.flush();
            fos.close();
            is.close();

            MediaScannerConnection.scanFile(
                    getActivity(),
                    new String[]{file.getAbsolutePath()},
                    new String[]{"application/pdf"},
                    null
            );

            lastDownloadedFilePath = file.getAbsolutePath(); // save path globally
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // Show Notification When Download Complete
    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "download_channel";
            CharSequence name = "Download Channel";
            String description = "Shows notification when download is complete";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showDownloadNotification() {
        createNotificationChannel(getActivity()); // Add this line
        File file = new File(lastDownloadedFilePath);
        Uri fileUri = FileProvider.getUriForFile(
                getActivity(),
                getActivity().getPackageName() + ".provider",
                file
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getActivity(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "download_channel")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Invoice Downloaded")
                .setContentText("Tap to open PDF")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.notify(1001, builder.build());
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Disable all gestures
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        // Disable marker clicks
        mMap.setOnMarkerClickListener(marker -> true);
        if (pickupLatLng != null && destinationLatLng != null) {
            updateMapMarkers();
        }
    }

    private void updateMapMarkers() {
        mMap.clear();

        // Add pickup marker with custom icon
        mMap.addMarker(new MarkerOptions()
                .position(pickupLatLng)
                .title("Pickup")
                .icon(resizeMapIcon(R.drawable.ic_circle_loc_black, 50, 70)));

        // Add destination marker with custom icon
        mMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .title("Destination")
                .icon(resizeMapIcon(R.drawable.ic_circle_loc_blue, 50, 70)));

        // Adjust camera to show both points
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pickupLatLng);
        builder.include(destinationLatLng);
        LatLngBounds bounds = builder.build();

        int padding = 100; // pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);

        // 👉 Ensure minimum zoom level so map doesn't look too far
        float currentZoom = mMap.getCameraPosition().zoom;
        float minZoom = 13.5f; // Adjust as needed (13-15 is street level)

        if (currentZoom < minZoom) {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(minZoom));
        }
    }

    private BitmapDescriptor resizeMapIcon(int drawableRes, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), drawableRes);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
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
                    Log.e("BookingDetail ", data.toString());
                    if (resp.getStatus() == 200) {

//         1 => 'Pending',2 => 'Accepted',
//                    3 => 'On the Way',
//                    4 => 'Arrived',
//                    5 => 'Picked Up',
//                    6 => 'On Going',
//                    7 => 'Completed',
//                    9 => 'Cancelled'

                        binding.tvStatus.setText(resp.getRecordList().getTripStatusName());
                        if(resp.getRecordList().getTripStatusName().equalsIgnoreCase("completed")){
                            binding.tvStatus.setBackgroundTintList(getActivity().getColorStateList(R.color.green_500));
                        }
                        if(resp.getRecordList().getIsOutstation()==1){
                            trip_type="1";
                            if(resp.getRecordList().getIsRound()==1){
                                outstation_type="1";
                            }
                            else {
                                outstation_type="0";
                            }
                        }
                        else{
                            trip_type="0";
                            outstation_type="0";
                        }


                        if(trip_type.equalsIgnoreCase("1")){
                            binding.tvTrip.setVisibility(View.VISIBLE);
                            if(outstation_type.equalsIgnoreCase("0")){
                                binding.tvTrip.setText(getActivity().getString(R.string.one_way_trip));
                            }
                            else {
                                binding.tvTrip.setText(getActivity().getString(R.string.round_trip));
                                binding.tvReturndata.setVisibility(View.VISIBLE);
                            }

                        }
                        else{
                            binding.tvReturndata.setVisibility(View.GONE);
                            binding.tvTrip.setVisibility(View.VISIBLE);
                        }

                        if(common.checkNullString(resp.getRecordList().getReturnDate()).equalsIgnoreCase("")){
                            binding.tvReturndata.setVisibility(View.GONE);
                        }else{
                            binding.tvReturndata.setVisibility(View.VISIBLE);
                            binding.tvReturndata.setText(getActivity().getString(R.string.return_date)+resp.getRecordList().getReturnDate());
                        }
                        //use this if need map visiulization
//                        setLocations(Double.parseDouble(resp.getRecordList().getPickupLat()), Double.parseDouble(resp.getRecordList().getPickupLng()),
//                                Double.parseDouble(resp.getRecordList().getDestinationLat()), Double.parseDouble(resp.getRecordList().getDestinationLng()));
                        String startLat =resp.getRecordList().getPickupLat();
                        String startLng = resp.getRecordList().getPickupLng();
                        String endLat = resp.getRecordList().getDestinationLat();
                        String endLng = resp.getRecordList().getDestinationLng();

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int width = displayMetrics.widthPixels;  // screen width in pixels
                        int height = (int) (width * 0.33);        // maintain 3:1 aspect ratio

                        String apiKey = getString(R.string.google_maps_key);
                        String url = "https://maps.googleapis.com/maps/api/staticmap?size=" + width + "x" + height
                                + "&markers=label:S|" + startLat + "," + startLng
                                + "&markers=label:E|" + endLat + "," + endLng
                                + "&key=" + apiKey;

                        Glide.with(getActivity()).load(url).into(binding.iv);
                        if (resp.getRecordList().getTripStatus() > 1&&resp.getRecordList().getTripStatus()<7) {
                            binding.linTrack.setVisibility(View.VISIBLE);
                        }
                        else if (resp.getRecordList().getTripStatus() > 6) {
                            binding.linInvoice.setVisibility(View.VISIBLE);
                            binding.linTrack.setVisibility(View.GONE);
                        }

                        if (common.checkNullString(resp.getRecordList().getUserFeedback()).equalsIgnoreCase("")) {
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
                        if(resp.getRecordList().getTripStatus()==9||resp.getRecordList().getTripStatus()<7){
                            binding.linData.setVisibility(View.GONE);
                            binding.linFill.setVisibility(View.GONE);
                            binding.linInvoice.setVisibility(View.GONE);
                            binding.linTrack.setVisibility(View.GONE);
                        }

                        tripId = String.valueOf(resp.getRecordList().getId());
                        if(!common.checkNullString(resp.getRecordList().getVehicleTypeImage()).equalsIgnoreCase("")){
                            Picasso.get().load(IMAGE_BASE_URL + resp.getRecordList().getVehicleTypeImage()).placeholder(R.drawable.logo).
                                    into(binding.vImg);
                        }
                        if(!common.checkNullString(resp.getRecordList().getProfileImage()).equalsIgnoreCase("")){
                            Picasso.get().load(IMAGE_BASE_URL + resp.getRecordList().getProfileImage()).placeholder(R.drawable.logo).
                                   into(binding.dImg);
                        }
                        if(!common.checkNullString(resp.getRecordList().getVehicleNumber()).equalsIgnoreCase("")){
                            binding.tvVnum.setText(resp.getRecordList().getVehicleNumber());}
                        if(!common.checkNullString(resp.getRecordList().getName()).equalsIgnoreCase("")){
                            binding.tvDname.setText(resp.getRecordList().getName());
                        }
                        if(!common.checkNullString(resp.getRecordList().getVehicleModelName()).equalsIgnoreCase("")){
                            binding.tvVname.setText(resp.getRecordList().getVehicleModelName() + "(" +
                                    resp.getRecordList().getVehicleColor() + ")");
                        }
                        binding.tvAmt.setText("-Rs." + resp.getRecordList().getAmount());
                        binding.tvDist.setText("Distance: " + resp.getRecordList().getDistance() + "KM");
                        if(resp.getRecordList().getTripStatus()==9){
                            binding.tvPayment.setText("");
                        }
                        else{
                        binding.tvPayment.setText("Payment by " + resp.getRecordList().getPaymentMode());}

                        binding.tvPick.setText(resp.getRecordList().getPickup());
                        binding.tvDesctination.setText(resp.getRecordList().getDestination());


                    } else {
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
                downloadInvoiceFile(book_id);
//                try {
//                  //  String pdfUrl = BASE_URL+model.getReceipt_url() ;
//                    String pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf" ;
////                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
////                    getActivity().startActivity(browserIntent);
//                    pendingFileUrl = pdfUrl;
//                    checkPermissionsAndDownload(pdfUrl);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
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
        progressbar = new LoadingBar(getActivity());
        ((MapActivity)getActivity()).showCommonPickDestinationArea(false,false);
        sessionManagment=new SessionManagment(getActivity());
        common=new Common(getActivity());
        book_id=getArguments().getString("book_id");
        book_date=getArguments().getString("book_date");
        String[] date=book_date.split(" ");
        String date_val=date[0]+" | "+common.convertToAmPm(date[1]);
        repository=new Repository(getActivity());
        ((MapActivity)getActivity()).setTitle("#ID "+book_id+"  "+date_val);
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

    private void checkPermissionsAndDownload(String fileUrl) {
        pendingFileUrl = fileUrl;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33
            // For Android 13+, ask media permissions
            List<String> permissionsToRequest = new ArrayList<>();
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_VIDEO);

            List<String> notGranted = new ArrayList<>();
            for (String perm : permissionsToRequest) {
                if (ContextCompat.checkSelfPermission(requireContext(), perm) != PackageManager.PERMISSION_GRANTED) {
                    notGranted.add(perm);
                }
            }

            if (!notGranted.isEmpty()) {
                requestPermissions(notGranted.toArray(new String[0]), 101);
            } else {
                startDownload(fileUrl);
            }

        } else {
            // For Android 12 and below
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            } else {
                startDownload(fileUrl);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted && pendingFileUrl != null) {
                startDownload(pendingFileUrl);
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startDownload(String fileUrl) {
        String fileName = getFileNameFromUrl(fileUrl);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle("Saving file");
        request.setDescription(fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.allowScanningByMediaScanner();

        DownloadManager manager = (DownloadManager) requireContext().getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Toast.makeText(requireContext(), "Download started", Toast.LENGTH_SHORT).show();
    }

    private String getFileNameFromUrl(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (mimeType != null && mimeType.startsWith("image")) {
            return "downloaded_image_" + System.currentTimeMillis() + "." + extension;
        } else if ("pdf".equalsIgnoreCase(extension)) {
            return "downloaded_pdf_" + System.currentTimeMillis() + ".pdf";
        } else {
            return "downloaded_file_" + System.currentTimeMillis() + "." + extension;
        }
    }


}
