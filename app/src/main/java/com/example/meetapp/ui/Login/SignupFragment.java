package com.example.meetapp.ui.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.PhotoUploadCompleteListener;
import com.example.meetapp.callbacks.PhotoUploadErrorListener;
import com.example.meetapp.firebaseActions.StorageUpload;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class SignupFragment extends Fragment implements PhotoUploadErrorListener, PhotoUploadCompleteListener {

    private static final int PICK_IMAGE = 50;
    Uri imageUri;
    View view;
    ProgressDialog progressDialog;

    //https://cdn.business2community.com/wp-content/uploads/2017/08/blank-profile-picture-973460_640.png

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUri = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);

        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Creating Your Account, Please Wait...");

        EditText displayET = view.findViewById(R.id.signup_editTextTextDisplayName);
        EditText emailET = view.findViewById(R.id.signup_editTextTextEmailAddress);
        EditText passET = view.findViewById(R.id.signup_editTextTextPassword);
        EditText passConfirmET = view.findViewById(R.id.signup_editTextTextPasswordConfirm);

        view.findViewById(R.id.signup_profilepic_civ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        view.findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display = displayET.getText().toString();
                String email = emailET.getText().toString();
                String pass = passET.getText().toString();
                String passConfirm = passConfirmET.getText().toString();

                if (validateSignUp(display,email,pass,passConfirm)){
                    progressDialog.show();
                    signUpWithEmail(display,email,pass);
                }
            }
        });

        return view;
    }

    private void signUpWithEmail(String display , String email, String password){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupFragment.this.requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(display)
                                    .setPhotoUri(Uri.parse("https://cdn.business2community.com/wp-content/uploads/2017/08/blank-profile-picture-973460_640.png"))
                                    .build();
                            if (user != null) {
                                user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        CurrentUser.addOrUpdateUser();
                                        if (imageUri != null){
                                            Thread thread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    StorageUpload.uploadProfileImage(SignupFragment.this,CurrentUser.getInstance().getId(),imageUri);
                                                }
                                            });
                                            thread.start();
                                        }
                                        else {
                                            signUpComplete();
                                        }
                                    }
                                });
                            }
                        } else {
                        }

                    }
                });
    }

    private boolean validateSignUp(String display , String email, String pass , String passConfirm){
        if (display.matches("")){
            Snackbar snackbar = Snackbar
                    .make(view, "Please Choose A Display Name", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
        else if (email.matches("") || email.indexOf('@') == -1 || email.indexOf('.') == -1 || email.charAt(0) == '@' ){
            Snackbar snackbar = Snackbar
                    .make(view, "Invalid Email Address", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
        else if (pass.length() < 6){
            Snackbar snackbar = Snackbar
                    .make(view, "A Password Needs To Contained At Least 6 Characters", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
        else if (!pass.equals(passConfirm)){
            Snackbar snackbar = Snackbar
                    .make(view, "Passwords Are Not Identical", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
        return true;
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            ((CircleImageView)view.findViewById(R.id.signup_profilepic_civ)).setImageURI(imageUri);
        }
    }

    private void signUpComplete() {
        progressDialog.dismiss();
        openMainAppScreen();
    }

    private void openMainAppScreen() {
        startActivity(new Intent(this.requireActivity(), MainActivity.class));
        this.requireActivity().finish();
    }


    @Override
    public void onPhotoUploadError() {

    }

    @Override
    public void onPhotoUploadComplete() {
        signUpComplete();
    }
}