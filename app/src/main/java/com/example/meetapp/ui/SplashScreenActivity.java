package com.example.meetapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetapp.R;
import com.example.meetapp.firebaseActions.UserGroupsRepo;
import com.example.meetapp.firebaseActions.UserMeetingsRepo;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.ui.login.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    public static final int DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (CurrentUser.isConnected()){
            UserGroupsRepo.getInstance();
            UserMeetingsRepo.getInstance();
        }

        Intent intent = new Intent(this, LoginActivity.class);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, DELAY);
    }
}