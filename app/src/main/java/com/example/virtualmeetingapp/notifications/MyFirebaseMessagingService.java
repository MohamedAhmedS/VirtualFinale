package com.example.virtualmeetingapp.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.virtualmeetingapp.CallingActivity;
import com.example.virtualmeetingapp.MainActivity;
import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.activites.AppointResponseListActivity;
import com.example.virtualmeetingapp.activites.AppointmentListActivity;
import com.example.virtualmeetingapp.activites.ChatActivity;
import com.example.virtualmeetingapp.models.AppointmentModel;
import com.example.virtualmeetingapp.receivers.CallBroadcastReceiver;
import com.example.virtualmeetingapp.receivers.ReminderBroadcast;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.WakeLock;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";
    private static final String ADMIN_CHANNEL_ID = "admin_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //get current user from shared preferences
        Log.d(TAG, "Message Received");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            sendNormalNotification(remoteMessage);
        } else {
            sendOAndAboveNotification(remoteMessage);
        }
    }

//    private void showPostNotification(String pId, String pTitle, String pDescription) {
//        MNotificationManager notificationManager = (MNotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        int notificationID = new Random().nextInt(3000);
//
//
//        /*Apps targeting SDK 26 or above (Android O and above) must implement notification channels
//         and add its notifications to at least one of them
//         Let's add check if version is Oreo or higher then setup notification channel*/
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            setupPostNotificationChannel(notificationManager);
//        }
//
//        //show post detail activity using post id when notification clicked
//        Intent intent = new Intent(this, PostDetailFragment.class);
//        intent.putExtra("postid", pId);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        //LargeIcon
//        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
//
//        //sound for notification
//        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "" + ADMIN_CHANNEL_ID)
//                .setSmallIcon(R.drawable.placeholder)
//                .setLargeIcon(largeIcon)
//                .setContentTitle(pTitle)
//                .setContentText(pDescription)
//                .setSound(notificationSoundUri)
//                .setContentIntent(pendingIntent);
//
//        //show notification
//        assert notificationManager != null;
//        notificationManager.notify(notificationID, notificationBuilder.build());
//
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupPostNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName = "New Notification";
        String channelDescription = "Device to device post notification";

        NotificationChannel adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(channelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String type = remoteMessage.getData().get("type");
        String message = "" + remoteMessage.getData().get("message");

        PendingIntent pIntent = null;
        NotificationCompat.Builder notificationBuilder;
        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (type != null && type.equals("appointment")) {
            String appointmentId = remoteMessage.getData().get("appointmentId");
            Intent intent = new Intent(this, AppointResponseListActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("appointmentId", appointmentId);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_APPOINTMENTS)
                    .document(appointmentId)
                    .get()
                    .addOnSuccessListener(task -> {
                        if (task.exists()) {
                            AppointmentModel appointmentModel = task.toObject(AppointmentModel.class);
                            if (appointmentModel != null && appointmentModel.isChatApvByOfficer()) {
                                String appointmentTime = appointmentModel.getAppointmentDate() + " " + appointmentModel.getAppointmentTime();

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);
                                try {
                                    long mScheduleDate = sdf.parse(appointmentTime).getTime();
                                    //            long mScheduleDate = System.currentTimeMillis();
                                    Calendar cal = Calendar.getInstance();
                                    // remove next line if you're always using the current time.
                                    cal.setTimeInMillis(mScheduleDate);
                                    cal.add(Calendar.HOUR, -12);
                                    Date twelveHourBack = cal.getTime();
                                    int requestCode = new Random().nextInt(900) + 1;

                                    cal.setTimeInMillis(mScheduleDate);
                                    cal.add(Calendar.MINUTE, -30);
                                    Date halfHourBack = cal.getTime();
                                    if (twelveHourBack.getTime() < System.currentTimeMillis()) {
                                        ReminderBroadcast.setAlarm(this, halfHourBack.getTime(), "Appointment Alert",
                                                "30 minutes Left in Appointment", requestCode, -1);
                                    } else {
                                        ReminderBroadcast.setAlarm(this, twelveHourBack.getTime(), "Appointment Alert",
                                                "12 hours Left in Appointment", requestCode, halfHourBack.getTime());
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "sendNormalNotification: " + e.toString());
//                                    e.printStackTrace();
                                }
                            }
                        }
                    });

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentText(message)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setAutoCancel(true)
                    .setSound(defSoundUri)
                    .setContentIntent(pIntent);
            assert notificationManager != null;
            WakeLock.acquire(this);
            notificationManager.notify(1, notificationBuilder.build());
            WakeLock.release();
        } else if ( type != null && type.equals("calling")) {
            String callerID = remoteMessage.getData().get("callerId");
            String receiverID = remoteMessage.getData().get("receiverId");
            String appointmentId = remoteMessage.getData().get("appointmentId");
            String videoCall = remoteMessage.getData().get("videoCall");

            Intent newIntent = new Intent(getApplicationContext(), CallingActivity.class);
            newIntent.putExtra("callerID", callerID);
            newIntent.putExtra("receiverID", receiverID);
            newIntent.putExtra("ringing", true);
            newIntent.putExtra("videoCall", videoCall);
            newIntent.putExtra("appointmentId", appointmentId);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(newIntent);
        }
    }

    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String type = remoteMessage.getData().get("type");
        String message = "" + remoteMessage.getData().get("message");

        PendingIntent pIntent = null;
        if (type != null && type.equals("appointment")) {
            String appointmentId = remoteMessage.getData().get("appointmentId");
            Intent intent = new Intent(this, AppointResponseListActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("appointmentId", appointmentId);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_APPOINTMENTS)
                    .document(appointmentId)
                    .get()
                    .addOnSuccessListener(task -> {
                        if (task.exists()) {
                            AppointmentModel appointmentModel = task.toObject(AppointmentModel.class);
                            if (appointmentModel != null && appointmentModel.isChatApvByOfficer()) {
                                String appointmentTime = appointmentModel.getAppointmentDate() + " " + appointmentModel.getAppointmentTime();

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);
                                try {
                                    long mScheduleDate = sdf.parse(appointmentTime).getTime();
                                    //            long mScheduleDate = System.currentTimeMillis();
                                    Calendar cal = Calendar.getInstance();
                                    // remove next line if you're always using the current time.
                                    cal.setTime(new Date(mScheduleDate));
                                    cal.add(Calendar.HOUR, -12);
                                    Date twelveHourBack = cal.getTime();
                                    int requestCode = new Random().nextInt(900) + 1;

                                    cal.setTime(new Date(mScheduleDate));
                                    cal.add(Calendar.MINUTE, -30);
                                    Date halfHourBack = cal.getTime();
                                    if (twelveHourBack.getTime() < System.currentTimeMillis()) {
                                        ReminderBroadcast.setAlarm(this, halfHourBack.getTime(), "Appointment Alert",
                                                "30 minutes Left in Appointment", requestCode, -1);
                                    } else {
                                        ReminderBroadcast.setAlarm(this, twelveHourBack.getTime(), "Appointment Alert",
                                                "12 hours Left in Appointment", requestCode, halfHourBack.getTime());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });


            Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
            NotificationCompat.Builder builder = notification1.getONotifications(title, message, pIntent, defSoundUri, R.mipmap.ic_launcher_round);

            WakeLock.acquire(this);
            notification1.getManager().notify(1, builder.build());
            WakeLock.release();
        } else if (type != null && type.equals("calling")) {
            String callerID = remoteMessage.getData().get("callerId");
            String receiverID = remoteMessage.getData().get("receiverId");
            String appointmentId = remoteMessage.getData().get("appointmentId");
            String videoCall = remoteMessage.getData().get("videoCall");

            Intent newIntent = new Intent(this, CallingActivity.class);
            newIntent.putExtra("callerID", callerID);
            newIntent.putExtra("receiverID", receiverID);
            newIntent.putExtra("ringing", true);
            newIntent.putExtra("videoCall", videoCall);
            newIntent.putExtra("appointmentId", appointmentId);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pIntent = PendingIntent.getActivity(this, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            newIntent.setAction("calling");
            sendBroadcast(newIntent);

            Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

            OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
            NotificationCompat.Builder builder = notification1.getONotifications(title, message, pIntent, defSoundUri, R.drawable.call)
                    .setSmallIcon(R.drawable.call)
                    .setContentTitle("Incoming Call")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pIntent, true);
//            NotificationCompat.Builder builder = notification1.getONotifications(title, message, pIntent, defSoundUri, R.mipmap.ic_launcher_round);
            builder.setVibrate(new long[]{1000, 500, 1000, 500, 1000, 500});
            Intent answerIntent = new Intent(this, CallingActivity.class);
            answerIntent.putExtra("callingIntent", "answerCall");
            answerIntent.putExtra("callerID", callerID);
            answerIntent.putExtra("receiverID", receiverID);
            answerIntent.putExtra("ringing", true);
            answerIntent.putExtra("videoCall", videoCall);
            answerIntent.putExtra("appointmentId", appointmentId);
            answerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            answerIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pIntent = PendingIntent.getActivity(this, 0, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.call, "Answer", pIntent);
            Intent cancelIntent = new Intent(this, CallingActivity.class);
            cancelIntent.putExtra("callingIntent", "rejectCall");
            cancelIntent.putExtra("callerID", callerID);
            cancelIntent.putExtra("receiverID", receiverID);
            cancelIntent.putExtra("ringing", true);
            cancelIntent.putExtra("videoCall", videoCall);
            cancelIntent.putExtra("appointmentId", appointmentId);
            cancelIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cancelIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pIntent = PendingIntent.getActivity(this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.cancel_call, "Reject", pIntent);

            if (!Global.appInForeground) {
                WakeLock.acquire(this);
                notification1.getManager().notify(1, builder.build());
                WakeLock.release();
            }
        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        updateToken(s);
    }

    private void updateToken(String tokenRefresh) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        Global.updateDeviceToken(user.getUid(), tokenRefresh);
    }
}
