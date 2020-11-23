package com.example.virtualmeetingapp.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.virtualmeetingapp.ClientTypeActivity;
import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.adapter.OngoingAppointmentAdapter;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.NetworkUtils;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import carbon.dialog.ProgressDialog;

public class OfficersActivity extends BaseActivity {

    Button addPrisoners, logoutOfficer, viewConversations, approveVisitorsAppointment,
            viewList, btnOngoingAppointments;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void initXML() {

    }

    @Override
    public void initVariables() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officers);

        addPrisoners = findViewById(R.id.addPrisoners);
        logoutOfficer = findViewById(R.id.logoutOfficer);
        viewConversations = findViewById(R.id.viewConversations);
        approveVisitorsAppointment = findViewById(R.id.approveVisitorsAppointment);
        viewList = findViewById(R.id.viewList);
        btnOngoingAppointments = findViewById(R.id.btnOngoingAppointments);


        addPrisoners.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddPrisonersActivity.class);
            startActivity(intent);
        });

        viewList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ViewListActivity.class);
            startActivity(intent);
        });

        approveVisitorsAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AppointmentListActivity.class);
            startActivity(intent);
        });

        viewConversations.setOnClickListener(v -> {
            startActivity(new Intent(this, ConversationsActivity.class));
        });

        btnOngoingAppointments.setOnClickListener(v -> {
            startActivity(new Intent(this, OngoingAppointmentActivity.class));
        });

        logoutOfficer.setOnClickListener(v -> {
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
        });
    }
}