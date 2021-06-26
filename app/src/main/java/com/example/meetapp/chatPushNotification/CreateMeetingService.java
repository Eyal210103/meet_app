package com.example.meetapp.chatPushNotification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.meetapp.R;
import com.example.meetapp.model.Const;
import com.example.meetapp.ui.MainActivity;

import java.util.Calendar;
import java.util.Date;

public class CreateMeetingService extends Service {

    private static final String CHANNEL_ID = "com.example.meetapp.chatPushNotification";
    private static final String CHANNEL_NAME = "MeetApp Confirmation";

    private MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer == null) {
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }
        long date = intent.getLongExtra(Const.BUNDLE_MEETING,0);
        sendOreoNotification(date);
        mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendOreoNotification(long date){
        NotificationCompat.Builder builder = getOreoNotification(date);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(12, builder.build());
    }
    private NotificationCompat.Builder getOreoNotification(long meetingDate) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(meetingDate));

        String title = "Congrats";
        @SuppressLint("DefaultLocale")
        String body = "Your Meeting at ";//"+ String.format("%d,%d %2d:%2d",calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)) +" Has Successfully Created";

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        return new NotificationCompat.Builder(this, CHANNEL_NAME)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }
}
