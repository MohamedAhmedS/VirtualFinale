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

public class PrisonersActivity extends BaseActivity {

    Button logoutPrisoner, viewConversations;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prisoners);

        initXML();
        initVariables();
        setListeners();
    }

    @Override
    public void initXML() {
        logoutPrisoner = findViewById(R.id.logoutPrisoner);
        viewConversations = findViewById(R.id.viewConversations);
    }

    @Override
    public void initVariables() {

    }

    private void setListeners() {
        viewConversations.setOnClickListener(v -> {
            startActivity(new Intent(this, ConversationsActivity.class));
        });

        logoutPrisoner.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Logging Out");
            progressDialog.setText("Please wait ...");
            progressDialog.show();
            if (NetworkUtils.getInstance().internetConnectionAvailable(3000)) {
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
        });
    }

}