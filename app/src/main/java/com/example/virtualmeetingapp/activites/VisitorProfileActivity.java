package com.example.virtualmeetingapp.activites;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class VisitorProfileActivity extends BaseActivity {

    TextView tvVisitorProName, tvVisitorProEmail, tvVisitorProDes;
    TextView tvVisitorProApproveByAdmin, tvVisitorProPriListApproval, tvVisitorProOffListApproval;
    ImageView VisitorProImage1, VisitorProImage2;
    Button deleteImage, deleteImage1, updateImage, updateImage1;

    private final int PERMISSION_CODE = 1000;
    private static final int MY_CAMERA_PERMISSION_CODE = 2000;
    private static final int MY_GALLREY_PERMISSION_CODE = 3000;
    private static final int IMAGE_CODE = 1;

    private ProgressDialog loadingBar;

    AlertDialog.Builder builder;

    private Uri file;
    int imageType = 0;
    String permission[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    FirebaseStorage mStorage;
    FirebaseFirestore db;
    StorageReference mStorageRef;
    private Boolean imagePickId, imagePickId1 = false;
    //    Map<String, String> imageUpdateMap = new HashMap<>();
    Map<String, Object> imageUpdateMap = new HashMap<>();

    private User currentUser;

    @Override
    public void initXML() {
        tvVisitorProName = findViewById(R.id.tvVisitorProName);
        tvVisitorProEmail = findViewById(R.id.tvVisitorProEmail);
        tvVisitorProDes = findViewById(R.id.tvVisitorProDes);
        tvVisitorProApproveByAdmin = findViewById(R.id.tvVisitorProApproveByAdmin);
        tvVisitorProPriListApproval = findViewById(R.id.tvVisitorProPriListApproval);
        tvVisitorProOffListApproval = findViewById(R.id.tvVisitorProOffListApproval);
        VisitorProImage1 = findViewById(R.id.VisitorProImage1);
        VisitorProImage2 = findViewById(R.id.VisitorProImage2);
        deleteImage = findViewById(R.id.deleteImage);
        deleteImage1 = findViewById(R.id.deleteImage1);

    }

    @Override
    public void initVariables() {
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        currentUser = (User) Global.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_profile);

        loadingBar = new ProgressDialog(this);
        builder = new AlertDialog.Builder(this);

        initXML();
        initVariables();

        // Alert Dialog start

        VisitorProImage1.setOnClickListener(v -> {
            if (visitor.getImage1Url().isEmpty() || visitor.getImage1Url() == null) {
                imagePickId = true;
                imagePickId1 = false;
                UpdateUserImage();
            } else {
                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("This is test text").setTitle("Head Title");
                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to update your picture?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            dialog.dismiss();
                            DeleteUserImage();
                            imagePickId = true;
                            imagePickId1 = false;
                            UpdateUserImage();
                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            //  Action for 'NO' Button
                            dialog.cancel();
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                    }
                });
                //Setting the title manually
                alert.setTitle("AlertDialogExample");
                alert.show();
            }

        });

        VisitorProImage2.setOnClickListener(v -> {
            if (visitor.getImage2Url().isEmpty() || visitor.getImage2Url() == null) {
                imagePickId1 = true;
                imagePickId = false;
                UpdateUserImage();
            } else {
                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("This is test text").setTitle("Head Title");

                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to update your picture?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            finish();
                            imagePickId1 = true;
                            imagePickId = false;
                            UpdateUserImage();
                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            //  Action for 'NO' Button
                            dialog.cancel();
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                    }
                });
                //Setting the title manually
                alert.setTitle("Alert");
                alert.show();
            }

        });

        // Alert Dialog end

        deleteImage.setOnClickListener(v -> {
            DeleteUserImage();
        });

        deleteImage1.setOnClickListener(v -> {
            DeleteUserImage1();
        });

        if (currentUser.getUserType().equals(Constants.USER_TYPE_OFFICER)) {
            if (getIntent() != null) {
                String visitorName = getIntent().getStringExtra("visitorName");
                String visitorEmail = getIntent().getStringExtra("visitorEmail");
                String visitorDescription = getIntent().getStringExtra("visitorDescription");
                String visitorImage1Url = getIntent().getStringExtra("visitorImage1Url");
                String visitorImage2Url = getIntent().getStringExtra("visitorImage2Url");
                UserData(visitorName, visitorEmail, visitorDescription, visitorImage1Url, visitorImage2Url);
            }
            deleteImage.setVisibility(View.GONE);
            deleteImage1.setVisibility(View.GONE);
            VisitorProImage1.setEnabled(false);
            VisitorProImage2.setEnabled(false);
        } else if (currentUser.getUserType().equals(Constants.USER_TYPE_VISITOR)) {
            UserData1();
        }

    }

    public void UserData(String visitorName, String visitorEmail, String visitorDescription,
                         String visitorImage1Url, String visitorImage2Url) {

        tvVisitorProName.setText(visitorName);
        tvVisitorProEmail.setText(visitorEmail);
        tvVisitorProDes.setText(visitorDescription);

        if (visitorImage1Url == null || visitorImage1Url.equals("") || visitorImage1Url.isEmpty()) {
            Picasso.get().load(R.drawable.add).into(VisitorProImage1);
        } else {
            Picasso.get().load(visitorImage1Url).into(VisitorProImage1);
        }

        if (visitorImage2Url == null || visitorImage2Url.equals("") || visitorImage2Url.isEmpty()) {
            Picasso.get().load(R.drawable.add).into(VisitorProImage2);
        } else {
            Picasso.get().load(visitorImage2Url).into(VisitorProImage2);
        }
    }

    public void UserData1() {

        String userName = visitor.getUserName();
        tvVisitorProName.setText(userName);

        String userEmail = visitor.getUserEmail();
        tvVisitorProEmail.setText(userEmail);

        String userDes = visitor.getDescriptionVisitor();
        tvVisitorProDes.setText(userDes);

        if (visitor.getImage1Url().equals("") || visitor.getImage1Url() == null) {
            Picasso.get().load(R.drawable.add).into(VisitorProImage1);
        } else {
            String imageView = visitor.getImage1Url();
            Picasso.get().load(imageView).into(VisitorProImage1);
        }

        if (visitor.getImage2Url().equals("") || visitor.getImage2Url().isEmpty()) {
            Picasso.get().load(R.drawable.add).into(VisitorProImage2);
        } else {
            String imageView1 = visitor.getImage2Url();
            Picasso.get().load(imageView1).into(VisitorProImage2);
        }
        Boolean isApproveByAdmin = visitor.isApprovedByAdmin();
        if (isApproveByAdmin = true) {
            tvVisitorProApproveByAdmin.setText("true");
            tvVisitorProApproveByAdmin.setTextColor(Color.GREEN);
        } else {
            tvVisitorProApproveByAdmin.setText("false");
            tvVisitorProApproveByAdmin.setTextColor(Color.RED);
        }

        Boolean isVisitorProPriListApproval = visitor.isPriListApvByOfficer();
        if (isVisitorProPriListApproval) {
            tvVisitorProPriListApproval.setText("true");
            tvVisitorProPriListApproval.setTextColor(Color.GREEN);
        } else {
            tvVisitorProPriListApproval.setText("false");
            tvVisitorProPriListApproval.setTextColor(Color.RED);
        }

        Boolean isVisitorProOffListApproval = visitor.isOffListApvByOfficer();
        if (isVisitorProOffListApproval) {
            tvVisitorProOffListApproval.setText("true");
            tvVisitorProOffListApproval.setTextColor(Color.GREEN);
        } else {
            tvVisitorProOffListApproval.setText("false");
            tvVisitorProOffListApproval.setTextColor(Color.RED);
        }

    }

    private void UpdateUserImage() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, MY_GALLREY_PERMISSION_CODE);//zero can be replaced with any action code

    }

    public void DeleteUserImage() {

        StorageReference ref = mStorage.getReferenceFromUrl(visitor.getImage1Url());
        ref.delete().addOnSuccessListener(aVoid -> {
            ToastHelper.showToast("Image Deleted Done");
            Picasso.get().load(R.drawable.add).into(VisitorProImage1);
        }).addOnFailureListener(exception -> {
            ToastHelper.showToast(exception.getMessage());
        });

        Map<String, Object> map = new HashMap<>();
        visitor.setImage1Url("");
        map.put("image1Url", "");

        db.collection(Constants.COLLECTION_USER).document(visitor.getUserEmail())
                .update(map)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                    }

                });

    }

    public void DeleteUserImage1() {

        StorageReference ref = mStorage.getReferenceFromUrl(visitor.getImage2Url());
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ToastHelper.showToast("Image Deleted Done");
                Picasso.get().load(R.drawable.placeholder).into(VisitorProImage2);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
//                ToastHelper.showToast("Error in Image Deleting");
                ToastHelper.showToast(exception.getMessage());
            }
        });

        Map<String, Object> map1 = new HashMap<>();
        visitor.setImage2Url("");
        map1.put("image2Url", "");

        db.collection(Constants.COLLECTION_USER).document(visitor.getUserEmail())
                .update(map1)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                    }

                });

    }

    //Update Image Start

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
                                        VisitorProfileActivity.this.requestPermissions(new String[]{Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
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
                ActivityCompat.requestPermissions(VisitorProfileActivity.this, permission, PERMISSION_CODE);
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
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    StorageReference ref = mStorageRef.child("images/" + UUID.randomUUID().toString());
                    ref.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();

                                    ref.getDownloadUrl().addOnCompleteListener(v -> {
                                        if (v.isSuccessful()) {
                                            String imageUrl = v.getResult().toString();
                                            if (imagePickId) {
                                                visitor.setImage1Url(imageUrl);
                                                imageUpdateMap.put("image1Url", imageUrl);
                                            } else if (imagePickId1) {
                                                visitor.setImage2Url(imageUrl);
                                                imageUpdateMap.put("image2Url", imageUrl);
                                            }
                                            if (imagePickId) {
//                                        Object imageUrl = imageUpdateMap.get("image1Url");
                                                db.collection(Constants.COLLECTION_USER).document(visitor.getUserEmail())
                                                        .update(imageUpdateMap)
                                                        .addOnCompleteListener(task -> {
                                                            if (!task.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                                                                new SystemPrefs().saveUserSession(visitor, Constants.USER_TYPE_VISITOR);
                                                            }

                                                        });
                                            } else if (imagePickId1) {
//                                        String imageUrl1 = imageUpdateMap.get("image2Url");
                                                db.collection(Constants.COLLECTION_USER).document(visitor.getUserEmail())
                                                        .update(imageUpdateMap)
                                                        .addOnCompleteListener(task -> {
                                                            if (!task.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                                                                new SystemPrefs().saveUserSession(visitor, Constants.USER_TYPE_VISITOR);
                                                            }

                                                        });
                                            }
                                        }
                                    });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Picasso.get().load(file).into(VisitorProImage1);
                } else if (imagePickId1) {
                    Picasso.get().load(file).into(VisitorProImage2);
                }
                imageType = 1;
            } else {
//                VisitorProImage1.setImageDrawable(getApplicationContext().getDrawable(R.drawable.add));
//                VisitorProImage2.setImageDrawable(getApplicationContext().getDrawable(R.drawable.add));
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
            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            getApplicationContext().sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Update Image code end

}