package com.example.meetapp.ui.Login;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.meetapp.R;
import com.example.meetapp.firebaseActions.DatabaseWrite;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.ui.MainActivity;
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

import java.util.concurrent.Executor;

public class LoginOptionsFragment extends Fragment {

    private LoginOptionsViewModel mViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";
    private static final int GOOGLE = 101;
    private FirebaseAuth mAuth;

    public static LoginOptionsFragment newInstance() {
        return new LoginOptionsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.login_options_fragment, container, false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        this.mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        view.findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignInWithGoogle();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LoginOptionsViewModel.class);
    }

    private void onClickSignInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(requireActivity(), "Authentication failed, Try Again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void firebaseAuthWithGoogle(@NotNull GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity() , new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            CurrentUser.firebaseUserToAppUser(mAuth.getCurrentUser());
                            DatabaseWrite.addOrUpdateUser(CurrentUser.getCurrentUser());
                            Toast.makeText(requireActivity(), "Authentication succeed.", Toast.LENGTH_SHORT).show();
                            openMainAppScreen();
                        } else {
                            Toast.makeText(requireActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            LoginOptionsFragment.this.requireActivity().finish();
                        }
                    }
                });
    }
    private void openMainAppScreen() {
        startActivity(new Intent(requireActivity(), MainActivity.class));
        requireActivity().finish();
    }

}