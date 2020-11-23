package com.example.virtualmeetingapp.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.virtualmeetingapp.MainActivity;
import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.activites.ChatActivity;
import com.example.virtualmeetingapp.models.MessageModel;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.TextUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BackgroundService extends Service {

    DatabaseReference chatRef, userRef, postRef, followRef;
    private static final String ADMIN_CHANNEL_ID = "admin_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        startForegroundBackgroundService();

        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
        userRef = FirebaseDatabase.getInstance().getReference().child(Constants.COLLECTION_USER);
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        followRef = FirebaseDatabase.getInstance().getReference().child("Follow");

        SharedPreferences sp = getSharedPreferences("PREFS", MODE_PRIVATE);
        final String savedCurrentUser = sp.getString(TextUtil.PREF_LATEST_USER_ID, "");
        final boolean enabledNotification = sp.getBoolean(TextUtil.PREF_NOTIFICATION_ENABLED, false);

        /*Now there are two types of notifications
                      > notificationType="PostNotification"
                      > notificationType="ChatNotification"*/
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0){
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        final MessageModel messageModel = ds.getValue(MessageModel.class);
                        assert messageModel != null;
                        String sender = messageModel.getSender();
                        String receiver = messageModel.getReceiver();
                        User currentUser = (User) Global.getCurrentUser();
                        if(enabledNotification){
//                            if (currentUser != null && !sender.equals(currentUser.getId())) {
                            if (currentUser != null && !sender.equals(currentUser.getUserEmail())) {
                                if (savedCurrentUser.equals(receiver)) {
                                    if(!messageModel.isSeen()){
                                        userRef.child(messageModel.getSender()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String senderName = dataSnapshot.child("userName").getValue().toString();
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    sendOAndAboveNotification(messageModel, senderName);
                                                } else {
                                                    sendNormalNotification(messageModel, senderName);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        followRef.child(Global.getCurrentUser().getId()).child("following").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.getChildrenCount() != 0){
//                    for(DataSnapshot ds : dataSnapshot.getChildren()){
//                        final String follower = ds.getKey();
//                        postRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if(dataSnapshot.getChildrenCount() != 0){
//                                    for(DataSnapshot ds : dataSnapshot.getChildren()){
//                                        final Post post = ds.getValue(Post.class);
//                                        assert post != null;
//                                        final String publisher = post.getPublisher();
//                                        if(publisher.equals(follower)){
//                                            userRef.child(publisher).addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                    User publisherInfo = dataSnapshot.getValue(User.class);
//                                                    assert publisherInfo != null;
//                                                    showPostNotification(post.getPostid(), publisherInfo.getUserName() + " followed you!", post.getCarType() + "\n" + post.getDescription());
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void startForegroundBackgroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle("Service")
                .setContentText("Running...");
        Notification notification=builder.build();
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel("ARBA_Notification", "Sync Service", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Service Name");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            notification = new Notification.Builder(this,"ARBA_Notification")
                    .setContentTitle("Service")
                    .setContentText("Running...")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(restartServiceIntent);
        } else {
            startService(restartServiceIntent);
        }
        super.onTaskRemoved(rootIntent);
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

    private void sendNormalNotification(MessageModel messageModel, String senderName) {
        String sender = messageModel.getSender();
        String message = messageModel.getMessage();

        int i = Integer.parseInt(sender.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUid", sender);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.placeholder)
                .setContentText(message)
                .setContentTitle("New message from " + senderName)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j = 0;
        if (i > 0) {
            j = i;
        }
        assert notificationManager != null;
        notificationManager.notify(j, builder.build());
    }

    private void sendOAndAboveNotification(MessageModel messageModel, String senderName) {

        String sender = messageModel.getSender();
        String message = messageModel.getMessage();

        int i = Integer.parseInt(sender.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUid", sender);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
        NotificationCompat.Builder builder = notification1.getONotifications("New message from " + senderName, message, pIntent, defSoundUri, R.drawable.placeholder);

        int j = 0;
        if (i > 0) {
            j = i;
        }
        notification1.getManager().notify(j, builder.build());
    }
}
