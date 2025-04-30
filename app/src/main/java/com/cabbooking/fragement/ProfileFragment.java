package com.cabbooking.fragement;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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

import com.cabbooking.R;
import com.cabbooking.activity.MapActivity;
import com.cabbooking.databinding.FragmentContactUsBinding;
import com.cabbooking.databinding.FragmentProfileBinding;
import com.cabbooking.utils.Common;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    Common common;
    Dialog dialog_profile;
    RelativeLayout rel_show_image,relImage;
    ImageView iv_image;
    LinearLayout lin_add_image;
    ImageView iv_add, iv_edit;
    String imageString="";



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
        return binding.getRoot();
    }

    private void initView(){
        ((MapActivity)getActivity()).setTitle("Profile");
        common = new Common(getActivity());

    }

    private void manageClicks(){
        binding.linName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                fragment = new UpdateProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type","name");
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
                fragment.setArguments(bundle);

                common.switchFragment(fragment);
            }
        });

        binding.ivEdit.setOnClickListener(view -> OpenProfileImageDialog());//imp

    }

    private void OpenProfileImageDialog() {
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
//                    callUpdateCategoryWiseProfileApi(dialog_profile);
                }
            }
        });
    }

    public void openImagePickerForProfile() {
        ImagePicker.with(getActivity())
//                .crop() // Enable cropping
                .createIntent(new Function1<Intent, Unit>() {
                    @Override
                    public Unit invoke(Intent intent) {
                        if (getActivity() != null && !getActivity() .isFinishing() && !getActivity() .isDestroyed()) {
                            profileImgLauncher.launch(intent);
                        }

                        return null;
                    }
                });
    }

    private final ActivityResultLauncher<Intent> profileImgLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            startCropActivity(selectedImageUri);
                        }
                    }
                }
            }
    );

    private void startCropActivity(Uri sourceUri) {
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

    private final ActivityResultLauncher<Intent> cropImageLauncher = registerForActivityResult(
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

    private void handleCroppedImage(Uri croppedImageUri) {
        try {
            // Convert the cropped image URI to Base64 string
            String imageBase64 = common.convertToBase64String(croppedImageUri, getContext());
            if (imageBase64 != null) {
                Bitmap bitmap = common.handleImageRotation(croppedImageUri, getContext());

//                if (removeBgType.equals("1")) {
//                    //paid
//                    RemoveBackground removeBackground = new RemoveBackground(getActivity());
//                    removeBackground.uploadImageToRemoveBackground(bitmap, new OnRemoveBackgroundCallBack() {
//                        @Override
//                        public void onSuccess(Bitmap processedBitmap) {

                            // Set the final bitmap and convert to Base64
                            imageString = common.convertBitmapToBase64(bitmap);
                            if (imageString != null && !imageString.isEmpty()) {
//                                finalResultBitmap = processedBitmap;
                                iv_add.setImageBitmap(bitmap);
                                iv_add.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                                // Call success callback
                            }

                            if (imageString != null) {
                                Log.e("image", "Cropped Image Processed: " + imageString);

                                TextView tv_message = dialog_profile.findViewById(R.id.tv_message);
//                                if (activeCategoryId.equals(Constance.BUSINESS_CATEGORY)) {
//                                    tv_message.setText(getString(R.string.logo_selected));
//                                } else {
                                    tv_message.setText(getString(R.string.photo_selected));
//                                }
                                tv_message.setTextColor(getResources().getColor(R.color.green_500));
                            }
//                        }
//
//                        @Override
//                        public void onFailure(String error) {
//                            common.errorToast(error);
//                            finalResultBitmap = null;
//                            imageString = "";
//                        }
//                    });
//                }else {
                    // important
                    // un-paid
                    // Process the cropped image
//                    loadingBar.show();
//
//                    processImage(bitmap, new OnSuccessCallBack() {
//                        @Override
//                        public void onSuccess() {
//                            loadingBar.dismiss();
//                            if (imageString != null) {
//                                Log.e("image", "Cropped Image Processed: " + imageString);
//
//                                TextView tv_message = dialog_profile.findViewById(R.id.tv_message);
//                                if (activeCategoryId.equals(Constance.BUSINESS_CATEGORY)) {
//                                    tv_message.setText(getString(R.string.logo_selected));
//                                } else {
//                                    tv_message.setText(getString(R.string.photo_selected));
//                                }
//                                tv_message.setTextColor(getResources().getColor(R.color.green));
//                            }
//                        }
//
//                        @Override
//                        public void onFailure() {
//                            loadingBar.dismiss();
//                        }
//                    });
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}