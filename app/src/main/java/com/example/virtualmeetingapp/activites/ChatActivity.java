package com.example.virtualmeetingapp.activites;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.CallingActivity;
import com.example.virtualmeetingapp.ClientTypeActivity;
import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.adapter.MessageAdapter;
import com.example.virtualmeetingapp.models.AppointmentModel;
import com.example.virtualmeetingapp.models.ConversationModel;
import com.example.virtualmeetingapp.models.MessageModel;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.App;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import carbon.view.View;
import carbon.widget.ProgressBar;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends BaseActivity {

    private EditText etMessage;
    private RecyclerView rvMessages;
    private CircleImageView ivProfileThumb, ivVideoCall, ivVoiceCall;
    private TextView tvUsername, tvUserStatus, tvNoMessage;
    private ImageButton btnSend, btnAttach;
    private ProgressBar progressBar;

    private String chatId, myUid, chatUid, chatProfileThumb;
    private List<MessageModel> messageModelList;
    private MessageAdapter messageAdapter;
    private FirebaseFirestore fireStoreDB;
    private CollectionReference messagesCollectionRef, userCollectionRef, conversationCollectionRef;

    private boolean isOfficerChat = false;
    //my changing

    private final int PERMISSION_CODE = 1000;
    private static final int MY_CAMERA_PERMISSION_CODE = 2000;
    private static final int MY_GALLREY_PERMISSION_CODE = 3000;
    private static final int IMAGE_CODE = 1;

    Intent pickPhoto;
    private Uri file;
    int imageType = 0;
    String permission[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private AppointmentModel appointmentModel;
    private LinearLayout llChatLayout;

    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    Map<String, String> imageMap = new HashMap<>();

    User currentUser = (User) Global.getCurrentUser();
    //my changing

    @Override
    public void initXML() {
        tvUsername = findViewById(R.id.nameTv);
        btnSend = findViewById(R.id.sendBtn);
        ivProfileThumb = findViewById(R.id.proifleIv);
        tvUserStatus = findViewById(R.id.userStatusTv);
        tvNoMessage = findViewById(R.id.tvNoMessage);
        etMessage = findViewById(R.id.messageEt);
        btnAttach = findViewById(R.id.attachBtn);
        rvMessages = findViewById(R.id.chat_recyclerView);
        progressBar = findViewById(R.id.progressBar);
        ivVideoCall = findViewById(R.id.videoCall);
        ivVoiceCall = findViewById(R.id.ivVoiceCall);
        llChatLayout = findViewById(R.id.chatLayout);
    }

    @Override
    public void initVariables() {
        messageModelList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageModelList, chatProfileThumb);

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            ToastHelper.showToast("You Are Not Logged In ... Signing Out");

            Global.clearCurrentUser();
            FirebaseAuth.getInstance().signOut();
            new SystemPrefs().clearUserSession();

            startActivity(new Intent(this, ClientTypeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        } else {
            myUid = currentUser.getUid();
        }
        //get chat id from intent
        if (getIntent() == null) {
            finish();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            isOfficerChat = bundle.getBoolean("isOfficerChat");
        }
        chatId = intent.getStringExtra(Constants.INTENT_CHAT_ID);
        chatUid = intent.getStringExtra(Constants.INTENT_CHAT_UID);
        chatProfileThumb = intent.getStringExtra(Constants.INTENT_CHAT_PROFILE_THUMB);

        appointmentModel = (AppointmentModel) intent.getSerializableExtra("appointment");

        fireStoreDB = FirebaseFirestore.getInstance();
        userCollectionRef = fireStoreDB.collection(Constants.COLLECTION_USER);

        if (chatId == null) {
            fireStoreDB.collection(Constants.COLLECTION_USER_CONVERSATIONS).document(chatUid)
                    .collection(Constants.COLLECTION_CONVERSATIONS).document(myUid).get()
                    .addOnCompleteListener(fetchChatId -> {
                        if (fetchChatId.isSuccessful() && fetchChatId.getResult() != null
                                && fetchChatId.getResult().toObject(ConversationModel.class) != null) {
                            chatId = fetchChatId.getResult().toObject(ConversationModel.class).getId();
                        } else {
                            chatId = fireStoreDB.collection(Constants.COLLECTION_CHATS).document().getId();
                        }
                        messagesCollectionRef = fireStoreDB.collection(Constants.COLLECTION_CHATS)
                                .document(chatId).collection(Constants.COLLECTION_MESSAGES);
                        fetchChatMessages();
                    });
        } else {
            messagesCollectionRef = fireStoreDB.collection(Constants.COLLECTION_CHATS)
                    .document(chatId).collection(Constants.COLLECTION_MESSAGES);
            fetchChatMessages();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initXML();
        initVariables();
        setListeners();
        setUpMessagesRecycler();
        fetchChatUser();

        if (currentUser.getUserType().equals(Constants.USER_TYPE_PRISONER)) {
            btnAttach.setVisibility(android.view.View.GONE);
            ivVideoCall.setVisibility(android.view.View.GONE);
            ivVoiceCall.setVisibility(android.view.View.GONE);
        }

        if (currentUser.getUserType().equals(Constants.USER_TYPE_OFFICER)) {
            ivVideoCall.setVisibility(android.view.View.GONE);
            ivVoiceCall.setVisibility(android.view.View.GONE);
        }

        if (currentUser.getUserType().equals(Constants.USER_TYPE_VISITOR) && isOfficerChat) {
            ivVideoCall.setVisibility(android.view.View.GONE);
            ivVoiceCall.setVisibility(android.view.View.GONE);
        }
    }

    Handler btnMessageHandler = new Handler();

    @Override
    protected void onStop() {
        super.onStop();
        btnMessageHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (!currentUser.getUserType().equals(Constants.USER_TYPE_OFFICER) ||
//                !currentUser.getUserType().equals(Constants.USER_TYPE_PRISONER)) {
//            if (appointmentModel == null) {
//                llChatLayout.setVisibility(View.GONE);
////            ToastHelper.showToast("Invalid access, contact support");
//                return;
//            }
//        }
        if (!isOfficerChat) {
            if (currentUser.getUserType().equals(Constants.USER_TYPE_VISITOR)) {
                Runnable btnMessageRunnable = new Runnable() {
                    @Override
                    public void run() {
                        long currentTimeInMillis = System.currentTimeMillis();
                        if (currentTimeInMillis <= appointmentModel.getStartTimeInMillis() ||
                                currentTimeInMillis >= appointmentModel.getEndTimeInMillis()) {
//                        new MaterialAlertDialogBuilder(ChatActivity.this)
//                                .setTitle("Appointment Time Completed")
//                                .setMessage("Your appointment time is finished. Please press OK to leave. Thank you.")
//                                .setCancelable(false)
//                                .setPositiveButton("OK", (dialog, which) -> {
                            ToastHelper.showToast("Meeting Time Completed");
                            btnMessageHandler.removeCallbacksAndMessages(null);
                            Intent intent = new Intent(ChatActivity.this, VisitorActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
//                                }).show();
                        }
                        btnMessageHandler.postDelayed(this, 1000);
                    }
                };
                btnMessageHandler.post(btnMessageRunnable);
            }
            else if (currentUser.getUserType().equals(Constants.USER_TYPE_PRISONER)) {
                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
                fireStoreDB.collection(Constants.COLLECTION_APPOINTMENTS)
                        .whereEqualTo("prisonerId", prisoner.getUid())
                        .whereEqualTo("visitorId", currentUser.getUid())
                        .whereEqualTo("appointmentDate", currentDate)
                        .whereGreaterThanOrEqualTo("endTimeInMillis", System.currentTimeMillis())
                        .whereEqualTo("appointmentStatus", "approved").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            ToastHelper.showToast(task.getException().getMessage());
                        } else {
                            List<AppointmentModel> appointmentModels = task.getResult().toObjects(AppointmentModel.class);
                            if (appointmentModels != null && appointmentModels.size() > 0) {
                                Runnable btnMessageRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        if (appointmentModels.size() > 0) {
                                            long currentTimeInMillis = System.currentTimeMillis();
                                            if (currentTimeInMillis >= appointmentModels.get(0).getStartTimeInMillis() &&
                                                    currentTimeInMillis <= appointmentModels.get(0).getEndTimeInMillis()) {
                                                appointmentModel = appointmentModels.get(0);
                                                llChatLayout.setVisibility(android.view.View.VISIBLE);
                                            } else {
                                                llChatLayout.setVisibility(View.GONE);
                                                ToastHelper.showToast("Meeting Time Completed");
                                                Intent intent = new Intent(ChatActivity.this, PrisonersActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                            btnMessageHandler.postDelayed(this, 1000);
                                        } else {
                                            llChatLayout.setVisibility(View.GONE);
                                        }
                                    }
                                };
                                btnMessageHandler.post(btnMessageRunnable);
                            }
                        }
                    }
                });
            }
        }
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        //this method will be called after picking image from camera or gallery
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            assert result != null;
//            Uri mImageUri = result.getUri();
//            ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
//            progressDialog.setText("Analyzing image..");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
////            runTextRecognition(mImageUri, progressDialog);
//        } else {
//            Toast.makeText(this, "You quit camera", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }

//    My code for uploading image in chat start

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            boolean arePermissionsGranted = true;
            for (String t : permissions) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), t) != PackageManager.PERMISSION_GRANTED) {
                    arePermissionsGranted = false;

                    if (shouldShowRequestPermissionRationale(t)) {
                        new AlertDialog.Builder(getApplicationContext())
                                .setMessage("These permissions are required to open camera or gallery.")
                                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ChatActivity.this.requestPermissions(new String[]{Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                        break;
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission permanently denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        if (requestCode == PERMISSION_CODE) {
            if ((grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) ||
                    (grantResults.length > 1 && grantResults[1] != PackageManager.PERMISSION_GRANTED) ||
                    (grantResults.length > 2 && grantResults[2] != PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getApplicationContext(), "Permission required", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(ChatActivity.this, permission, PERMISSION_CODE);
            }
        }
    }

    // uploading image on selection start
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_GALLREY_PERMISSION_CODE) {
            if (resultCode == RESULT_OK) {
                file = data.getData();
                if (file != null) {
                    final android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    StorageReference ref = mStorageRef.child("images/" + UUID.randomUUID().toString());
                    ref.putFile(file)
                            .addOnSuccessListener(taskSnapshot -> {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();

                                ref.getDownloadUrl().addOnCompleteListener(v -> {
                                    if (v.isSuccessful()) {
                                        String imageUrl = v.getResult().toString();
                                        imageMap.put("message", imageUrl);

                                        final String message1 = imageMap.get("message");
                                        String type = retResType(file);
                                        sendImageMessage(message1, type);
                                    }
                                });

                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnProgressListener(taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            });

                }

//                Glide.with(getApplicationContext()).load(file).into(VisitorProImage1);
                imageType = 1;
            } else {
//                VisitorProImage1.setImageDrawable(getApplicationContext().getDrawable(R.drawable.add));
//                VisitorProImage2.setImageDrawable(getApplicationContext().getDrawable(R.drawable.add));
            }
        }

    }
    // uploading image on selection end

//    My code for uploading image in chat end

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onBackPressed();
    }

    private void setListeners() {
        ivVoiceCall.setOnClickListener(v -> {
            Intent intent = new Intent(this, CallingActivity.class);
            intent.putExtra("receiverID", chatUid);
            intent.putExtra("videoCall", false);
            intent.putExtra("appointment", appointmentModel);
            startActivity(intent);
        });

        ivVideoCall.setOnClickListener(v -> {
//            FirebaseFirestore.getInstance()
//                    .collection(Constants.COLLECTION_CALLING)
//                    .document(chatUid)
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful() && (task.getResult() == null || !task.getResult().exists())) {
            Intent intent = new Intent(this, CallingActivity.class);
            intent.putExtra("receiverID", chatUid);
            intent.putExtra("appointment", appointmentModel);
            startActivity(intent);
//                        }
//                    });
        });

        btnSend.setOnClickListener(v -> {
            final String message = etMessage.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                ToastHelper.showToast("Cannot send the empty message...");
            } else {
                sendTextMessage(message);
            }
            etMessage.setText("");
        });

        btnAttach.setOnClickListener(v -> {
//            CropImage.activity()
//                    .setAspectRatio(1, 1)
//                    .start(ChatActivity.this);
//            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            pickPhoto = new Intent(Intent.ACTION_PICK);
            pickPhoto.setType("image/*,video/*");
            startActivityForResult(pickPhoto, MY_GALLREY_PERMISSION_CODE);//zero can be replaced with any action code

        });
    }

    private void setUpMessagesRecycler() {
        rvMessages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        rvMessages.setAdapter(messageAdapter);
    }

    private void fetchChatUser() {
        userCollectionRef.whereEqualTo("uid", chatUid)
                .addSnapshotListener((userSnapshot, e) -> {
                    User user = userSnapshot.toObjects(User.class).get(0);
                    tvUsername.setText(user.getUserName());
                    tvUserStatus.setText("");

                    //image code not required now
//                    Picasso.get().load(R.drawable.placeholder).into(ivProfileThumb);
                });
    }

    private void fetchChatMessages() {
        progressBar.setVisibility(View.VISIBLE);
        messagesCollectionRef.orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener((messageSnapshot, e) -> {
            progressBar.setVisibility(View.GONE);
            if (e != null) {
                ToastHelper.showToast(e.getMessage());
            }

            if (messageSnapshot != null && !messageSnapshot.isEmpty()) {
                tvNoMessage.setVisibility(View.GONE);

                messageModelList = messageSnapshot.toObjects(MessageModel.class);
                messageAdapter = new MessageAdapter(ChatActivity.this, messageModelList, chatProfileThumb);
                rvMessages.setAdapter(messageAdapter);

                rvMessages.smoothScrollToPosition(-2);
            } else {
                tvNoMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void sendTextMessage(final String message) {
        MessageModel messageModel = new MessageModel();
        messageModel.setSender(myUid);
        messageModel.setReceiver(chatUid);
        messageModel.setMessage(message);
        messageModel.setTimeStamp(System.currentTimeMillis());
        messageModel.setSeen(false);
        messageModel.setType("text");
        messagesCollectionRef.add(messageModel);

        ConversationModel conversation = new ConversationModel();
        conversation.setId(chatId);
        conversation.setWithUID(chatUid);
        conversation.setLastMessage(message);
        conversation.setLastMessageTimeStamp(System.currentTimeMillis());
        conversationCollectionRef = fireStoreDB.collection(Constants.COLLECTION_USER_CONVERSATIONS)
                .document(myUid).collection(Constants.COLLECTION_CONVERSATIONS);
        conversationCollectionRef.document(chatUid).set(conversation);

        conversationCollectionRef = fireStoreDB.collection(Constants.COLLECTION_USER_CONVERSATIONS)
                .document(chatUid).collection(Constants.COLLECTION_CONVERSATIONS);
        conversation.setWithUID(myUid);
        conversationCollectionRef.document(myUid).set(conversation);
    }

    private void sendImageMessage(final String message1, String type) {
        MessageModel messageModel = new MessageModel();
        messageModel.setSender(myUid);
        messageModel.setReceiver(chatUid);
        messageModel.setMessage(message1);
        messageModel.setTimeStamp(System.currentTimeMillis());
        messageModel.setSeen(false);
        messageModel.setType(type);
        messagesCollectionRef.add(messageModel);

        ConversationModel conversation = new ConversationModel();
        conversation.setId(chatId);
        conversation.setWithUID(chatUid);
        if (messageModel.getType().equals("image")) {
            conversation.setLastMessage("Photo");
        } else if (messageModel.getType().equals("video")) {
            conversation.setLastMessage("Video");
        } else {
            conversation.setLastMessage(message1);
        }
        conversation.setLastMessageTimeStamp(System.currentTimeMillis());
        conversationCollectionRef = fireStoreDB.collection(Constants.COLLECTION_USER_CONVERSATIONS)
                .document(myUid).collection(Constants.COLLECTION_CONVERSATIONS);
        conversationCollectionRef.document(chatUid).set(conversation);

        conversationCollectionRef = fireStoreDB.collection(Constants.COLLECTION_USER_CONVERSATIONS)
                .document(chatUid).collection(Constants.COLLECTION_CONVERSATIONS);
        conversation.setWithUID(myUid);
        conversationCollectionRef.document(myUid).set(conversation);
    }

//    private void sendImageMessage(Uri imageUri, final ProgressDialog progressDialog) {
//        final String timeStamp = "" + System.currentTimeMillis();
//
//        String fileNameAndPath = "ChatImages/" + "post_" + timeStamp;
//
//        /*Chats node will be created that will contain all images sent via chat*/
//
//        //get bitmap from image uri
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeFile(imageUri.toString(), options);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] data = baos.toByteArray(); //conver image to bytes
//        StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
//        ref.putBytes(data)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        //image uploaded
//                        progressDialog.dismiss();
//                        //get url of uploaded image
//                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                        while (!uriTask.isSuccessful()) ;
//                        String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
//
//                        if (uriTask.isSuccessful()) {
//                            //add image uri and other info to database
//                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//                            //setup required data
//                            MessageModel messageModel = new MessageModel();
//                            messageModel.setSender(myUid);
//                            messageModel.setReceiver(hisUid);
//                            messageModel.setMessage(downloadUri);
//                            messageModel.setType("image");
//                            messageModel.setTimeStamp(System.currentTimeMillis());
//                            messageModel.setSeen(false);
//                            //put this data to firebease
//                            databaseReference.child("Chats").push().setValue(messageModel);
//
//                            //send notification
//                            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(Constants.COLLECTION_USER).child(myUid);
//                            database.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    User user = dataSnapshot.getValue(User.class);
//
//                                    if (notify) {
//                                        assert user != null;
//                                        sendNotification(hisUid, user.getUserName(), "Sent you a photo...");
//                                    }
//                                    notify = false;
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//
//
//                            //create chatlist node/child in firebase database
//                            final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference().child("Chatlist")
//                                    .child(myUid)
//                                    .child(hisUid);
//                            chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    if (!dataSnapshot.exists()) {
//                                        chatRef1.child("id").setValue(hisUid);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//
//                            final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference().child("Chatlist")
//                                    .child(hisUid)
//                                    .child(myUid);
//                            chatRef2.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    if (!dataSnapshot.exists()) {
//                                        chatRef2.child("id").setValue(myUid);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//
//
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //failed
//                        progressDialog.dismiss();
//                    }
//                });
//
//
//    }

    private String retResType(Uri uri) {
        if (isImage(uri)) {
            return "image";
        } else if (isVideo(uri)) {
            return "video";
        }
        return null;
    }

    private Boolean isImage(Uri uri) {
        return App.getAppContext().getContentResolver().getType(uri).contains("image");
    }

    private Boolean isVideo(Uri uri) {
        return App.getAppContext().getContentResolver().getType(uri).contains("video");
    }

}

