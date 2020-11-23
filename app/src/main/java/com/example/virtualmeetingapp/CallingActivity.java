package com.example.virtualmeetingapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.virtualmeetingapp.activites.BaseActivity;
import com.example.virtualmeetingapp.activites.VideoChatActivity;
import com.example.virtualmeetingapp.models.AppointmentModel;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CallingActivity extends BaseActivity {
    private static final String TAG = "CallingActivity";

    private TextView nameContact, tvCallType;
    private ImageView profileImage;
    private ImageView cancelCallBtn, acceptCallBtn;

    //    private DatabaseReference usersRef;
    private CollectionReference callingRef;

    private MediaPlayer mediaPlayer;

    private String callerID;
    private String receiverID;
    private boolean videoCall = true;
    private AppointmentModel appointmentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        initXML();
        initVariables();
        setListeners();

        if (getIntent().hasExtra("callingIntent")) {
            if (getIntent().getStringExtra("callingIntent").equals("answerCall")) {
                fetchIntentSetupData();
                acceptCallBtn.callOnClick();
            } else if (getIntent().getStringExtra("callingIntent").equals("rejectCall")) {
                cancelCallBtn.callOnClick();
            }
        }
        fetchIntentSetupData();
        fetchAndShowUsername();
    }

    private void fetchIntentSetupData() {
        Intent activityIntent = getIntent();
        if (activityIntent == null) {
            ToastHelper.showToast("Calling Crashed!");
            finish();
            return;
        }
        Map<String, String> callingMap = new HashMap<>();
        if (activityIntent.hasExtra("ringing") &&
                activityIntent.getBooleanExtra("ringing", false)) {
            if (activityIntent.hasExtra("videoCall") && activityIntent.getStringExtra("videoCall").equals("false")) {
                tvCallType.setText("Voice Call");
            } else {
                tvCallType.setText("Video Call");
            }
            callerID = activityIntent.getStringExtra("callerID");
            receiverID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            acceptCallBtn.setVisibility(View.VISIBLE);
            checkIfCallEnded();
        } else {
            callerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            receiverID = activityIntent.getStringExtra("receiverID");
            if (activityIntent.hasExtra("videoCall")) {
                videoCall = activityIntent.getBooleanExtra("videoCall", true);
            }
            if (activityIntent.hasExtra("appointment")) {
                appointmentModel = (AppointmentModel) activityIntent.getSerializableExtra("appointment");
            }
            //        usersRef = FirebaseDatabase.getInstance().getReference().child(Constants.COLLECTION_USER);
            callingRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_CALLING);

            callingMap.put("callerID", callerID);
            callingMap.put("receiverID", receiverID);
            callingMap.put("status", "ringing");
            callingMap.put("appointmentId", String.valueOf(appointmentModel.getAppointmentId()));
            if (videoCall) {
                tvCallType.setText("Video Call");
                callingMap.put("videoCall", "true");
            } else {
                tvCallType.setText("Voice Call");
                callingMap.put("videoCall", "false");
            }

            Map<String, String> notificationMap = new HashMap<>();
            notificationMap.put("callerId", callerID);
            notificationMap.put("receiverId", receiverID);
            notificationMap.put("appointmentId", String.valueOf(appointmentModel.getAppointmentId()));
            if (videoCall) {
                notificationMap.put("videoCall", "true");
            } else {
                notificationMap.put("videoCall", "false");
            }
            notificationMap.put("title", "Incoming Call");
            notificationMap.put("type", "calling");
            notificationMap.put("message", "");
            FirebaseDatabase.getInstance().getReference(Constants.NOTIFICATIONS).child(receiverID).push().setValue(notificationMap);


            callingRef.document(callerID).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().exists()) {
                    callingRef.document(callerID).set(callingMap);
//                    callingMap.put("status", "ringing");
                    checkIfCallEnded();
                }
            });
        }
    }

    @Override
    public void initXML() {
        nameContact = findViewById(R.id.name_calling);
        tvCallType = findViewById(R.id.txt);
        cancelCallBtn = findViewById(R.id.cancel_call);
        acceptCallBtn = findViewById(R.id.make_call);
    }

    @Override
    public void initVariables() {
        mediaPlayer = MediaPlayer.create(this, R.raw.ringing);
        mediaPlayer.setLooping(true);
    }

    private void setListeners() {
        cancelCallBtn.setOnClickListener(v -> {
            mediaPlayer.stop();
            Global.listeningToCall = false;

            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_CALLING).document(callerID).delete();
            finish();
        });

        acceptCallBtn.setOnClickListener(view -> {
            mediaPlayer.stop();

            Map<String, Object> callingMap = new HashMap<>();
            callingMap.put("status", "ongoing");

            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_CALLING).document(callerID).update(callingMap);

            Intent intent = new Intent(this, VideoChatActivity.class);
            intent.putExtra("callerID", callerID);
            intent.putExtra("receiverID", receiverID);
            intent.putExtra("appointmentId", getIntent().getStringExtra("appointmentId"));
            if (getIntent() != null && getIntent().hasExtra("videoCall")) {
                intent.putExtra("videoCall", getIntent().getStringExtra("videoCall"));
            }
            startActivity(intent);
            finish();
        });
    }

    private void fetchAndShowUsername() {
        String receiverNameId = receiverID;
        if (!callerID.equals(currentUser.getUid())) {
            receiverNameId = callerID;
        }
        FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_USER)
                .whereEqualTo("uid", receiverNameId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        User user = task.getResult().toObjects(User.class).get(0);
                        nameContact.setText(user.getUserName());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.start();
    }

    private void checkIfCallEnded() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        String checkCancelledID = currentUid;
//        if (!callerID.equals(currentUid)) {
//            checkCancelledID = receiverID;
//        }

        FirebaseFirestore
                .getInstance()
                .collection(Constants.COLLECTION_CALLING)
                .whereEqualTo("callerID", callerID)
                .whereEqualTo("receiverID", receiverID)
                .addSnapshotListener((snapshot, exception) -> {
                    if (snapshot != null && snapshot.getDocuments().isEmpty() && !isFinishing()) {
                        Global.listeningToCall = false;
                        mediaPlayer.stop();
                        finish();
                    } else if (!isFinishing() && currentUid.equals(callerID) && snapshot.getDocuments() != null && snapshot.getDocuments().size() > 0 && snapshot.getDocuments().get(0).get("status").equals("ongoing")) {
                        mediaPlayer.stop();
                        Intent intent = new Intent(this, VideoChatActivity.class);
                        intent.putExtra("callerID", callerID);
                        intent.putExtra("receiverID", receiverID);
                        intent.putExtra("appointment", appointmentModel);
                        if (currentUid.equals(callerID)) {
                            intent.putExtra("videoCall", String.valueOf(videoCall));
                        }
                        startActivity(intent);
                        finish();
                    }
                });
    }
}