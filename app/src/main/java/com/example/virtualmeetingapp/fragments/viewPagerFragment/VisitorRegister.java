package com.example.virtualmeetingapp.fragments.viewPagerFragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.adapter.ImageAdapter;
import com.example.virtualmeetingapp.models.ImageModel;
import com.example.virtualmeetingapp.models.Visitor;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class VisitorRegister extends Fragment {
    private CountryCodePicker ccp;
    private EditText phoneText, userName, description, visitorEmail, visitorPassword;
    private EditText codeText;
    private Button continueAndNextBtn, registerVisitor;
    private Button dialogBox;
    private String checker = "", phoneNumber = "";
    private String mVerificationId;
    //    private String imagePickId = "0";
//    private String imagePickId, imagePickId1;
    private Boolean imagePickId, imagePickId1 = false;
    Map<String, String> imageMap = new HashMap<>();

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // my changes
    private String userType, typingTo, onlineStatus, token;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final int PERMISSION_CODE = 1000;
    private static final int MY_CAMERA_PERMISSION_CODE = 2000;
    private static final int MY_GALLREY_PERMISSION_CODE = 3000;

    private Uri file, file1;
    int imageType = 0;
    String permission[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    // my changes
    FirebaseStorage mStorage = FirebaseStorage.getInstance();

    private StorageReference mStorageRef = mStorage.getReference();


    private ProgressDialog loadingBar;

    private static final int IMAGE_CODE = 1;
    TextView selectBtn, noImage;
    RecyclerView recyclerView;
    ImageView visitorImage, visitorImage1;
    List<ImageModel> imageModels;
    ImageAdapter imageAdapter;
    ProgressDialog pd;


    public static VisitorRegister newInstance() {
        return new VisitorRegister();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_visitor_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(getContext());

        phoneText = view.findViewById(R.id.phoneNumber);
        userName = view.findViewById(R.id.visitorName);
        visitorEmail = view.findViewById(R.id.visitorEmail);
        visitorPassword = view.findViewById(R.id.visitorPassword);
        registerVisitor = view.findViewById(R.id.registerVisitor);
        description = view.findViewById(R.id.description);
        continueAndNextBtn = view.findViewById(R.id.btnSendCode);
        selectBtn = view.findViewById(R.id.btnSelect);
        visitorImage = view.findViewById(R.id.visitorImage);
        visitorImage1 = view.findViewById(R.id.visitorImage1);
        recyclerView = view.findViewById(R.id.recyclerViewId);
        noImage = view.findViewById(R.id.noImage);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mStorageRef = FirebaseStorage.getInstance().getReference();


        imageModels = new ArrayList<>();

//        selectBtn.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//            @Override
//            public void onClick(View v) {
//
////                Intent intent = new Intent();
////                intent.setType("image/*");
////                intent.setAction(Intent.ACTION_GET_CONTENT);
////                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
////                startActivityForResult(intent, IMAGE_CODE);
//                selectDialogOption();
//            }
//        });

        visitorImage.setOnClickListener(v -> {
            imagePickId = true;
            imagePickId1 = false;
            selectDialogOption();
        });

        visitorImage1.setOnClickListener(v -> {
            imagePickId1 = true;
            imagePickId = false;
            selectDialogOption();
        });
        // originally it is empty so it will return null of an object reference
//        int itemCount = imageAdapter.getItemCount();
//
//        if (itemCount > 1) {
//            noImage.setVisibility(View.GONE);
//        }


        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);

        AddOfficer();

        return view;
    }

    //My changings

    private void AddOfficer() {
        registerVisitor.setOnClickListener(v -> {
//                phoneNumber = ccp.getFullNumberWithPlus();

            pd = new ProgressDialog(getContext());
            pd.setMessage("Please wait...");
            pd.show();

            final String str_username = userName.getText().toString();
//                final String str_phoneNo = phoneAddOfficer.getText().toString();
            final String str_phoneNo = "";
            final String str_description = description.getText().toString();
            final String str_email = visitorEmail.getText().toString();
            final String str_password = visitorPassword.getText().toString();
            final Boolean isApproved = false;
            final Boolean isOffListApproved = true;
            final Boolean isPriListApproved = false;
            final Boolean isChatApprovedByOfficer = false;
            final String userType = Constants.USER_TYPE_VISITOR;
            final String token = "";

            if (TextUtils.isEmpty(str_username) && TextUtils.isEmpty(str_phoneNo) &&
                    TextUtils.isEmpty(str_description)) {
                pd.dismiss();
                userName.setError("Username is required!");
//                    phoneNumber.setError("Phone Number is required!");
                description.setError("description is required!");
            }
//                else if (str_phoneNo.length() != 10) {
//                    pd.dismiss();
//                    phoneNumber.setError("Phone Number should have no less or more than 10 characters!");
//
//                }
            else if (str_description.length() < 15) {
                pd.dismiss();
                description.setError("Description should contain at least 15 characters!");
            } else {

                String imageUrl = imageMap.get("image1Url");
                String imageUrl1 = imageMap.get("image2Url");
                if (imageUrl == null || imageUrl1 == null) {
                    ToastHelper.showToast("Please Select at least two Images.");
                    pd.dismiss();
                } else {
                    mAuth.createUserWithEmailAndPassword(str_email, str_password).addOnCompleteListener(signUp -> {
                        if (!signUp.isSuccessful()) {
                            pd.dismiss();
                            Toast.makeText(getContext(), signUp.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Visitor visitor = new Visitor(userType, userName.getText().toString(), phoneNumber,
                                    str_email, token, description.getText().toString(), str_password,
                                    isApproved, isOffListApproved, isPriListApproved,
                                    isChatApprovedByOfficer, imageUrl, imageUrl1);
                            visitor.setUid(signUp.getResult().getUser().getUid());
                            db.collection(Constants.COLLECTION_USER).document(str_email.toString()).set(visitor)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Visitor Added to database",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), task.getException().getLocalizedMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            pd.dismiss();
                        }
                    });
                }
            }

        });
    }

    //My changings

    // Put your logic like firebase etc. . .
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(getContext(), "Congratulations, you are logged in successfully.", Toast.LENGTH_SHORT).show();
//                            sendUserToMainActivity();
                        } else {
                            loadingBar.dismiss();
                            String e = task.getException().toString();
                            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //My Code for image upload start

    public void selectDialogOption() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, MY_GALLREY_PERMISSION_CODE);//zero can be replaced with any action code

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            boolean arePermissionsGranted = true;
            for (String t : permissions) {
                if (ContextCompat.checkSelfPermission(getContext(), t) != PackageManager.PERMISSION_GRANTED) {
                    arePermissionsGranted = false;

                    if (shouldShowRequestPermissionRationale(t)) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("These permissions are required to open camera or gallery.")
                                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        VisitorRegister.this.requestPermissions(new String[]{Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
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
                        Toast.makeText(getContext(), "Permission permanently denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        if (requestCode == PERMISSION_CODE) {
            if ((grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) ||
                    (grantResults.length > 1 && grantResults[1] != PackageManager.PERMISSION_GRANTED) ||
                    (grantResults.length > 2 && grantResults[2] != PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getContext(), "Permission required", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(getActivity(), permission, PERMISSION_CODE);
            }
        }
    }

    // uploading image on selection start
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_GALLREY_PERMISSION_CODE) {
            if (resultCode == RESULT_OK) {
                file = data.getData();
                if (file != null) {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    StorageReference ref = mStorageRef.child("images/" + UUID.randomUUID().toString());
                    ref.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "SignUp successfully", Toast.LENGTH_SHORT).show();

                                    ref.getDownloadUrl().addOnCompleteListener(v -> {
                                        if (v.isSuccessful()) {
                                            String imageUrl = v.getResult().toString();
                                            if (imagePickId) {
                                                imageMap.put("image1Url", imageUrl);
                                            } else if (imagePickId1) {
                                                imageMap.put("image2Url", imageUrl);
                                            }
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                }
                            });

                }

                if (imagePickId) {
                    Picasso.get().load(file).into(visitorImage);
                } else if (imagePickId1) {
                    Picasso.get().load(file).into(visitorImage1);
                }
                imageType = 1;
            } else {
                visitorImage.setImageDrawable(getContext().getDrawable(R.drawable.add));
                visitorImage1.setImageDrawable(requireContext().getDrawable(R.drawable.add));
            }
        }
    }

    // uploading image on selection end

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toURI());
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    public final void notifyMediaStoreScanner(final File file) {
        try {
            MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            getContext().sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //My Code for image upload end


}


/* Old Code of visitor register Starts*/
/* Old Code of visitor register From onCreateView */

//continueAndNextBtn.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        phoneNumber = ccp.getFullNumberWithPlus();
//
//        pd = new ProgressDialog(getContext());
//        pd.setMessage("Please wait...");
//        pd.show();
//
//final String str_username = userName.getText().toString();
//final String str_phoneNo = phoneText.getText().toString();
//final String str_description = description.getText().toString();
//
//        Query usernameQuery = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_COLLECTION).orderByChild("userName").equalTo(str_username);
//        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//@Override
//public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//        if (TextUtils.isEmpty(str_username) && TextUtils.isEmpty(str_phoneNo) &&
//        TextUtils.isEmpty(str_description)) {
//        pd.dismiss();
//        userName.setError("Username is required!");
//        phoneText.setError("Phone Number is required!");
//        description.setError("description is required!");
//        } else if (str_phoneNo.length() != 10) {
//        pd.dismiss();
//        phoneText.setError("Phone Number should have no less or more than 10 characters!");
//
//        } else if (dataSnapshot.getChildrenCount() > 0) {
//        pd.dismiss();
//        userName.setError("Username already exists!");
//        } else if (str_description.length() < 15) {
//        description.setError("Description should contain atleast 15 characters!");
//        } else {
//        //TODO::Other Variable data pending for upload
////                            register(str_username, str_phoneNo, str_email, str_password);
////                            register(str_username, str_phoneNo.toString(), str_description.toString());
//        register();
//        pd.dismiss();
//        }
//        }
//
//@Override
//public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//        });
////                if (!phoneNumber.equals("")) {
////                    loadingBar.setTitle("Phone Number Verification");
////                    loadingBar.setMessage("Please wait, while we are verifying your phone number.");
////                    loadingBar.setCanceledOnTouchOutside(false);
////                    loadingBar.show();
////                    Intent intent = new Intent(getContext(), VerificationActivity.class);
////                    intent.putExtra("mobile", phoneNumber);
////                    startActivity(intent);
////
////                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, getActivity(), mCallbacks);
////                } else {
////                    Toast.makeText(getContext(), "Please write valid phone number.", Toast.LENGTH_SHORT).show();
////                }
//
//        }
//
//
//        });
//
//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//@Override
//public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//        signInWithPhoneAuthCredential(phoneAuthCredential);
//        }
//
//@Override
//public void onVerificationFailed(FirebaseException e) {
//        Toast.makeText(getContext(), "Invalid Phone Number...", Toast.LENGTH_SHORT).show();
//
//        loadingBar.dismiss();
//        continueAndNextBtn.setText("Continue");
//        codeText.setVisibility(View.GONE);
//        }
//
//@Override
//public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//        super.onCodeSent(s, forceResendingToken);
//
//        mVerificationId = s;
//        mResendToken = forceResendingToken;
//
//        checker = "Code Sent";
//
//        loadingBar.dismiss();
//        Toast.makeText(getContext(), "Code has been sent, please check.", Toast.LENGTH_SHORT).show();
//        }
//        };

//my test function

//    public void register(){
//
//        Visitor visitor = new Visitor(phoneNumber, userName.toString(), description.toString());
//        db.collection("Visitors").document(phoneNumber).set(visitor)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()){
//                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }

//my test function

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK) {
//
//            if (data != null) {
//                if (data.getClipData() != null) {
//
//                    int totalitem = data.getClipData().getItemCount();
//
//                    if (totalitem != 2) {
//                        Toast.makeText(getContext(), "Select 2 images of an ID (Back and forth)", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//
//                    for (int i = 0; i < totalitem; i++) {
//
//                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
//                        String imagename = getFileName(imageUri);
//
//                        ImageModel modalClass = new ImageModel(imagename, imageUri);
//                        imageModels.add(modalClass);
//
//                        imageAdapter = new ImageAdapter(getContext(), imageModels);
//                        recyclerView.setAdapter(imageAdapter);
//
//                        noImage.setVisibility(View.GONE);
//
//
//                        StorageReference mRef = mStorageRef.child("image").child(imagename);
//
//                        mRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(getContext(), "Fail" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//
//                    }
//
//
//                }
//            } else {
//                Toast.makeText(getContext(), "Select 2 images of an ID (Back and forth)", Toast.LENGTH_LONG).show();
//            }
//        }
//
//    }
//
//    public String getFileName(Uri uri) {
//        String result = null;
//        if (uri.getScheme().equals("content")) {
//            Context applicationContext = getContext();
//
//            Cursor cursor = applicationContext.getContentResolver().query(uri, null, null, null, null);
//
//            try {
//                if (cursor != null && cursor.moveToFirst()) {
//                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//        if (result == null) {
//            result = uri.getPath();
//            int cut = result.lastIndexOf('/');
//            if (cut != -1) {
//                result = result.substring(cut + 1);
//            }
//        }
//        return result;
//    }

/* Old Code of visitor register Ends*/