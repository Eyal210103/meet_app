package com.example.meetapp.ui.Login;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meetapp.R;
import com.example.meetapp.firebaseActions.DatabaseWrite;
import com.example.meetapp.firebaseActions.StorageUpload;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.uploadsListeners.PhotoUploadCompleteListener;
import com.example.meetapp.uploadsListeners.PhotoUploadErrorListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupFragment extends Fragment implements PhotoUploadErrorListener, PhotoUploadCompleteListener {

    View view;

    //https://cdn.business2community.com/wp-content/uploads/2017/08/blank-profile-picture-973460_640.png

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);


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
                                        CurrentUser.firebaseUserToAppUser(user);
                                        DatabaseWrite.addOrUpdateUser(CurrentUser.getCurrentUser());
                                        //TODO implement select Image
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
        else if (!pass.matches(passConfirm)){
            Snackbar snackbar = Snackbar
                    .make(view, "Passwords Are Not Identical", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
        return true;
    }

    @Override
    public void onPhotoUploadError() {

    }

    @Override
    public void onPhotoUploadComplete() {

    }
}