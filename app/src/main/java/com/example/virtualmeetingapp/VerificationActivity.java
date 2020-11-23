package com.example.virtualmeetingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OtpView;


public class VerificationActivity extends AppCompatActivity {
  private Button validateButton;
  private OtpView otpView;
  private android.app.ProgressDialog bd;
  private String mVerificationId;
  private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
  private FirebaseAuth mAuth;
  private PhoneAuthProvider.ForceResendingToken mResendToken;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_verification);
    initializeUi();

    bd = new ProgressDialog(this);

    Intent intent = getIntent();
    String mobile = intent.getStringExtra("mobile");
    String verificationCode = otpView.getText().toString();





//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
//        signInWithPhoneAuthCredential(credential);


//    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//      @Override
//      public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
//      {
////        signInWithPhoneAuthCredential(phoneAuthCredential);
//      }
//
//      @Override
//      public void onVerificationFailed(FirebaseException e)
//      {
//        Toast.makeText(VerificationActivity.this, "Something went wrong. . .", Toast.LENGTH_SHORT).show();
//
//        bd.dismiss();
//
//      }
//    };

    validateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String code = otpView.getText().toString().trim();
        if (code.isEmpty() || code.length() < 6) {
          otpView.setError("Enter valid code");
          return;
        }else {
          bd.setTitle("Code Verification");
          bd.setMessage("Please wait, while we are verifying your code.");
          bd.setCanceledOnTouchOutside(false);
          bd.show();
        }

        //verifying the code entered manually
        verifyVerificationCode(code);
      }
    });

//    setListeners();


  }

  //the method is sending verification code
  //the country id is concatenated
  //you can take the country id as user input as well
  private void sendVerificationCode(String mobile) {
//    PhoneAuthProvider.getInstance().verifyPhoneNumber(
//            "+" + mobile,                 //phoneNo that is given by user
//            60,                             //Timeout Duration
//            TimeUnit.SECONDS,                   //Unit of Timeout
//            TaskExecutors.MAIN_THREAD,          //Work done on main Thread
//            callbacks);                       // OnVerificationStateChangedCallbacks
  }


  //the callback to detect the verification status
  private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
          new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

              //Getting the code sent by SMS
              String code = phoneAuthCredential.getSmsCode();

              //sometime the code is not detected automatically
              //in this case the code will be null
              //so user has to manually enter the code
              if (code != null) {
                otpView.setText(code);
                //verifying the code
                verifyVerificationCode(code);
              }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
              Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
              Log.e("TAG",e.getMessage() );
            }

            //when the code is generated then this method will receive the code.
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                super.onCodeSent(s, forceResendingToken);

              //storing the verification id that is sent to the user
              mVerificationId = s;
            }
          };

  private void verifyVerificationCode(String code) {
    //creating the credential
    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
    signInWithPhoneAuthCredential(credential);
  }

  //used for signing the user
  private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(VerificationActivity.this,
                    new OnCompleteListener<AuthResult>() {

                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                          //verification successful we will start the profile activity
                          Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          startActivity(intent);

                        } else {

                          //verification unsuccessful.. display an error message

                          String message = "Somthing is wrong, we will fix it soon...";

                          if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid code entered...";
                          }
                          Toast.makeText(VerificationActivity.this,message,Toast.LENGTH_SHORT).show();
                        }
                      }
                    });
  }

//  @Override public void onClick(View v) {
//    if (v.getId() == R.id.btnVerify) {
//      Toast.makeText(this, otpView.getText(), Toast.LENGTH_SHORT).show();
//    }
//  }
//
  private void initializeUi() {
    otpView = findViewById(R.id.otp_view);
    validateButton = findViewById(R.id.btnVerify);
  }
//
//  private void setListeners() {
//    validateButton.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//      }
//    });
//    otpView.setOtpCompletionListener(this);
//  }
//
//  @Override public void onOtpCompleted(String otp) {
//    // do Stuff
//    Toast.makeText(this, "OnOtpCompletionListener called", Toast.LENGTH_SHORT).show();
//  }
//
////  private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
////    mAuth.signInWithCredential(credential)
////            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
////              @Override
////              public void onComplete(@NonNull Task<AuthResult> task) {
////                if (task.isSuccessful())
////                {
////                  bd.dismiss();
////                  Toast.makeText(VerificationActivity.this, "Congratulations, you are logged in successfully.", Toast.LENGTH_SHORT).show();
////                  sendUserToMainActivity();
////                }
////                else
////                {
////                  bd.dismiss();
////                  String e = task.getException().toString();
////                  Toast.makeText(VerificationActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
////                }
////              }
////            });
////  }
//
//  private void sendUserToMainActivity()
//  {
//    Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
//    startActivity(intent);
//    finish();
//  }
}
