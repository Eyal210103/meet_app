package com.example.meetapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnCompleteAction;
import com.example.meetapp.firebaseActions.AvailableMeetingsRepo;
import com.example.meetapp.firebaseActions.UserGroupsRepo;
import com.example.meetapp.firebaseActions.UserMeetingsRepo;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.ui.login.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity implements OnCompleteAction {

    public static final int DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Const.SP_SETTINGS,MODE_PRIVATE);
        if (!sharedPreferences.contains(Const.SP_IS_DARK_ON)){
            sharedPreferences.edit().putBoolean(Const.SP_IS_DARK_ON, AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES).apply();
        }else {
            if (sharedPreferences.getBoolean(Const.SP_IS_DARK_ON,false))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        if (CurrentUser.isConnected()) {
            UserGroupsRepo.getInstance();
            UserMeetingsRepo.getInstance();
            AvailableMeetingsRepo.getInstance(this);
        } else {
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

    @Override
    public void OnComplete() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}