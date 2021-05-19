 package com.example.meetapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.meetapp.chatPushNotification.FirebaseDatabaseListening;
import com.example.meetapp.chatPushNotification.MyFirebaseMessagingService;

 public class ServiceStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("com.example.meetapp.notifications2.MyFirebaseMessaging"); // To Service
        i.setClass(context, MyFirebaseMessagingService.class);
        FirebaseDatabaseListening.getInstance().startService();
        context.startService(i);
    }
}