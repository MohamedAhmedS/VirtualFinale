package com.example.virtualmeetingapp.activites;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.models.Prisoner;
import com.example.virtualmeetingapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

public class AddPrisonersActivity extends BaseActivity {

    private CountryCodePicker ccpAddPrisoners;
    private EditText phoneAddPrisoners, nameAddPrisoners, descriptionAddPrisoners, emailAddPrisoners, passwordAddPrisoners;
    private EditText emailAddPrisoner;
    private Button btnAddPrisoners;
    private String phoneNumber = "";
    private FirebaseAuth mAuth;

    ProgressDialog pd;

    FirebaseFirestore db;

    @Override
    public void initXML() {
        phoneAddPrisoners = findViewById(R.id.phoneNumberAddPrisoners);
        nameAddPrisoners = findViewById(R.id.nameAddPrisoners);
//        emailAddPrisoners = findViewById(R.id.emailAddPrisoners);
        emailAddPrisoner = findViewById(R.id.emailAddPrisoner);
        passwordAddPrisoners = findViewById(R.id.passwordAddPrisoners);
        descriptionAddPrisoners = findViewById(R.id.descriptionAddPrisoners);
        btnAddPrisoners = findViewById(R.id.btnAddPrisoners);
        ccpAddPrisoners = (CountryCodePicker) findViewById(R.id.ccpAddPrisoners);
        ccpAddPrisoners.registerCarrierNumberEditText(phoneAddPrisoners);
    }

    @Override
    public void initVariables() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prisoners);

        initXML();
        initVariables();
        addPrisoner();
    }

    private void addPrisoner() {
        btnAddPrisoners.setOnClickListener(v -> {
//                phoneNumber = ccpAddPrisoners.getFullNumberWithPlus();

            pd = new ProgressDialog(AddPrisonersActivity.this);
            pd.setMessage("Please wait...");
            pd.show();

            final String str_username = nameAddPrisoners.getText().toString();
//                final String str_phoneNo = phoneAddPrisoners.getText().toString();
//            final String str_phoneNo = "";
            final String str_id = emailAddPrisoner.getText().toString();
            final String str_description = descriptionAddPrisoners.getText().toString();
            final String str_email = "prisoner" + emailAddPrisoner.getText().toString() + "@moi.ae";
            final String str_password = passwordAddPrisoners.getText().toString();
            final String userType = Constants.USER_TYPE_PRISONER;
            final String token = "";


//                TextUtils.isEmpty(str_phoneNo) &&
            if (TextUtils.isEmpty(str_username) &&
                    TextUtils.isEmpty(str_description)) {
                pd.dismiss();
                nameAddPrisoners.setError("Username is required!");
//                    phoneAddPrisoners.setError("Phone Number is required!");
                descriptionAddPrisoners.setError("description is required!");
            }
//                else if (str_phoneNo.length() != 10) {
//                    pd.dismiss();
//                    phoneAddPrisoners.setError("Phone Number should have no less or more than 10 characters!"); }

            else if (str_description.length() < 15) {
                pd.dismiss();
                descriptionAddPrisoners.setError("Description should contain at least 15 characters!");
            } else {
                mAuth.createUserWithEmailAndPassword(str_email, str_password).addOnCompleteListener(signUp -> {
                    if (!signUp.isSuccessful()) {
//                        Toast.makeText(getApplicationContext(), signUp.getException().getMessage(),
//                                Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "SignUp Unsuccessful, Please try again",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Prisoner prisoner = new Prisoner(str_id, userType, nameAddPrisoners.getText().toString(), phoneNumber,
                                str_email, token, descriptionAddPrisoners.getText().toString(), str_password);
                        prisoner.setUid(signUp.getResult().getUser().getUid());
                        db.collection(Constants.COLLECTION_USER).document(str_email).set(prisoner)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Prisoner Added to database",
                                                Toast.LENGTH_SHORT).show();
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