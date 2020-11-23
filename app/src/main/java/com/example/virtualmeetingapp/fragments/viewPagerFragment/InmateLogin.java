package com.example.virtualmeetingapp.fragments.viewPagerFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.activites.PrisonersActivity;
import com.example.virtualmeetingapp.models.Prisoner;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class InmateLogin extends Fragment {
    EditText email, password;
    Button login;
    FirebaseAuth auth;

    public static InmateLogin newInstance() {
        return new InmateLogin();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_inmate_login, container, false);

        email = view.findViewById(R.id.inmateEmail);
        password = view.findViewById(R.id.inmatePassword);
        login = view.findViewById(R.id.btnLogin);
        auth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(getContext());
                pd.setMessage("Please wait...");
                pd.show();

                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_email) && TextUtils.isEmpty(str_password) && !str_email.contains("inmate")) {
                    pd.dismiss();
                    email.setError("Email is required!");
                    password.setError("Password is required!");
                } else {
                    auth.signInWithEmailAndPassword(str_email, str_password.toString())
                            .addOnCompleteListener(task -> {
                                pd.dismiss();
                                if (task.isSuccessful()) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection(Constants.COLLECTION_USER).document(str_email).get().addOnCompleteListener(fetchUserTask -> {
                                        if (fetchUserTask.isSuccessful() && fetchUserTask.getResult() != null) {
                                            Prisoner currentUser = fetchUserTask.getResult().toObject(Prisoner.class);

                                            if (currentUser != null && currentUser.getUserType().equals(Constants.USER_TYPE_PRISONER)) {

//                                                Global.updateDeviceToken(currentUser.getUid());
                                                new SystemPrefs().saveUserSession(currentUser, Constants.USER_TYPE_PRISONER);
                                                Intent intent = new Intent(getContext(), PrisonersActivity.class);
                                                startActivity(intent);
                                                getActivity().finish();
                                            } else {
                                                Toast.makeText(getActivity(), "Only Prisoners can login.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), fetchUserTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
//
        return view;
    }
}

//auth.signInWithEmailAndPassword(str_email, str_password)
//        .addOnCompleteListener((Activity) requireContext(), new OnCompleteListener<AuthResult>() {
//@Override
//public void onComplete(@NonNull Task<AuthResult> task) {
//        if (task.isSuccessful()) {
////                                        if (Objects.requireNonNull(auth.getCurrentUser()).isEmailVerified()) {
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_COLLECTION);
//
//        mDatabase.orderByChild("email").equalTo(str_email)
//        .addListenerForSingleValueEvent(new ValueEventListener() {
//@Override
//public void onDataChange(DataSnapshot dataSnapshot) {
//        pd.dismiss();
//
//        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//        Log.d("userTypeF", userSnapshot.child("userType").getValue(String.class));
//
//
//        User userModel = new User(userSnapshot.child("id").getValue(String.class),
//        userSnapshot.child("userType").getValue(String.class),
//        userSnapshot.child("userName").getValue(String.class),
//        null, null, null, null);
////
//        new SystemPrefs(requireActivity()).setObjectData(Constants.USER, (Object) userModel);
//
//        Intent intent = new Intent(getContext(), MainActivity.class);
//        startActivity(intent);
//        }
//        }
//
//@Override
//public void onCancelled(@NonNull DatabaseError databaseError) {
//        pd.dismiss();
//        }
//        });
////                                        } else {
////                                            pd.dismiss();
////                                            Toast.makeText(LoginActivity.this, "Please, verify your e-mail address", Toast.LENGTH_SHORT).show();
////                                        }
//
//        } else {
//        pd.dismiss();
//        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
//        }
//        }
//        });