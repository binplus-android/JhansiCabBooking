package com.cabbooking.fragement;

import static android.app.Activity.RESULT_OK;

import static com.cabbooking.utils.RetrofitClient.IMAGE_BASE_URL;
import static com.cabbooking.utils.SessionManagment.KEY_ID;
import static com.cabbooking.utils.SessionManagment.KEY_SHARE_LINK;
import static com.cabbooking.utils.SessionManagment.KEY_USER_IMAGE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cabbooking.R;
import com.cabbooking.Response.BookingDetailResp;
import com.cabbooking.Response.CommonResp;
import com.cabbooking.Response.LoginResp;
import com.cabbooking.Response.ProfileDetailResp;
import com.cabbooking.Response.ProfileUpdateResp;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentContactUsBinding;
import com.cabbooking.databinding.FragmentProfileBinding;
import com.cabbooking.utils.BuildConfig;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.Repository;
import com.cabbooking.utils.ResponseService;
import com.cabbooking.utils.SessionManagment;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    Common common;
    SessionManagment sessionManagment;
    Repository repository;
    Dialog dialog_profile;
    RelativeLayout rel_show_image,relImage;
    ImageView iv_image;
    LinearLayout lin_add_image;
    ImageView iv_add, iv_edit;
    String imageString="";

    private String currentPhotoPath; // <- yeh global banani padegi


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        initView();
        manageClicks();
        getProfile();
        return binding.getRoot();
    }

    public void getProfile() {
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        
        repository.getProfile(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    ProfileDetailResp resp = (ProfileDetailResp) data;
                    Log.e("getProfile ",data.toString());
                    if (resp.getStatus()==200) {
                        binding.tvMobile.setText(resp.getRecordList().getContactNo());
                        binding.tvName.setText(resp.getRecordList().getName());
                        binding.tvEmail.setText(resp.getRecordList().getEmail());
                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getProfileImage()).placeholder(R.drawable.logo).
                                error(R.drawable.logo).into(binding.imgProfile);
                        sessionManagment.setValue(KEY_SHARE_LINK,resp.getRecordList().getReferralLink());

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

    public void initView(){
        repository=new Repository(getActivity());
        sessionManagment=new SessionManagment(getActivity());
        ((MapActivity)getActivity()).setTitle("Profile");
        common = new Common(getActivity());

    }

    public void manageClicks(){
        binding.linName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                fragment = new UpdateProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type","name");
                bundle.putString("name",binding.tvName.getText().toString());
                fragment.setArguments(bundle);

                common.switchFragment(fragment);
            }
        });

        binding.linEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment;
                fragment = new UpdateProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type","email");
                bundle.putString("email",binding.tvEmail.getText().toString());
                fragment.setArguments(bundle);

                common.switchFragment(fragment);
            }
        });

        binding.ivEdit.setOnClickListener(v -> {
            imageString = "";
            OpenProfileImageDialog();;//imp

        });
    }

    public void OpenProfileImageDialog() {
        dialog_profile = new Dialog(getActivity(), R.style.AlertDialog);
        dialog_profile.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_profile.getWindow();
        dialog_profile.setContentView(R.layout.dialog_profile_update);
        dialog_profile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog_profile.getWindow().setGravity(Gravity.CENTER);
        dialog_profile.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        TextView tv_message=dialog_profile.findViewById (R.id.tv_message);
        rel_show_image = dialog_profile.findViewById(R.id.rel_show_image);
        Button btn_upload = dialog_profile.findViewById(R.id.btn_upload);
        TextView tv_heading = dialog_profile.findViewById(R.id.tv_heading);
        iv_image=dialog_profile.findViewById(R.id.iv_image);

        lin_add_image=dialog_profile.findViewById(R.id.lin_add_image);

        tv_heading.setText(getString(R.string.change_photo));
        tv_message.setText(getString(R.string.upload_photo));


        ImageView ivRemoveImg=dialog_profile.findViewById(R.id.iv_cancel_img);

        ivRemoveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rel_show_image.setVisibility(View.GONE);
                lin_add_image.setVisibility(View.VISIBLE);
            }
        });

        if (!dialog_profile.isShowing()) {
            dialog_profile.show();
        }
        //dialog.setCancelable(false);

        iv_add = dialog_profile.findViewById(R.id.iv_add);

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePickerForProfile();
            }
        });

        ImageView iv_close = dialog_profile.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_profile.dismiss();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //  SelectProfileImage(dialog_profile);
