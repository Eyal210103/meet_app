package com.example.meetapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {
    Dialog dialog;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.no_connection_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       // dialog.setCancelable(false);
        try{
            if (isOnline(context)) {
                dialog.dismiss();
            }
            if (!isOnline(context)){
                dialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo!= null && (networkInfo.isConnected()||networkInfo.isAvailable());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
