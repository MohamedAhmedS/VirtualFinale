package com.example.virtualmeetingapp.activites;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.adapter.OfficersAdapter;
import com.example.virtualmeetingapp.models.Officer;
import com.example.virtualmeetingapp.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OfficersListActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    OfficersAdapter officersAdapter;
    RecyclerView officerRecyclerView;
    private List<Officer> officerList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officers_list);

        officerList = findViewById(R.id.officerList);

        officersAdapter = new OfficersAdapter(officerList);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        officerRecyclerView.setLayoutManager(manager);
        officerRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        db.collection(Constants.COLLECTION_USER).whereEqualTo("userType", Constants.USER_TYPE_OFFICER).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        List<Officer> officers = task.getResult().toObjects(Officer.class);
                        officerList.addAll(officers);
                        officerRecyclerView.setAdapter(officersAdapter);
                    }
                });

    }
}