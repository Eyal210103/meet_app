package com.example.meetapp.ui.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInFragment;
import com.example.meetapp.callbacks.OnCompleteAction;
import com.example.meetapp.callbacks.PhotoUploadCompleteListener;
import com.example.meetapp.databinding.SettingsFragmentBinding;
import com.example.meetapp.firebaseActions.StorageUpload;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.CurrentUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static android.app.Activity.RESULT_OK;
import static com.example.meetapp.ui.groupInfo.GroupInfoFragment.getDominantColor;

public class SettingsFragment extends Fragment implements PhotoUploadCompleteListener, OnCompleteAction {

    private static final int PICK_IMAGE = 50;

    SettingsViewModel mViewModel;
    ProgressDialog progressDialog;

    private Uri imageUri;
    private SettingsFragmentBinding binding;
    private SharedPreferences sp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        sp = requireContext().getSharedPreferences(Const.SP_SETTINGS, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = SettingsFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        updateUI();

        if (mViewModel.isPrefFieldOpen){
            binding.preferencesSettingsField.setVisibility(View.VISIBLE);
        }
        if (mViewModel.isAccountFieldOpen){
            binding.accountSettingsFieldLinearLayout.setVisibility(View.VISIBLE);
        }

        binding.settingsEditTextDisplayName.setText(CurrentUser.getInstance().getDisplayName());

        binding.settingsChooseImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        binding.settingsLogoutTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickInFragment onClickInFragment = (OnClickInFragment) requireActivity();
                onClickInFragment.onClickInFragment(Const.ACTION_LOGOUT);
            }
        });

        binding.settingsSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDisplayName = binding.settingsEditTextDisplayName.getText().toString();
                if (imageUri != null || (!newDisplayName.matches("") && !newDisplayName.matches(CurrentUser.getInstance().getDisplayName()))) {
                    progressDialog = new ProgressDialog(requireActivity());
                    progressDialog.setCancelable(false);
                    progressDialog.setTitle("Updating Your Account, Please Wait...");
                    progressDialog.show();
                    if (imageUri != null && (newDisplayName.matches("") || newDisplayName.matches(CurrentUser.getInstance().getDisplayName())))
                        StorageUpload.uploadProfileImage(SettingsFragment.this, CurrentUser.getInstance().getId(), imageUri);
                    else if (imageUri != null && !newDisplayName.matches(""))
                        StorageUpload.uploadProfileImageWithDisplayNameUpdate(SettingsFragment.this, CurrentUser.getInstance().getId(), imageUri, newDisplayName);
                    else
                        updateDisplayName(newDisplayName);
                }
            }
        });

        binding.settingsAccountTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.accountSettingsFieldLinearLayout.getVisibility() == View.VISIBLE) {
                    binding.accountSettingsFieldLinearLayout.setVisibility(View.GONE);
                } else {
                    binding.accountSettingsFieldLinearLayout.setVisibility(View.VISIBLE);
                    mViewModel.isAccountFieldOpen = true;
                }
            }
        });

        binding.settingsPreferencesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.preferencesSettingsField.getVisibility() == View.VISIBLE){
                    binding.preferencesSettingsField.setVisibility(View.GONE);
                }else {
                    binding.preferencesSettingsField.setVisibility(View.VISIBLE);
                    mViewModel.isPrefFieldOpen = true;
                }

            }
        });

        if (sp.getBoolean(Const.SP_IS_DARK_ON,false))
            binding.settingsDarkModeSwitch.setChecked(true);

        binding.settingsDarkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sp.edit().putBoolean(Const.SP_IS_DARK_ON,isChecked).apply();
            }
        });

        return view;
    }

    public void updateUI() {
        Glide.with(requireActivity()).load(CurrentUser.getInstance().getProfileImageUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
                int colorFromImg = getDominantColor(bitmap);
                try {
                    int[] colors = {requireActivity().getColor(R.color.background),requireActivity().getColor(R.color.background),colorFromImg};
                    GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
                    binding.settingsLayout.setBackground(gd);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }
        }).into(binding.settingsUserProfile);
        binding.settingsUserName.setText(CurrentUser.getInstance().getDisplayName());
        binding.settingsUserEmail.setText(CurrentUser.getInstance().getEmail());
        binding.settingsEditTextDisplayName.setText(CurrentUser.getInstance().getDisplayName());
        Glide.with(requireActivity()).load(CurrentUser.getInstance().getProfileImageUrl()).into(binding.settingsEditImgCiv);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void updateDisplayName(String displayName){
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();
        CurrentUser.updateUserInAuth(profileUpdate,this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            binding.settingsEditImgCiv.setImageURI(imageUri);
        }
    }

    @Override
    public void onPhotoUploadComplete() {
        progressDialog.dismiss();
        updateUI();
    }

    @Override
    public void OnComplete() {
        progressDialog.dismiss();
        updateUI();
    }
}