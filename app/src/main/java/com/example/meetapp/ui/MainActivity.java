package com.example.meetapp.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInFragment;
import com.example.meetapp.firebaseActions.UserGroupsRepo;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.notifications.FirebaseDatabaseListening;
import com.example.meetapp.receivers.NetworkChangeReceiver;
import com.example.meetapp.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity extends AppCompatActivity implements OnClickInFragment  {
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityViewModel mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder().build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        UserGroupsRepo.getInstance().getGroups();
        FirebaseDatabaseListening.getInstance().startService();
    }

    @Override
    public void onClickInFragment(String action) {
        if (action.equals(getString(R.string.logoutAction)))
            CurrentUser.logout();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}