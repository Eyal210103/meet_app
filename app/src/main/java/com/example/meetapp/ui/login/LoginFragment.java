package com.example.meetapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.meetapp.R;
import com.example.meetapp.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText emailET = view.findViewById(R.id.login_editTextTextEmailAddress);
        EditText passwordET = view.findViewById(R.id.login_editTextTextPassword);

        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String pass = passwordET.getText().toString();
                if (validateEmailAndPassword(email,pass))
                    singInWithEmail(email,pass);
                else{
                    Snackbar snackbar = Snackbar
                            .make(view, getResources().getString(R.string.invalid_inputs), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        return view;
    }

    private void singInWithEmail(String email , String password){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginFragment.this.requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            openMainAppScreen();
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(view, getResources().getString(R.string.auth_failed_message), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
    }

    private boolean validateEmailAndPassword(String email ,String password){
        return !email.matches("") && !password.matches("");
    }

    private void openMainAppScreen() {
        startActivity(new Intent(requireActivity(), MainActivity.class));
        requireActivity().finish();
    }
}