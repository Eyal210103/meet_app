package com.example.meetapp.ui.Login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetapp.R;
import com.example.meetapp.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            openMainAppScreen();
        }
    }

    private void openMainAppScreen() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

}