package com.example.virtualmeetingapp.activites;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.adapter.VisitorAdapter;
import com.example.virtualmeetingapp.models.Visitor;
import com.example.virtualmeetingapp.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ApproveVisitorActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    VisitorAdapter visitorAdapter;
    RecyclerView visitorList;
    private List<Visitor> visitorsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_visitor);

        visitorList = findViewById(R.id.visitorList);

        visitorAdapter = new VisitorAdapter(visitorsList);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        visitorList.setLayoutManager(manager);
        visitorList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        Boolean isApproved = false;
        db.collection(Constants.COLLECTION_USER).whereEqualTo("userType", Constants.USER_TYPE_VISITOR)
                .whereEqualTo("approvedByAdmin", isApproved).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        List<Visitor> visitors = task.getResult().toObjects(Visitor.class);
                        visitorsList.addAll(visitors);
                        visitorList.setAdapter(visitorAdapter);
                    }
                });

    }
}