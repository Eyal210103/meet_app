package com.example.meetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.meetapp.firebaseActions.DatabaseWrite;
import com.example.meetapp.model.CurrentUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    private static final int GOOGLE = 101;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        if (mAuth.getCurrentUser() != null){
            CurrentUser.firebaseUserToAppUser(mAuth.getCurrentUser());
            openMainAppScreen();
        }
        else {

            //Google sign in
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
            this.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickSignInWithGoogle();
                }
            });
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
//            imageUri = data.getData();
//            CircleImageView profile = (CircleImageView)signUpDialog.findViewById(R.id.civ_profile_sign_up);
//            Glide.with(this).load(imageUri).into(profile);
//        } else
            if (requestCode == GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Authentication failed, Try Again", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void openMainAppScreen() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    private void onClickSignInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE);
    }

    private void firebaseAuthWithGoogle(@NotNull GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            CurrentUser.firebaseUserToAppUser(mAuth.getCurrentUser());
                            DatabaseWrite.AddOrUpdateUser(CurrentUser.getCurrentUser());
                            Toast.makeText(getApplicationContext(), "Authentication succeed.", Toast.LENGTH_SHORT).show();
                            openMainAppScreen();
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }
}