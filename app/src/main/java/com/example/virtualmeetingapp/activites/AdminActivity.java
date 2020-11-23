package com.example.virtualmeetingapp.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.virtualmeetingapp.ClientTypeActivity;
import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.NetworkUtils;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import carbon.dialog.ProgressDialog;

public class AdminActivity extends BaseActivity {

    Button addOfficer, logoutAdmin, viewList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initXML();
        initVariables();
        setListeners();
    }

    @Override
    public void initXML() {
        viewList = findViewById(R.id.viewList);
        addOfficer = findViewById(R.id.addOfficer);
        logoutAdmin = findViewById(R.id.logoutAdmin);
    }

    @Override
    public void initVariables() {

    }

    private void setListeners() {
        addOfficer.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddOfficersActivity.class);
            startActivity(intent);
        });

        viewList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ViewListActivity.class);
            startActivity(intent);
        });

        logoutAdmin.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Logging Out");
            progressDialog.setText("Please wait ...");
            progressDialog.show();
            if (NetworkUtils.getInstance().internetConnectionAvailable(3000)) {
                mAuth.signOut();
                progressDialog.dismiss();
                Global.clearCurrentUser();
                new SystemPrefs().clearUserSession();
                Intent intent = new Intent(getApplicationContext(), ClientTypeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}