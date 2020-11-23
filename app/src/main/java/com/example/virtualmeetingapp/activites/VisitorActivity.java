package com.example.virtualmeetingapp.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.virtualmeetingapp.ClientTypeActivity;
import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.NetworkUtils;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import carbon.dialog.ProgressDialog;

public class VisitorActivity extends BaseActivity {

    Button logoutVisitor, viewList, profile, appointmentResponse;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void initXML() {
        viewList = findViewById(R.id.viewList);
        profile = findViewById(R.id.viewProfile);
        appointmentResponse = findViewById(R.id.appointmentResponse);
        logoutVisitor = findViewById(R.id.logoutVisitor);
    }

    @Override
    public void initVariables() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);

        initXML();
        initVariables();

//        if (NetworkUtils.getInstance().isNetworkConnected(this) &&
//                NetworkUtils.getInstance().internetConnectionAvailable(3000)) {
//            ToastHelper.showToast("You are online");
//        } else {
//            ToastHelper.showToast("You are offline");
//        }

        viewList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ViewListActivity.class);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), VisitorProfileActivity.class);
            startActivity(intent);
        });

        appointmentResponse.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AppointResponseListActivity.class);
            startActivity(intent);
        });

        logoutVisitor.setOnClickListener(v -> {
            if(!isFinishing()) {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Logging Out");
                progressDialog.setText("Please wait ...");
                progressDialog.show();
                if (NetworkUtils.getInstance().isNetworkConnected(this)) {
                    mAuth.signOut();
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(currentUser.getUid())
                            .child("device_token")
                            .setValue("0")
                            .addOnCompleteListener(task -> {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Global.clearCurrentUser();
                                    new SystemPrefs().clearUserSession();
                                    Intent intent = new Intent(getApplicationContext(), ClientTypeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }
            }
        });
    }
}