package com.example.virtualmeetingapp.activites;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.models.AppointmentModel;
import com.example.virtualmeetingapp.models.Prisoner;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.NetworkUtils;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity
        implements Session.SessionListener,
        PublisherKit.PublisherListener {

    private static final String LOG_TAG = VideoChatActivity.class.getSimpleName();

    private static final String API_Key = "46984474";
    private static final String SESSION_ID = "1_MX40Njk4NDQ3NH5-MTYwNTI2NjAyNDM3MH45M0RERGMrSEdNVldUdlN6MTRiSE81aE5-fg";
    private static final String TOKEN = "T1==cGFydG5lcl9pZD00Njk4NDQ3NCZzaWc9NDFkYmE3NWQ4NGYyZjEwYWZiMzJhZTEwOTkxZGVjOWF" +
            "iYjRiY2Q3MDpzZXNzaW9uX2lkPTFfTVg0ME5qazRORFEzTkg1LU1UWXdOVEkyTmpBeU5ETTNNSDQ1TTBSRVJHTXJTRWROVmxkVWRsTjZNVF" +
            "JpU0U4MWFFNS1mZyZjcmVhdGVfdGltZT0xNjA1MjY2MDM3Jm5vbmNlPTAuNzkxNDk1NzA3NDM5NzQ3MSZyb2xlPXB1Ymxpc2hlciZleHBpc" +
            "mVfdGltZT0xNjA3ODU4MDM2JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final int RC_VIDEO_APP_PERM = 1222;

    private FrameLayout mPublisherViewController;
    private FrameLayout mSubscriberViewController;
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private ImageView closeVideoChatBtn;
    private DatabaseReference usersRef;

    private String userID = "";
    private TextView tvTimer, tvNoVideo, tvNoAudio;
    private long tenminutesleft = 600000;
    private long timeLeft = tenminutesleft;
    private CountDownTimer countDownTimer;
    private boolean timeIsRunning;
    private boolean videoCall = true;

    private String callerID = "";
    private String receiverID = "";

    private long callStartTime, callEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvNoVideo = (TextView) findViewById(R.id.tvNoVideo);
        tvNoAudio = (TextView) findViewById(R.id.tvNoAudio);

        Global.appointmentDetailsSaved = false;
        Global.listeningToCall = true;

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child(Constants.COLLECTION_USER);

        if (getIntent() != null && getIntent().hasExtra("videoCall")) {
            videoCall = getIntent().getStringExtra("videoCall").equals("true");
        }
        receiverID = getIntent().getStringExtra("receiverID");
        callerID = getIntent().getStringExtra("callerID");

        closeVideoChatBtn = findViewById(R.id.close_video_chat_btn);
        closeVideoChatBtn.setOnClickListener(view -> {
            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_CALLING).document(callerID).delete()
                    .addOnCompleteListener(task -> {
                        onBackPressed();
                    });
        });

        requestPermissions();
        checkIfCallEnded();
    }

    Handler btnMessageHandler = new Handler();

    @Override
    protected void onStart() {
        super.onStart();

        if (getIntent() != null) {
            if (getIntent().hasExtra("appointment") && ((User) new SystemPrefs().getUserSession()).getUserType().equals(Constants.USER_TYPE_VISITOR)) {
                AppointmentModel appointmentModel = (AppointmentModel) getIntent().getSerializableExtra("appointment");

                appointmentModel.setAppointmentEndedByOfficer(false);
                appointmentModel.setAppointmentEndedByOfficerName("");
                FirebaseFirestore.getInstance().collection(Constants.COLLECTION_VIDEO_CALLS)
                        .document(String.valueOf(appointmentModel.getAppointmentId()))
                        .set(appointmentModel);

                checkIfAppointmentEndedByOfficer(String.valueOf(appointmentModel.getAppointmentId()));

                long timePassed = System.currentTimeMillis() - appointmentModel.getStartTimeInMillis();
                tenminutesleft = tenminutesleft - timePassed;
                timeStart();

                callStartTime = System.currentTimeMillis();
                Runnable btnMessageRunnable = new Runnable() {
                    @Override
                    public void run() {
                        long currentTimeInMillis = System.currentTimeMillis();
                        if (currentTimeInMillis <= appointmentModel.getStartTimeInMillis() ||
                                currentTimeInMillis >= appointmentModel.getEndTimeInMillis()) {
//                            new MaterialAlertDialogBuilder(VideoChatActivity.this)
//                                    .setTitle("Appointment Time Completed")
//                                    .setMessage("Your appointment time is finished. Please press OK to leave. Thank you.")
//                                    .setCancelable(false)
//                                    .setPositiveButton("OK", (dialog, which) -> {
                            ToastHelper.showToast("Meeting Time Completed");
                            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_CALLING).document(callerID).delete();
                            Intent intent = new Intent(VideoChatActivity.this, VisitorActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            onBackPressed();
//                                    }).show();
                        }
                        btnMessageHandler.postDelayed(this, 1000);
                    }
                };
                btnMessageHandler.post(btnMessageRunnable);
            } else if (getIntent().hasExtra("appointmentId") && ((User) new SystemPrefs().getUserSession()).getUserType().equals(Constants.USER_TYPE_PRISONER)) {
                String appointmentID = getIntent().getStringExtra("appointmentId");
                checkIfAppointmentEndedByOfficer(String.valueOf(appointmentID));

                if (NetworkUtils.getInstance().isNetworkConnected(this)) {
                    FirebaseFirestore.getInstance().collection(Constants.COLLECTION_APPOINTMENTS)
                            .document(appointmentID)
                            .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            AppointmentModel appointmentModel = task.getResult().toObject(AppointmentModel.class);
                            if (appointmentModel != null) {
                                long timePassed = System.currentTimeMillis() - appointmentModel.getStartTimeInMillis();
                                tenminutesleft = tenminutesleft - timePassed;
                                timeStart();

                                Runnable btnMessageRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        long currentTimeInMillis = System.currentTimeMillis();
                                        if (currentTimeInMillis <= appointmentModel.getStartTimeInMillis() ||
                                                currentTimeInMillis >= appointmentModel.getEndTimeInMillis()) {
                                            ToastHelper.showToast("Meeting Time Completed");
                                            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_CALLING).document(callerID).delete();
                                            Intent intent = new Intent(VideoChatActivity.this, PrisonersActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            onBackPressed();
//                                            new MaterialAlertDialogBuilder(VideoChatActivity.this)
//                                                    .setTitle("Appointment Time Completed")
//                                                    .setMessage("Your appointment time is finished. Thank you.")
//                                                    .setCancelable(false)
//                                                    .show();
                                        } else {
                                            btnMessageHandler.postDelayed(this, 1000);
                                        }
                                    }
                                };
                                btnMessageHandler.post(btnMessageRunnable);
                            }
                        }
                    });
                } else {
                    ToastHelper.showToast("Internet Not Available");
                }
            }
        }
    }

    private void timeStart() {
        countDownTimer = new CountDownTimer(tenminutesleft, 1000) {
            @Override
            public void onTick(long l) {
                tenminutesleft = l;
                updateTime();
            }

            @Override
            public void onFinish() {
                timeIsRunning = false;
            }
        }.start();
        timeIsRunning = true;
    }

    private void updateTime() {
        timeLeft = tenminutesleft;
        int minutes = (int) tenminutesleft / 60000;
        int seconds = (int) tenminutesleft % 60000 / 1000;

        String timeLeft;

        if (minutes < 10) {
            timeLeft = "0" + minutes;
            timeLeft += ":";
            if (seconds < 10) timeLeft += "0";
            timeLeft += seconds;

            tvTimer.setText(timeLeft);
        } else if (minutes >= 10) {
            timeLeft = "" + minutes;
            timeLeft += ":";
            if (seconds < 10) timeLeft += "0";
            timeLeft += seconds;

            tvTimer.setText(timeLeft);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, VideoChatActivity.this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

        if (EasyPermissions.hasPermissions(this, perms)) {
            mPublisherViewController = findViewById(R.id.publisher_container);
            mSubscriberViewController = findViewById(R.id.subscriber_container);

            //1.initialize and connect to the Session
            mSession = new Session.Builder(this, API_Key, SESSION_ID).build();
            mSession.setSessionListener(VideoChatActivity.this);
            mSession.connect(TOKEN);
        } else {
            EasyPermissions.requestPermissions(this, "Hey this app needs Mic and Camera, Please allow.", RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
    }

    //2. Publishing a stream to the session
    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublishVideo(videoCall);
        mPublisher.setPublisherListener(VideoChatActivity.this);

        mPublisherViewController.addView(mPublisher.getView());
        if (mPublisher.getView() instanceof GLSurfaceView) {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Stream Disconnected");
    }

    //3. Subscribing to the streams
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");

        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();

            if (stream.hasAudio()) {
                // You may want to adjust the user interface
                tvNoAudio.setVisibility(View.GONE);
                mSubscriber.setSubscribeToAudio(true);
            } else {
                mSubscriber.setSubscribeToAudio(false);
                tvNoAudio.setVisibility(View.VISIBLE);
            }
            if (stream.hasVideo()) {
                // You may want to adjust the user interface
                tvNoVideo.setVisibility(View.GONE);
                mSubscriber.setSubscribeToVideo(true);
            } else {
                mSubscriber.setSubscribeToVideo(false);
                tvNoVideo.setVisibility(View.VISIBLE);
            }

            mSession.subscribe(mSubscriber);
            mSubscriberViewController.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewController.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.i(LOG_TAG, "Stream Error");
    }

    private static final String TAG = "VideoChatActivity";

    private void checkIfCallEnded() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection(Constants.COLLECTION_CALLING)
                .whereEqualTo("callerID", callerID)
                .whereEqualTo("receiverID", receiverID)
                .addSnapshotListener((snapshot, exception) -> {
                    if ((snapshot == null || snapshot.isEmpty()) && !isFinishing()) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (!Global.appointmentDetailsSaved && ((User) new SystemPrefs().getUserSession()).getUserType().equals(Constants.USER_TYPE_VISITOR)) {
            callEndTime = System.currentTimeMillis();
            Global.appointmentDetailsSaved = true;

            Map<String, Object> appointmentDetailMap = new HashMap<>();
            appointmentDetailMap.put("callStartTimeInMillis", callStartTime);
            appointmentDetailMap.put("callEndTimeInMillis", callEndTime);
            appointmentDetailMap.put("type", videoCall ? "Video Call" : "Voice Call");

            AppointmentModel appointmentModel = (AppointmentModel) getIntent().getSerializableExtra("appointment");
            appointmentDetailMap.put("visitorName", appointmentModel.getVisitorName());
            appointmentDetailMap.put("prisonerName", appointmentModel.getPrisonerName());
            appointmentDetailMap.put("appointmentId", appointmentModel.getAppointmentId());

            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_APPOINTMENT_DETAILS).add(appointmentDetailMap);
            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_VIDEO_CALLS).document(String.valueOf(appointmentModel.getAppointmentId())).delete();
        }

        if (mPublisher != null) {
            mPublisher.destroy();
            mPublisher = null;
        }
        if (mSubscriber != null) {
            mSubscriber.destroy();
            mSubscriber = null;
        }
        if (mSession != null) {
            mSession.disconnect();
            mSession = null;
        }

        Global.listeningToCall = false;
        if (btnMessageHandler != null)
            btnMessageHandler.removeCallbacksAndMessages(null);
        finish();
//        super.onBackPressed();
    }

    private void checkIfAppointmentEndedByOfficer(String appointmentId) {
        FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_VIDEO_CALLS)
                .document( appointmentId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            ToastHelper.showToast(error.getMessage());
                        } else {
                            if (value != null && value.exists()) {
                                AppointmentModel appointmentModels = value.toObject(AppointmentModel.class);
//                                if (appointmentModels.size() > 0) {
                                    if (appointmentModels != null && appointmentModels.isAppointmentEndedByOfficer()) {
//                                        new MaterialAlertDialogBuilder(ChatActivity.this)
//                                                .setTitle("Appointment Time Completed")
//                                                .setMessage("Your appointment is ended by officer")
//                                                .setCancelable(false)
//                                                .setPositiveButton("OK", (dialog, which) -> {
                                        ToastHelper.showToast("Your appointment is ended by Officer " + appointmentModels.getAppointmentEndedByOfficerName());
//                                        Intent intent = new Intent(VideoChatActivity.this, ChatActivity.class);
//                                        if (((User) new SystemPrefs().getUserSession()).getUserType().equals(Constants.USER_TYPE_PRISONER)) {
//                                            intent = new Intent(VideoChatActivity.this, ConversationsActivity.class);
//                                        }
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
                                        onBackPressed();
//                                                }).show();
                                    }
//                                }
                            }
                        }
                    }
                });
    }

}
