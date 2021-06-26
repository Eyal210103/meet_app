package com.example.meetapp.ui.myGroups;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.callbacks.PhotoUploadCompleteListener;
import com.example.meetapp.callbacks.PhotoUploadErrorListener;
import com.example.meetapp.databinding.FragmentCreateGroupBinding;
import com.example.meetapp.firebaseActions.StorageUpload;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.Group;
import com.example.meetapp.ui.createMeeting.SubjectAdapter;

import static android.app.Activity.RESULT_OK;
import static com.example.meetapp.ui.groupInfo.GroupInfoFragment.getDominantColor;


public class CreateGroupFragment extends Fragment implements PhotoUploadCompleteListener, PhotoUploadErrorListener, OnClickInRecyclerView {

    private static final int PICK_IMAGE = 52;
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private NavController navController;
    private int position;
    private GridLayoutManager gridLayoutManager;
    private SubjectAdapter subjectAdapter;
    private FragmentCreateGroupBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCreateGroupBinding.inflate(inflater, container, false);

        position = -1;
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        RecyclerView recyclerView = binding.createGroupChooseSubject;
        subjectAdapter = new SubjectAdapter(this);
        recyclerView.setAdapter(subjectAdapter);
        gridLayoutManager = new GridLayoutManager(requireActivity(), 5);
        recyclerView.setLayoutManager(gridLayoutManager);

        binding.createGroupChooseImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        binding.createGroupSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCreateGroup();
            }
        });
        return binding.getRoot();
    }

    private void onClickCreateGroup() {
        Group newGroup = new Group();
        boolean isGood = false;
        String name = "";
        String sub = "";
        if (!binding.createGroupGroupNameEt.getText().toString().matches("")) {
            name = binding.createGroupGroupNameEt.getText().toString();
            isGood = true;
            if (!binding.createGroupGroupSubject.getText().toString().equals("")) {
                sub = binding.createGroupGroupSubject.getText().toString();
            }
            if (isGood) {
                final String groupImageURL = "https://www.liberaldictionary.com/wp-content/uploads/2018/11/null.png";
                newGroup.setName(name);
                newGroup.setDescription(sub);
                newGroup.setPhotoUrl(groupImageURL);
                newGroup.setSubject(subjectAdapter.getSelected());
                newGroup.setIsPublic(binding.createGroupPublicSwitch.isChecked());
                String id = newGroup.addGroup();
                progressDialog = new ProgressDialog(requireActivity());
                progressDialog.setCancelable(false);
                progressDialog.setTitle(getString(R.string.create_group_message));
                progressDialog.show();
                StorageUpload.uploadGroupImage(CreateGroupFragment.this, id, imageUri);
                //StorageUpload.uploadGroupImage(CreateGroupFragment.this,id,imageUri);
            }
        } else {
            Toast.makeText(requireActivity(), "Please Type A Name", Toast.LENGTH_SHORT).show();
        }
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
                binding.createGroupCiv.setImageURI(imageUri);
                Bitmap bitmap = ((BitmapDrawable) binding.createGroupCiv.getDrawable()).getBitmap();
                int colorFromImg = getDominantColor(bitmap);
                int[] colors = {colorFromImg, requireActivity().getColor(R.color.background)};
                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                gd.setCornerRadius(0f);
                binding.createGroupLayout.setBackground(gd);
            }
        }
    }

    @Override
    public void onPhotoUploadComplete() {
        progressDialog.dismiss();
        navController.navigate(R.id.action_createGroupFragment_to_socialMenuFragment);
    }

    @Override
    public void onPhotoUploadError() {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), requireActivity().getString(R.string.upload_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickInRecyclerView(Object value, String action, Integer i) {
        if (action.equals(Const.ACTION_SUBJECT)) {
            int v = (int) value;
            if (position != -1) {
                gridLayoutManager.findViewByPosition(position).setBackgroundResource(R.drawable.subject_background);
            }
            position = v;
            gridLayoutManager.findViewByPosition(position).setBackgroundResource(R.drawable.selected_subject_background);
        }
    }
}