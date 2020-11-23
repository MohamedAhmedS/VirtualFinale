package com.example.virtualmeetingapp.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.AlarmManagerCompat;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.activites.SplashScreenActivity;
import com.example.virtualmeetingapp.notifications.MNotificationManager;
import com.example.virtualmeetingapp.utils.App;
import com.example.virtualmeetingapp.utils.WakeLock;

public class ReminderBroadcast extends BroadcastReceiver {
    private static final String TAG = "ReminderBroadcast";
    private static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";
    private static final String NOTIFICATION_DESC = "NOTIFICATION_DESC";
    private static final String NOTIFICATION_RC = "NOTIFICATION_RC";
    private static final String NEXT_ALARM_TIME = "NEXT_ALARM_TIME";

    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLock.acquire(context);
        Log.d(TAG, "onReceive:");
        String title = App.getAppContext().getResources().getString(R.string.app_name);
        String message = "Meeting Alert";
        int requestCode = 0;
        if (intent.hasExtra(NOTIFICATION_TITLE)) {
            title = intent.getStringExtra(NOTIFICATION_TITLE);
        }
        if (intent.hasExtra(NOTIFICATION_DESC)) {
            message = intent.getStringExtra(NOTIFICATION_DESC);
        }
        if (intent.hasExtra(NOTIFICATION_RC)) {
            requestCode = intent.getIntExtra(NOTIFICATION_RC, 0);
        }

        Intent notificationIntent = new Intent(context, SplashScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode + 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int notificationPriority = Notification.PRIORITY_HIGH;
        int channelPriority = -1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            channelPriority = NotificationManager.IMPORTANCE_HIGH;
        }
        if (intent.hasExtra(NEXT_ALARM_TIME)) {
            setAlarm(context, intent.getLongExtra(NEXT_ALARM_TIME, -1), "Next Alarm", "Next Alarm Desc", requestCode + 1, -1);
        }
        MNotificationManager.sendNotification(context,
                requestCode + 1,
                title,
                message,
                null,
                notificationPriority,
                channelPriority,
                pendingIntent);
        WakeLock.release();
    }

    public static void setAlarm(Context context, long alarmTimeMillis, String notification_title, String notification_desc, int requestCode, long nextAlarmTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.d(TAG, "setAlarm: AlarmManager Is Null");
            return;
        }

        Intent alarmIntent = new Intent(context, ReminderBroadcast.class);
        alarmIntent.putExtra(NOTIFICATION_TITLE, notification_title);
        alarmIntent.putExtra(NOTIFICATION_DESC, notification_desc);
        alarmIntent.putExtra(NOTIFICATION_RC, requestCode);
        if (nextAlarmTime != -1) {
            alarmIntent.putExtra(NEXT_ALARM_TIME, nextAlarmTime);
        }
//        alarmIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "setAlarm() api > 23");
            AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.d(TAG, "setAlarm() api > 19");
            AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
        } else {
            Log.d(TAG, "setAlarm() api < 19");
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
        }
    }
}
