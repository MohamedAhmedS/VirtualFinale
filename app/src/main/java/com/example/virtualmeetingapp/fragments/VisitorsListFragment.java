package com.example.virtualmeetingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.adapter.VisitorAdapter;
import com.example.virtualmeetingapp.models.Visitor;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class VisitorsListFragment extends BaseFragment {

    private TextView tvVisitorNoPer;
    private ProgressBar pb;
    RecyclerView visitorRecyclerView;

    private List<Visitor> visitorsList;
    private VisitorAdapter visitorAdapter;

    FirebaseFirestore fireStoreDB;

    @Override
    public void initXML(View view) {
        pb = view.findViewById(R.id.pb);
        visitorRecyclerView = view.findViewById(R.id.visitorList);
        tvVisitorNoPer = view.findViewById(R.id.tvVisitorNoPer);
    }

    @Override
    public void initVariables() {
        visitorsList = new ArrayList<>();
        visitorAdapter = new VisitorAdapter(visitorsList);

        fireStoreDB = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.visitors_list_fragment, container, false);
        initXML(fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();

        visitorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        visitorRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));

        visitorRecyclerView.setAdapter(visitorAdapter);

        Query visitorListQuery = fireStoreDB.collection(Constants.COLLECTION_USER).whereEqualTo("userType", Constants.USER_TYPE_VISITOR);
        if (admin != null || officer.isVisitorListAcceptByAdmin()) {
            visitorListQuery.get().addOnCompleteListener(visitorListTask -> {
                pb.setVisibility(View.GONE);
                if (!visitorListTask.isSuccessful() && visitorListTask.getException() != null) {
                    ToastHelper.showToast(visitorListTask.getException().getMessage());
                } else if (visitorListTask.getResult() == null) {
                    tvVisitorNoPer.setText(Constants.UNAUTHORIZED_ACCESS);
                    tvVisitorNoPer.setVisibility(View.VISIBLE);
                    visitorRecyclerView.setVisibility(View.GONE);
                } else {
                    List<Visitor> visitors = visitorListTask.getResult().toObjects(Visitor.class);
                    visitorsList.addAll(visitors);
                    visitorAdapter.notifyDataSetChanged();

                    tvVisitorNoPer.setVisibility(View.GONE);
                    visitorRecyclerView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            pb.setVisibility(View.GONE);
            visitorRecyclerView.setVisibility(View.GONE);

            tvVisitorNoPer.setText(Constants.UNAUTHORIZED_ACCESS);
            tvVisitorNoPer.setVisibility(View.VISIBLE);
        }

//        fireStoreDB.collection(Constants.USERS_COLLECTION).whereEqualTo("userType", Constants.USER_TYPE_VISITOR).get()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        List<Visitor> visitors = task.getResult().toObjects(Visitor.class);
//                        visitorsList.addAll(visitors);
//                        visitorAdapter.notifyDataSetChanged();
//                    }
//                });
    }

}
