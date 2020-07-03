package com.example.meetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.meetapp.model.CurrentUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (mAuth.getCurrentUser() != null){
            CurrentUser.firebaseUserToAppUser(mAuth.getCurrentUser());
            openMainAppScreen();
        }


    }

    private void openMainAppScreen() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }


}