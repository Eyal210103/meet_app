package com.example.meetapp.ui.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.meetapp.callbacks.OnClickInFragment;
import com.example.meetapp.callbacks.OnCompleteAction;
import com.example.meetapp.callbacks.PhotoUploadCompleteListener;
import com.example.meetapp.databinding.SettingsFragmentBinding;
import com.example.meetapp.firebaseActions.StorageUpload;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.CurrentUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment implements PhotoUploadCompleteListener, OnCompleteAction {

    private SettingsViewModel mViewModel;
    private static final int PICK_IMAGE = 50;

    ProgressDialog progressDialog;

    View selected;
    private Uri imageUri;
    SettingsFragmentBinding binding;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = SettingsFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        updateUI();
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
                    selected = null;
                } else {
                    binding.accountSettingsFieldLinearLayout.setVisibility(View.VISIBLE);
                    if (selected != null) {
                        selected.setVisibility(View.GONE);
                        selected = binding.accountSettingsFieldLinearLayout;
                    }
                }
            }
        });

        return view;
    }

    public void updateUI() {
        Glide.with(requireActivity()).load(CurrentUser.getInstance().getProfileImageUrl()).into(binding.settingsUserProfile);
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