package com.example.meetapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.meetapp.callbacks.OnClickInFragment;
import com.example.meetapp.firebaseActions.UserGroupsRepo;
import com.example.meetapp.model.CurrentUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements OnClickInFragment {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder().build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView,navController);
        UserGroupsRepo.getInstance(null).getGroups();
    }

    @Override
    public void onClickInFragment(String action) {
        if (action.equals(String.valueOf(R.string.logoutAction)))
            CurrentUser.logout();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}