package com.example.meetapp.ui.socialMenu.myGroups;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.meetapp.R;
import com.example.meetapp.firebaseActions.DatabaseWrite;
import com.example.meetapp.firebaseActions.StorageUpload;
import com.example.meetapp.model.Group;
import com.example.meetapp.uploadsListeners.PhotoUploadCompleteListener;
import com.example.meetapp.uploadsListeners.PhotoUploadErrorListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class CreateGroupFragment extends Fragment implements PhotoUploadCompleteListener, PhotoUploadErrorListener {

    private static final int PICK_IMAGE = 52;
    EditText groupNameEditText;
    EditText groupSubjectEditText;
    CircleImageView groupImageCIV;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_group, container, false);

        groupImageCIV = view.findViewById(R.id.create_group_civ);
        groupNameEditText = view.findViewById(R.id.create_group_group_name_et);
        groupSubjectEditText = view.findViewById(R.id.create_group_group_subject);

        view.findViewById(R.id.create_group_choose_img_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryGroup();
            }
        });

        view.findViewById(R.id.creat_group_submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCreateGroup();
            }
        });
        return view;
    }

    private void onClickCreateGroup() {
        Group newGroup = new Group();
        boolean isGood = false;
        String name = "";
        String sub = "";
        if (!groupNameEditText.getText().toString().matches("")) {
            name = groupNameEditText.getText().toString();
            isGood = true;
            if (!groupSubjectEditText.getText().toString().equals("")){
                sub = groupSubjectEditText.getText().toString();
            }
            if (isGood) {
                newGroup.setName(name);
                newGroup.setSubject(sub);
                final String groupImageURL = "https://www.liberaldictionary.com/wp-content/uploads/2018/11/null.png";
                newGroup.setPhotoUrl(groupImageURL);
                groupImageCIV.setDrawingCacheEnabled(true);
                groupImageCIV.buildDrawingCache();

                String id = DatabaseWrite.addOrUpdateGroupGetID(newGroup);

                Bitmap bitmap = ((BitmapDrawable) groupImageCIV.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] data = baos.toByteArray();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Creating , Please Wait..");
                progressDialog.show();
                StorageUpload.uploadGroupImage(this,id,data);
            }
        } else {
           // errors.setText("Type");
        }
    }

    private void openGalleryGroup() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            groupImageCIV.setImageURI(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri); //TODO check if need
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPhotoUploadComplete() {
        progressDialog.dismiss();
    }

    @Override
    public void onPhotoUploadError() {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), "Photo Wont Upload... \n Try Again Later", Toast.LENGTH_SHORT).show();
    }
}