//                dialog_profile.dismiss ();
                if (TextUtils.isEmpty(imageString)) {
                    common.errorToast(getString(R.string.choose_photo_to_upload));
                }else {
                    callUploadImage(dialog_profile);
                }
            }
        });
    }

    public void callUploadImage(Dialog dialogProfile)  {
        JsonObject object=new JsonObject();
        object.addProperty("userId",sessionManagment.getUserDetails().get(KEY_ID));
        object.addProperty("type","profile");
        object.addProperty("profile",imageString);
        repository.postProfileImage(object, new ResponseService() {
            @Override
            public void onResponse(Object data) {
                try {
                    ProfileUpdateResp resp = (ProfileUpdateResp) data;
                    Log.e("ProfileUpdateResp ",data.toString());
                    if (resp.getStatus()==200) {
                        dialogProfile.dismiss();
                        common.successToast(resp.getMessage ());
                        sessionManagment.setValue(KEY_USER_IMAGE,resp.getRecordList().getProfileImage());
                        ((MapActivity)getActivity()).setImage(resp.getRecordList().getProfileImage());
                        Picasso.get().load(IMAGE_BASE_URL+resp.getRecordList().getProfileImage()).placeholder(R.drawable.logo).
                                error(R.drawable.logo).into(binding.imgProfile);

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
        }, true);

    }

//    public void openImagePickerForProfile() {
//        File destination = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ImagePicker");
//        if (!destination.exists()) {
//            destination.mkdirs();
//        }
//
//
//        ImagePicker.with(getActivity())
//                .compress(1024) // Max 1 MB
//                .maxResultSize(1080, 1080)
//                .saveDir(destination) // Save image to file, avoid large Intent
//                .createIntent(new Function1<Intent, Unit>() {
//                    @Override
//                    public Unit invoke(Intent intent) {
//                        if (getActivity() != null && !getActivity() .isFinishing() && !getActivity() .isDestroyed()) {
//                            profileImgLauncher.launch(intent);
//                        }
//
//                        return null;
//                    }
//                });
//    }


    private void openImagePickerForProfile() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_picker); // (custom layout banayenge niche)
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFDD0"))); // Cream color

        Button btnCamera = dialog.findViewById(R.id.btnCamera);
        Button btnGallery = dialog.findViewById(R.id.btnGallery);

        btnCamera.setOnClickListener(v -> {
            dialog.dismiss();
            openCamera();
        });

        btnGallery.setOnClickListener(v -> {
            dialog.dismiss();
            openGallery();
        });

        dialog.show();
    }


    private void openCamera() {
        File destination = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ImagePicker");
        if (!destination.exists()) {
            destination.mkdirs();
        }

        ImagePicker.with(this)
                .cameraOnly()
                .compress(1024) // Compress to ~1 MB
                .maxResultSize(1080, 1080) // Resize for safe memory usage
                .saveDir(destination)
                .createIntent(intent -> {
                    if (getActivity() != null && !getActivity().isFinishing() && !getActivity().isDestroyed()) {
                        profileImgLauncher.launch(intent);
                    }
                    return null;
                });
    }

    private void openGallery() {

        File destination = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ImagePicker");
        if (!destination.exists()) {
            destination.mkdirs();
        }

        ImagePicker.with(this)
                .galleryOnly()
//                .compress(1024)
//                .maxResultSize(1080, 1080)
                .saveDir(destination) // Your destination folder
                .createIntent(intent -> {
                    if (getActivity() != null && !getActivity().isFinishing() && !getActivity().isDestroyed()) {
                        profileImgLauncher.launch(intent);
                    }
                    return null;
                });
    }

    public final ActivityResultLauncher<Intent> profileImgLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            try {
                                InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
                                if (inputStream != null) {
                                    int fileSizeInBytes = inputStream.available(); // get file size in bytes
                                    inputStream.close();

                                    if (fileSizeInBytes > 2 * 1024 * 1024) { // 2 MB
//                                        Toast.makeText(getContext(), "Image size should be of 2 MB", Toast.LENGTH_SHORT).show();
                                        common.errorToast(getString(R.string.image_size_exceeds));
                                    } else {
                                        startCropActivity(selectedImageUri); // if size is okay, proceed to crop
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
    );

    public void startCropActivity(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getContext().getCacheDir(), "croppedImage.jpg"));

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(90);
        options.setFreeStyleCropEnabled(true); // Enable free-style cropping

        Intent cropIntent = UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .getIntent(getActivity());

        cropImageLauncher.launch(cropIntent);
    }

    public final ActivityResultLauncher<Intent> cropImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Get the cropped image URI
                        Uri croppedImageUri = UCrop.getOutput(result.getData());
                        if (croppedImageUri != null) {
                            handleCroppedImage(croppedImageUri);
                        }
                    } else if (result.getResultCode() == UCrop.RESULT_ERROR && result.getData() != null) {
                        Throwable cropError = UCrop.getError(result.getData());
                        if (cropError != null) {
                            Log.e("UCrop Error", cropError.getMessage());
                        }
                    }
                }
            }
    );
