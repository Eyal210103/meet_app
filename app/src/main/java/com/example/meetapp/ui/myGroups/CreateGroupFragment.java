package com.example.meetapp.ui.myGroups;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.firebaseActions.StorageUpload;
import com.example.meetapp.model.Group;
import com.example.meetapp.ui.createMeeting.SubjectAdapter;
import com.example.meetapp.uploadsListeners.PhotoUploadCompleteListener;
import com.example.meetapp.uploadsListeners.PhotoUploadErrorListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class CreateGroupFragment extends Fragment implements PhotoUploadCompleteListener, PhotoUploadErrorListener, OnClickInRecyclerView {

    private static final int PICK_IMAGE = 52;
    private EditText groupNameEditText;
    private EditText groupSubjectEditText;
    private SwitchCompat switchCompat;
    private CircleImageView groupImageCIV;
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private NavController navController;
    private int position;
    private GridLayoutManager gridLayoutManager;
    private SubjectAdapter subjectAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_group, container, false);

        position = -1;
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        groupImageCIV = view.findViewById(R.id.create_group_civ);
        groupNameEditText = view.findViewById(R.id.create_group_group_name_et);
        groupSubjectEditText = view.findViewById(R.id.create_group_group_subject);
        switchCompat = view.findViewById(R.id.create_group_public_switch);

        RecyclerView recyclerView = view.findViewById(R.id.create_group_choose_subject);
        subjectAdapter = new SubjectAdapter(this);
        recyclerView.setAdapter(subjectAdapter);
        gridLayoutManager = new GridLayoutManager(requireActivity(), 5);
        recyclerView.setLayoutManager(gridLayoutManager);

        view.findViewById(R.id.create_group_choose_img_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        view.findViewById(R.id.create_group_submit_button).setOnClickListener(new View.OnClickListener() {
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
                final String groupImageURL = "https://www.liberaldictionary.com/wp-content/uploads/2018/11/null.png";
                newGroup.setName(name);
                newGroup.setDescription(sub);
                newGroup.setPhotoUrl(groupImageURL);
                newGroup.setSubject(subjectAdapter.getSelected());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        newGroup.setPublic(isChecked);
                    }
                });
                String id = newGroup.addOrUpdateGroupGetID();
                progressDialog = new ProgressDialog(requireActivity());
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Creating , Please Wait..");
                progressDialog.show();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StorageUpload.uploadGroupImage(CreateGroupFragment.this,id,imageUri);
                    }
                });
                thread.start();
                //StorageUpload.uploadGroupImage(CreateGroupFragment.this,id,imageUri);
            }
        } else {
           // errors.setText("Type");
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
                groupImageCIV.setImageURI(imageUri);
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
        Toast.makeText(getActivity(), "Photo Wont Upload... \n Try Again Later", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickInRecyclerView(Object value, String action) {
        if (action.equals("subject")){
            int v = (int)value;
            if (position != -1){
                gridLayoutManager.findViewByPosition(position).setBackgroundResource(R.drawable.subject_background);
            }
            position = v;
            gridLayoutManager.findViewByPosition(position).setBackgroundResource(R.drawable.selected_subject_background);
        }
    }
}