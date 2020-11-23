package com.example.virtualmeetingapp.activites;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.virtualmeetingapp.CallingActivity;
import com.example.virtualmeetingapp.models.Officer;
import com.example.virtualmeetingapp.models.Prisoner;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.models.Visitor;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    protected User admin;
    protected Officer officer;
    protected Visitor visitor;
    protected User currentUser;
    protected Prisoner prisoner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkForReceivingCall();

        String userType = new SystemPrefs().getUserType();
        currentUser = (User) Global.getCurrentUser();
        switch (userType) {
            case Constants.USER_TYPE_OFFICER:
                officer = (Officer) Global.getCurrentUser();
                break;
            case Constants.USER_TYPE_PRISONER:
                prisoner = (Prisoner) Global.getCurrentUser();
                break;
            case Constants.USER_TYPE_VISITOR:
                visitor = (Visitor) Global.getCurrentUser();
                break;
            case Constants.USER_TYPE_ADMIN:
                admin = (User) Global.getCurrentUser();
                break;
            default:
                ToastHelper.showToast("Unauthorized Logged User...");
                finish();
                break;
        }
    }

    public abstract void initXML();

    public abstract void initVariables();


    private void checkForReceivingCall() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore.getInstance()
                    .collection(Constants.COLLECTION_CALLING)
                    .whereEqualTo("receiverID", currentUserId)
                    .whereEqualTo("status", "ringing")
                    .limit(1)
                    .addSnapshotListener((snapshot, exception) -> {
                        if (!Global.listeningToCall) {
                            if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
                                Intent intent = new Intent(this, CallingActivity.class);
                                intent.putExtra("callerID", snapshot.getDocuments().get(0).get("callerID").toString());
                                intent.putExtra("ringing", true);
                                intent.putExtra("videoCall", snapshot.getDocuments().get(0).get("videoCall").toString());
                                intent.putExtra("appointmentId", snapshot.getDocuments().get(0).get("appointmentId").toString());
                                startActivity(intent);
                                Global.listeningToCall = true;
                            }
                        }
                    });
        }
    }
}
