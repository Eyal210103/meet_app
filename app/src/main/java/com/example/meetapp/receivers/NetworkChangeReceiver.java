package com.example.meetapp.receivers;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.meetapp.R;

public class NetworkChangeReceiver extends BroadcastReceiver {
    Dialog dialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.no_connection_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
        }
        try{
            if (isOnline(context)) {
                dialog.dismiss();
            }
            else {
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
