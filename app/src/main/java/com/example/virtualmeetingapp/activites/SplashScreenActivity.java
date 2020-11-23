package com.example.virtualmeetingapp.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.virtualmeetingapp.ClientTypeActivity;
import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends Activity {

    private FirebaseUser userLogin;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        checkUserLogin();
    }

    private void checkUserLogin() {
        String userType = new SystemPrefs().getUserType();
        userLogin = mAuth.getCurrentUser();
        if (userLogin != null) {
            if (userType.equals(Constants.USER_TYPE_ADMIN)) {
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                    finish();
                }, 3000);
            } else if (userType.equals(Constants.USER_TYPE_OFFICER)) {
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), OfficersActivity.class));
                    finish();
                }, 3000);
            } else if (userType.equals(Constants.USER_TYPE_VISITOR)) {
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), VisitorActivity.class));
                    finish();
                }, 3000);
            } else if (userType.equals(Constants.USER_TYPE_PRISONER)) {
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), PrisonersActivity.class));
                    finish();
                }, 3000);
            } else {
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), ClientTypeActivity.class));
                    finish();
                }, 3000);
            }
        } else {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(getApplicationContext(), ClientTypeActivity.class));
                finish();
            }, 3000);
        }
    }

}
