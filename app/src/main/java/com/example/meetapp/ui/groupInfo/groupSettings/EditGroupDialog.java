package com.example.meetapp.ui.groupInfo.groupSettings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.meetapp.R;
import com.example.meetapp.databinding.EditGroupDialogBinding;
import com.example.meetapp.firebaseActions.StorageUpload;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.Group;

import static android.app.Activity.RESULT_OK;
import static com.example.meetapp.ui.groupInfo.GroupInfoFragment.getDominantColor;

public class EditGroupDialog extends DialogFragment {

    private static final int PICK_IMAGE = 52;
    private Uri imageUri;
    private EditGroupDialogBinding binding;
    private Group group;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, Bundle savedInstanceState) {

        binding = EditGroupDialogBinding.inflate(inflater,container,false);
        View view =binding.getRoot();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        group = (Group)getArguments().getSerializable(Const.BUNDLE_GROUP_ID);
        binding.editGroupGroupNameEt.setText(group.getName());
        binding.editGroupGroupDescription.setText(group.getDescription());

        binding.editGroupGroupNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                group.setName(s.toString());
                group.updateGroup();
            }
        });
        binding.editGroupGroupDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                group.setDescription(s.toString());
                group.updateGroup();
            }
        });

        binding.editGroupCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        Glide.with(requireActivity()).load(group.getPhotoUrl()).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
                int colorFromImg = getDominantColor(bitmap);
                int[] colors = {EditGroupDialog.this.requireActivity().getColor(R.color.backgroundSec),colorFromImg,colorFromImg};

                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                gd.setCornerRadius(60);
                binding.getRoot().setBackground(gd);
                return false;
            }
        }).into(binding.editGroupCiv);

        return view;
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            if (data != null) {
                imageUri = data.getData();
                binding.editGroupCiv.setImageURI(imageUri);
                Bitmap bitmap = ((BitmapDrawable)binding.editGroupCiv.getDrawable()).getBitmap();
                int colorFromImg = getDominantColor(bitmap);
                int[] colors = {colorFromImg,requireActivity().getColor(R.color.background)};
                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                gd.setCornerRadius(0f);
                binding.getRoot().setBackground(gd);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StorageUpload.uploadGroupImage(EditGroupDialog.this,group.getId(),imageUri);
                    }
                });
                thread.start();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.dismiss();
    }
}