//
    public void handleCroppedImage(Uri croppedImageUri) {
        try {
            if (dialog_profile == null) {
                OpenProfileImageDialog();
            }

            InputStream in = getContext().getContentResolver().openInputStream(croppedImageUri);
            byte[] bytes = common.getBytes(in);

            Log.e("fgvbhjnk", croppedImageUri.toString());

            // Check if image size exceeds 2 MB
            if (bytes.length > 2 * 1024 * 1024) { // 2 MB = 2 * 1024 * 1024 bytes
                common.errorToast(getString(R.string.image_size_exceeds));
                return; // Stop further processing
            } else {

            // NEW: Load compressed Bitmap
            Bitmap bitmap = decodeSampledBitmapFromUri(croppedImageUri, 1024, 1024);

            if (bitmap != null) {
                imageString = common.convertBitmapToBase64(bitmap);
                iv_add.setImageBitmap(bitmap);
                iv_add.setBackgroundColor(getContext().getResources().getColor(R.color.white));

                TextView tv_message = dialog_profile.findViewById(R.id.tv_message);
                tv_message.setText(getString(R.string.photo_selected));
                tv_message.setTextColor(getResources().getColor(R.color.green_500));
            }
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
    // Helper Method to downsample the image
    public Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        InputStream imageStream = getContext().getContentResolver().openInputStream(uri);
        BitmapFactory.decodeStream(imageStream, null, options);
        if (imageStream != null) imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = getContext().getContentResolver().openInputStream(uri);
        Bitmap sampledBitmap = BitmapFactory.decodeStream(imageStream, null, options);
        if (imageStream != null) imageStream.close();

        return sampledBitmap;
    }
//
    // Calculate best sample size
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }














//    private void openCamera() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//                Toast.makeText(getContext(), "File creation failed", Toast.LENGTH_SHORT).show();
//            }
//
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(
//                        getContext(),
//                        getContext().getPackageName() + ".provider",
//                        photoFile
//                );
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, 1001);
//            }
//        }
//    }


//    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//
//        File image = File.createTempFile(
//                imageFileName,
//                ".jpg",
//                storageDir
//        );
//
//        // Save path to global variable
//        currentPhotoPath = image.getAbsolutePath();
//        return image;
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
//            if (currentPhotoPath != null) {
//                File file = new File(currentPhotoPath);
//
//                if (file.exists()) {
//                    long fileSizeInBytes = file.length();
//                    long fileSizeInMB = fileSizeInBytes / (1024 * 1024);
//
//                    if (fileSizeInMB > 2) {
//                        Toast.makeText(getContext(), "Image too large!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
//                        iv_add.setImageBitmap(bitmap);
//                    }
//                } else {
//                    Toast.makeText(getContext(), "File doesn't exist!", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(getContext(), "Photo path is null!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }




}