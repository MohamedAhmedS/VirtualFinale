package com.example.virtualmeetingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.virtualmeetingapp.models.Officer;
import com.example.virtualmeetingapp.models.Prisoner;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.models.Visitor;
import com.google.gson.Gson;

public class SystemPrefs {
    private static final String TAG = "SystemPrefs";

    private String LOGGED_IN_USER = "loggedInUser";
    private String USER_TYPE = "userType";
    private SharedPreferences sharedPreferences;

    public SystemPrefs() {
        String PREF_NAME = "virtualMeeting";
        sharedPreferences = App.getAppContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, @NonNull String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public String getUserType() {
        return getString(USER_TYPE, "");
    }

    public void saveUserSession(Object object, String userType) {
        //TODO: remove these 2 lines after logout work done
        Global.clearCurrentUser();
        clearUserSession();

        String objectJson = new Gson().toJson(object);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGGED_IN_USER, objectJson);
        editor.putString(USER_TYPE, userType);
        editor.apply();
    }

    public Object getUserSession() {
        String userType = getUserType();
        switch (userType) {
            case Constants.USER_TYPE_OFFICER:
                return getObjectData(LOGGED_IN_USER, Officer.class);
            case Constants.USER_TYPE_VISITOR:
                return getObjectData(LOGGED_IN_USER, Visitor.class);
            case Constants.USER_TYPE_PRISONER:
                return getObjectData(LOGGED_IN_USER, Prisoner.class);
        }

        return getObjectData(LOGGED_IN_USER, User.class);
    }

    public void clearUserSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(LOGGED_IN_USER);
        editor.remove(USER_TYPE);
        editor.apply();
    }

    public void setObjectData(String key, Object object) {
        Gson converter = new Gson();
        String objectStr = converter.toJson(object);
        setString(key, objectStr);
    }

    public <T> T getObjectData(String key, Class<T> objectClass) {
        String objectString = getString(key, "");
        return new Gson().fromJson(objectString, objectClass);
    }
}
