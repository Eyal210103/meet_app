package com.example.meetapp.receivers;

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

import com.example.meetapp.R;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.MainActivity;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class MeetingReminderNotificationBroadcastReceiver extends BroadcastReceiver {

    private static final int ALARM_REQUEST = 0;
    public static final long TIME_BEFORE = 7200000;
    private static final String CHANNEL_ID = "com.example.meetapp.receivers";
    private static final String CHANNEL_NAME = "MeetApp Meetings";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isGroup = false;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis() + TIME_BEFORE);
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 1, resultIntent, 0);
        @SuppressLint("DefaultLocale") String dateString = String.format(" %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_NAME)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(context.getString(R.string.meetings))
                .setChannelId(CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        if (isGroup) {
            builder.setContentText(context.getString(R.string.group_meeting_remainder) + dateString);
        } else {
            builder.setContentText(context.getString(R.string.meeting_remainder) + dateString);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(86, builder.build());
    }

    public static void setAlarm(Context context, Meeting meeting) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        Intent intent = new Intent(context, MeetingReminderNotificationBroadcastReceiver.class);
        intent.putExtra(Const.BUNDLE_MEETING, meeting);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, meeting.getMillis() - TIME_BEFORE, pendingIntent);
    }

//    public static void cancelAlarm(Context context, long millis) {
//        Intent intent = new Intent(context, MeetingReminderNotificationBroadcastReceiver.class);
//        PendingIntent sender = PendingIntent.getBroadcast(context, ALARM_REQUEST, intent, 0); //TODO individual meeting numbering
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        alarmManager.cancel(sender);
//    }

}
