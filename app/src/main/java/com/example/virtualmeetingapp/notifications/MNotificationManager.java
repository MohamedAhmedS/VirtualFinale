package com.example.virtualmeetingapp.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.utils.App;

import java.util.Random;

public class MNotificationManager {
    private static final String TAG = "MNotificationManager";
    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = App.getAppContext().getResources().getString(R.string.app_name);

    public static void sendNotification(Context context, Integer notificationID, String notificationTitle,
                                        String notificationDesc, Integer notificationIcon, int priority,
                                        int channelPriority, PendingIntent pendingIntent) {
        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (notificationIcon == null) {
            notificationIcon = R.mipmap.ic_launcher_round;
        }
        //------------------------------
        if (notificationID == null) {
            notificationID = new Random().nextInt();
            if (notificationID < 0) {
                notificationID *= -1;
            }
        }
        //------------------------------
        if (notificationTitle == null) {
            notificationTitle = context.getResources().getString(R.string.app_name);
        }
        //------------------------------
        if (notificationDesc == null) {
            notificationDesc = "Hi! How are you doing";
        }
        //------------------------------
        Notification.Builder builder = new Notification.Builder(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID);
        }
        builder.setSmallIcon(notificationIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDesc)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setPriority(priority)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null) {
                Log.d(TAG, "sendNotification: NotificationManagerCompat Is Null");
                return;
            }
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, channelPriority);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(0, builder.build());
        } else {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null) {
                Log.d(TAG, "sendNotification: NotificationManager Is NULL");
                return;
            }
            notificationManager.notify(notificationID, builder.build());
        }
    }
}
