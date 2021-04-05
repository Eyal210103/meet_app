package com.example.meetapp;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.meetapp.model.Consts;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.MainActivity;

import java.util.Calendar;
import java.util.Date;

public class MeetingReminderNotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isGroup = false;
        Meeting meeting = (Meeting) intent.getSerializableExtra(Consts.BUNDLE_MEETING);
        if (meeting instanceof GroupMeeting)
            isGroup =true;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(meeting.getMillis());
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,1, resultIntent, 0);
        @SuppressLint("DefaultLocale") String dateString = String.format("%02d:%02d",calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));

        NotificationCompat.Builder builder;
        if(isGroup) {
            builder = new NotificationCompat.Builder(context, context.getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(meeting.getSubject())
                    .setContentText(context.getString(R.string.group_meeting_remainder) + dateString)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true);
        }else {
            builder = new NotificationCompat.Builder(context, context.getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(meeting.getSubject())
                    .setContentText(context.getString(R.string.meeting_remainder) + dateString)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(52, builder.build());
    }


}
