package com.example.virtualmeetingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    private static NetworkUtils instance;

    public static NetworkUtils getInstance() {
        if (instance == null) {
            instance = new NetworkUtils();
        }
        return instance;
    }

    private NetworkUtils() {
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if (connectivityManager != null) {
            NetworkInfo activityNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = (activityNetwork != null) && (activityNetwork.isConnected());
        }

        if (!isConnected) {
            ToastHelper.showToast("You are offline");
        }

        return isConnected;
    }

    public boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    return InetAddress.getByName("google.com");
                } catch (UnknownHostException e) {
                    return null;
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Log.e(TAG, "internetConnectionAvailable: " + e.getMessage());
        }
        return inetAddress != null && !inetAddress.equals("");
    }
}
