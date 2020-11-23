package com.example.virtualmeetingapp.utils;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public abstract class WakeLock {
    private static final String TAG = "WakeLock";
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context c) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        if(pm == null){
            Log.d(TAG, "acquire: PowerManager is NULL");
            return;
        }
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, App.APP_TAG);
        wakeLock.acquire(5000);
    }

    public static void release() {
        if (wakeLock != null) {
            wakeLock.release();
        }
        wakeLock = null;
    }
}