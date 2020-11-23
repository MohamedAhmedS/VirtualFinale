package com.example.virtualmeetingapp.utils;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.installations.FirebaseInstallations;

public class App extends MultiDexApplication implements LifecycleObserver {
    public static String APP_TAG = "Virtual Meeting App";
    private static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;

        FirebaseApp.initializeApp(applicationContext);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        Global.appInForeground = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        Global.appInForeground = true;
    }

    public static Context getAppContext() {
        return applicationContext;
    }
}
