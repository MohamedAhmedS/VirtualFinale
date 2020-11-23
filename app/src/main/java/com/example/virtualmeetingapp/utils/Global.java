package com.example.virtualmeetingapp.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import carbon.view.View;

public class Global {
    private static Object currentUser;
    public static boolean listeningToCall = false;
    public static boolean appointmentDetailsSaved = false;
    public static boolean appInForeground = true;


    public static Object getCurrentUser() {
        if (currentUser == null) {
            currentUser = new SystemPrefs().getUserSession();
        }
        return currentUser;
    }

    public static void updateDeviceToken(String currentUserId) {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUserId)
                    .child("device_token")
                    .setValue(token);
        });
    }

    public static void clearCurrentUser() {
        currentUser = null;
    }

    public static String getDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(date);
    }

    public static String getTopActivityName(Context context) {
        ActivityManager result = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = result.getRunningTasks(Integer.MAX_VALUE);
        if (services != null && services.size() > 0) {
            return services.get(0).topActivity != null ? services.get(0).topActivity.getClassName() : "";
        }
        return "";
    }

    public static boolean isGreaterDate(Date operate_date_1, Date operate_date_2) {
        return operate_date_1.getTime() > operate_date_2.getTime();
    }

    public static String randomString() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder(20);
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static void retriveVideoFrameFromVideo(String videoPath, ImageView imageHolder, ProgressBar progressBar) throws Throwable {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap bitmap = null;
                MediaMetadataRetriever mediaMetadataRetriever = null;
                try {
                    mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                    //   mediaMetadataRetriever.setDataSource(videoPath);
                    bitmap = mediaMetadataRetriever.getFrameAtTime();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (mediaMetadataRetriever != null) {
                        mediaMetadataRetriever.release();
                    }
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap aVoid) {
                super.onPostExecute(aVoid);
                imageHolder.setImageBitmap(aVoid);
                progressBar.setVisibility(View.GONE);
            }
        }.execute();
    }
}
