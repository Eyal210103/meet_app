package com.example.meetapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.meetapp.model.Const;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.MainActivity;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class MeetingReminderNotificationBroadcastReceiver extends BroadcastReceiver {

    Meeting meeting;
    private static final String CHANNEL_ID = "com.example.meetapp";
    private static final String CHANNEL_NAME = "MeetApp Meetings";

    public MeetingReminderNotificationBroadcastReceiver(Meeting meeting) {
        this.meeting = meeting;
    }

    public MeetingReminderNotificationBroadcastReceiver() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isGroup = false;
//        Meeting meeting = (Meeting) intent.getSerializableExtra(Const.BUNDLE_MEETING);
//        if (meeting instanceof GroupMeeting)
//            isGroup =true;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        Calendar calendar = Calendar.getInstance();
       // calendar.setTimeInMillis(meeting.getMillis());
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,1, resultIntent, 0);
        @SuppressLint("DefaultLocale") String dateString = String.format(" %02d:%02d",calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));

        NotificationCompat.Builder builder;
        if(isGroup) {
            builder = new NotificationCompat.Builder(context, CHANNEL_NAME)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    //.setContentTitle(meeting.getSubject())
                    .setContentTitle(context.getString(R.string.meetings))
                    .setContentText(context.getString(R.string.group_meeting_remainder) + dateString)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true).setChannelId(CHANNEL_ID);
        }else {
            builder = new NotificationCompat.Builder(context, CHANNEL_NAME)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setChannelId(CHANNEL_ID)
                   // .setContentTitle(meeting.getSubject())
                    .setContentTitle(context.getString(R.string.meetings))
                    .setContentText(context.getString(R.string.meeting_remainder) + dateString)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(32, builder.build());
    }

    public void setAlarm(Context context,Meeting meeting) {
        Intent intent = new Intent(context, MeetingReminderNotificationBroadcastReceiver.class);
        intent.putExtra(Const.BUNDLE_MEETING,meeting);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        long currentTime = System.currentTimeMillis();
        alarmManager.set(AlarmManager.RTC_WAKEUP, meeting.getMillis(), pendingIntent);
    }

    public void cancelAlarm(Context context,long millis) {
        Intent intent = new Intent(context, MeetingReminderNotificationBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context,0 , intent, 0); //TODO individual meeting numbering
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

}
