package com.example.meetapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInFragment;
import com.example.meetapp.firebaseActions.UserGroupsRepo;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.notifications.MessageNotificationService;
import com.example.meetapp.ui.Login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements OnClickInFragment  {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityViewModel mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder().build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView,navController);

        UserGroupsRepo.getInstance().getGroups();

        MessageNotificationService messageNotificationService = new MessageNotificationService();
        messageNotificationService.startService();
    }

    @Override
    public void onClickInFragment(String action) {
        if (action.equals(getString(R.string.logoutAction)))
            CurrentUser.logout();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }


}