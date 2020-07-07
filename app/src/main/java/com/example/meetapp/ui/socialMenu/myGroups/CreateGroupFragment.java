package com.example.meetapp.ui.socialMenu.myGroups;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetapp.R;
import com.example.meetapp.firebaseActions.DatabaseWrite;
import com.example.meetapp.firebaseActions.StorageUpload;
import com.example.meetapp.model.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class CreateGroupFragment extends Fragment {

    private static final int PICK_IMAGE = 52;
    EditText groupNameEditText;
    EditText groupSubjectEditText;
    CircleImageView groupImageCIV;
    private Uri imageUri;
    private Bitmap bitmap;
    private Group newGroup;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_group, container, false);

        groupImageCIV = view.findViewById(R.id.create_group_civ);
        groupNameEditText = view.findViewById(R.id.create_group_group_name_et);
        groupSubjectEditText = view.findViewById(R.id.create_group_group_subject);

        return view;
    }

    private void onClickCreateGroup() {
        this.newGroup = new Group();
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
                this.newGroup.setName(name);
                this.newGroup.setSubject(sub);
                final String groupImageURL = "https://www.liberaldictionary.com/wp-content/uploads/2018/11/null.png";
                groupImageCIV.setDrawingCacheEnabled(true);
                groupImageCIV.buildDrawingCache();
                String id = DatabaseWrite.AddOrUpdateGroupGetID(newGroup);
                Bitmap bitmap = ((BitmapDrawable) groupImageCIV.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                byte[] data = baos.toByteArray();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Creating , Please Wait..");
                progressDialog.show();
                StorageUpload.uploadGroupImage(getContext(),id,data);
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
            this.imageUri = data.getData();
            groupImageCIV.setImageURI(this.imageUri);
            try {
                this.bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}