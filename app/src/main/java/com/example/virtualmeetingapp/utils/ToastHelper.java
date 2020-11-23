package com.example.virtualmeetingapp.utils;

import android.widget.Toast;

public class ToastHelper {

    public static void showToast(String message) {
        Toast.makeText(App.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(String message) {
        Toast.makeText(App.getAppContext(), message, Toast.LENGTH_LONG).show();
    }
}
