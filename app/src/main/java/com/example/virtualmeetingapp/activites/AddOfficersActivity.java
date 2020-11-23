package com.example.virtualmeetingapp.activites;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.models.Officer;
import com.example.virtualmeetingapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

public class AddOfficersActivity extends AppCompatActivity {

    private CountryCodePicker ccpAddOfficer;
    private EditText phoneAddOfficer, nameAddOfficer, descriptionAddOfficer, emailAddOfficer, passwordAddOfficer;
    private Button btnAddOfficer;
    private String phoneNumber = "";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ProgressDialog pd;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_officers);

        phoneAddOfficer = findViewById(R.id.phoneNumberAddOfficer);
        nameAddOfficer = findViewById(R.id.nameAddOfficer);
        emailAddOfficer = findViewById(R.id.emailAddOfficer);
        passwordAddOfficer = findViewById(R.id.passwordAddOfficer);
        descriptionAddOfficer = findViewById(R.id.descriptionAddOfficer);
        btnAddOfficer = findViewById(R.id.btnAddOfficer);

        ccpAddOfficer = (CountryCodePicker) findViewById(R.id.ccpAddOfficer);
        ccpAddOfficer.registerCarrierNumberEditText(phoneAddOfficer);

        AddOfficer();
    }

    private void AddOfficer() {
        btnAddOfficer.setOnClickListener(v -> {
//                phoneNumber = ccpAddOfficer.getFullNumberWithPlus();

            pd = new ProgressDialog(AddOfficersActivity.this);
            pd.setMessage("Please wait...");
            pd.show();

            final String str_username = nameAddOfficer.getText().toString();
//                final String str_phoneNo = phoneAddOfficer.getText().toString();
            final String str_phoneNo = "";
            final String str_id = emailAddOfficer.getText().toString();
            final String str_description = descriptionAddOfficer.getText().toString();
            final String str_email = "officer" + emailAddOfficer.getText().toString() + "@moi.ae";
            final String str_password = passwordAddOfficer.getText().toString();
            final String userType = Constants.USER_TYPE_OFFICER;
            final Boolean OfficerListAcceptByAdmin = true;
            final Boolean VisitorListAcceptByAdmin = true;
            final String token = "";

//                && TextUtils.isEmpty(str_phoneNo)
            if (TextUtils.isEmpty(str_username) &&
                    TextUtils.isEmpty(str_description)) {
                pd.dismiss();
                nameAddOfficer.setError("Username is required!");
//                    phoneAddOfficer.setError("Phone Number is required!");
                descriptionAddOfficer.setError("description is required!");
            }
//                else if (str_phoneNo.length() != 10) {
//                    pd.dismiss();
//                    phoneAddOfficer.setError("Phone Number should have no less or more than 10 characters!");
//
//                }
            else if (str_description.length() < 15) {
                pd.dismiss();
                descriptionAddOfficer.setError("Description should contain at least 15 characters!");
            } else {
                mAuth.createUserWithEmailAndPassword(str_email, str_password).addOnCompleteListener(signUp -> {
                    if (!signUp.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "SignUp Unsuccessful, Please try again",
                                Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(getApplicationContext(), "SignUp successfully", Toast.LENGTH_SHORT).show();

                        Officer officer = new Officer(str_id, userType, nameAddOfficer.getText().toString(), phoneNumber, str_email, token,
                                descriptionAddOfficer.getText().toString(), str_password, OfficerListAcceptByAdmin, VisitorListAcceptByAdmin);
                        officer.setUid(signUp.getResult().getUser().getUid());
                        db.collection(Constants.COLLECTION_USER).document(str_email).set(officer)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Officer Added to database", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        pd.dismiss();
                    }
                });
            }
        });
    }
}