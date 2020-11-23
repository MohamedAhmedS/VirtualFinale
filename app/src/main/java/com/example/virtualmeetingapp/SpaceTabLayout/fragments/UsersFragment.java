package com.example.virtualmeetingapp.SpaceTabLayout.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.adapter.UserAdapter;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private TextView noUser;
    private ImageView btnClose;
    private EditText search_bar;
    private User user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_users_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        noUser = view.findViewById(R.id.noUser);
        search_bar = view.findViewById(R.id.search_bar);
        btnClose = view.findViewById(R.id.btnClose);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        btnClose.setVisibility(View.GONE);


        noUser.setVisibility(View.VISIBLE);


        if (btnClose.getVisibility() == View.GONE) {
            search_bar.setPadding(10, 0, 10, 0);
        }

        user = (User) new SystemPrefs().getObjectData(Constants.COLLECTION_USER, User.class);
        readUsers();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
                btnClose.setVisibility(View.VISIBLE);
                if (search_bar.getText().toString().matches("")) {
                    btnClose.setVisibility(View.GONE);
                }
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        search_bar.setText("");
                        btnClose.setVisibility(View.GONE);
                    }
                });

            }
        });

        return view;
    }

    private void filter(String text) {
        ArrayList<User> filteredList = new ArrayList<>();
        noUser.setVisibility(View.VISIBLE);
        for (User user : userList) {
            if (user.getUserName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(user);
                noUser.setVisibility(View.GONE);
            }
        }
        userAdapter.filterList(filteredList);
    }

    private void readUsers() {


        List<String> listOfficer = Arrays.asList("inmate", "visitor", "officer");
        List<String> listVisitor = Arrays.asList("inmate", "officer");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.COLLECTION_USER);
        String userType = user.getUserType();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_bar.getText().toString().equals("")) {
                    userList.clear();
                    noUser.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
//                        if (!user.getId().equals(firebaseUser.getUid())) {
                        if (!user.getUserEmail().equals(firebaseUser.getUid())) {
                            if (userType.equals("officer")) {
                                if (listOfficer.contains(user.getUserType())) {
                                    userList.add(user);
                                    noUser.setVisibility(View.GONE);
                                }
                            } else if (userType.equals("inmate")) {
                                if (user.getUserType().equals("visitor")) {
                                    userList.add(user);
                                    noUser.setVisibility(View.GONE);
                                }

                            } else if (userType.equals("visitor")) {
                                if (listVisitor.contains(user.getUserType())) {
                                    userList.add(user);
                                    noUser.setVisibility(View.GONE);
                                }

                            } else if (userType.equals("admin")) {
                                userList.add(user);
                                noUser.setVisibility(View.GONE);
                            }
                        }

                    }

                    userAdapter = new UserAdapter(getContext(), userList, true);
                    recyclerView.setAdapter(userAdapter);
                    recyclerView.setItemViewCacheSize(1024);
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                noUser.setVisibility(View.VISIBLE);

            }
        });
    }

}
