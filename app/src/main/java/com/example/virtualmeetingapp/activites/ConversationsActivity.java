package com.example.virtualmeetingapp.activites;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.ClientTypeActivity;
import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.adapter.ConversationAdapter;
import com.example.virtualmeetingapp.models.ConversationModel;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carbon.widget.ProgressBar;

public class ConversationsActivity extends BaseActivity {

    private ImageView btnClose;
    private EditText searchBar;
    private TextView tvNoConversations;
    private RecyclerView rvConversations;
    private ProgressBar progressBar;

    private List<ConversationModel> conversationsList;
    private Map<String, ConversationModel> conversationsHashMap;

    private FirebaseFirestore fireStoreDB;
    private ConversationAdapter conversationAdapter;
    private CollectionReference conversationCollectionRef, userCollectionRef;

    @Override
    public void initXML() {
        btnClose = findViewById(R.id.btnClose);
        searchBar = findViewById(R.id.search_bar);
        rvConversations = findViewById(R.id.recyclerView);
        tvNoConversations = findViewById(R.id.noUser);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void initVariables() {
        conversationsHashMap = new HashMap<>();
        conversationsList = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(this, conversationsList);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            ToastHelper.showToast("You Are Not Logged In ... Signing Out");

            Global.clearCurrentUser();
            FirebaseAuth.getInstance().signOut();
            new SystemPrefs().clearUserSession();

            startActivity(new Intent(this, ClientTypeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }

        fireStoreDB = FirebaseFirestore.getInstance();
        userCollectionRef = fireStoreDB.collection(Constants.COLLECTION_USER);
        conversationCollectionRef = fireStoreDB.collection(Constants.COLLECTION_USER_CONVERSATIONS)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(Constants.COLLECTION_CONVERSATIONS);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        initXML();
        initVariables();
        setListeners();
        setUpConversationsRecycler();
        fetchUserConversations();
    }

    private void setListeners() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
                btnClose.setVisibility(View.VISIBLE);
                if (searchBar.getText().toString().matches("")) {
                    btnClose.setVisibility(View.GONE);
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setText("");
                btnClose.setVisibility(View.GONE);
            }
        });
    }

    private void setUpConversationsRecycler() {
        rvConversations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvConversations.setAdapter(conversationAdapter);
    }

    private void filter(String text) {
        List<ConversationModel> filteredList = new ArrayList<>();
        tvNoConversations.setVisibility(View.VISIBLE);
        for (ConversationModel conversation : conversationsList) {
            if (conversation.getUserName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(conversation);
                tvNoConversations.setVisibility(View.GONE);
            }
        }
        conversationAdapter.filterList(filteredList);
    }

    private void fetchUserConversations() {
        conversationCollectionRef.orderBy("lastMessageTimeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener((conversationsList, exception) -> {
                    progressBar.setVisibility(View.GONE);

                    if (exception != null) {
                        ToastHelper.showToast(exception.getMessage());
                        return;
                    }

                    if (conversationsList == null || conversationsList.isEmpty()) {
                        tvNoConversations.setVisibility(View.VISIBLE);
                        rvConversations.setVisibility(View.GONE);
                    } else {
                        tvNoConversations.setVisibility(View.GONE);
                        rvConversations.setVisibility(View.VISIBLE);

                        for (DocumentSnapshot conversationDocument : conversationsList.getDocuments()) {
                            ConversationModel conversation = conversationDocument.toObject(ConversationModel.class);

                            if (!conversation.getWithUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                userCollectionRef.whereEqualTo("uid", conversation.getWithUID())
                                        .addSnapshotListener((conversationUser, e) -> {
                                            User user = conversationUser.toObjects(User.class).get(0);

                                            conversation.setProfileThumbnail("");
                                            conversation.setUserName(user.getUserName());
                                            conversationsHashMap.put(user.getUid(), conversation);

                                            this.conversationsList.clear();
                                            this.conversationsList.addAll(conversationsHashMap.values());
                                            conversationAdapter.notifyDataSetChanged();
                                        });

                        }
                    }
                });
    }


}
