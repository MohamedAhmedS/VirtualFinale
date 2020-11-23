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
import com.example.virtualmeetingapp.adapter.OfficersAdapter;
import com.example.virtualmeetingapp.models.Officer;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class OfficersListFragment extends BaseFragment {

    private TextView tvOfficerNoPer;
    private ProgressBar pb;
    private RecyclerView officerRecyclerView;

    private List<Officer> officerList;
    private OfficersAdapter officersAdapter;

    private FirebaseFirestore fireStoreDB;


    @Override
    public void initXML(View view) {
        pb = view.findViewById(R.id.pb);
        officerRecyclerView = view.findViewById(R.id.officerList);
        tvOfficerNoPer = view.findViewById(R.id.tvOfficerNoPer);
    }

    @Override
    public void initVariables() {
        officerList = new ArrayList<>();
        officersAdapter = new OfficersAdapter(officerList);

        fireStoreDB = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.officers_list_fragment, container, false);
        initXML(fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();

        officerRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        officerRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));

        officerRecyclerView.setAdapter(officersAdapter);
        Query officerListQuery = fireStoreDB.collection(Constants.COLLECTION_USER).whereEqualTo("userType", Constants.USER_TYPE_OFFICER);

        //admin approval to view other officers not needed now
        //&& officer.isOfficerListAcceptByAdmin()
        if (admin != null || (officer != null ) ||
                (visitor != null && visitor.isOffListApvByOfficer())) {
            officerListQuery.get().addOnCompleteListener(officerListTask -> {
                pb.setVisibility(View.GONE);
                if (!officerListTask.isSuccessful() && officerListTask.getException() != null) {
                    ToastHelper.showToast(officerListTask.getException().getMessage());
                } else if (officerListTask.getResult() == null) {
                    tvOfficerNoPer.setText(Constants.UNAUTHORIZED_ACCESS);
                    tvOfficerNoPer.setVisibility(View.VISIBLE);
                    officerRecyclerView.setVisibility(View.GONE);
                } else {
                    List<Officer> officers = officerListTask.getResult().toObjects(Officer.class);
                    officerList.addAll(officers);
                    officersAdapter.notifyDataSetChanged();

                    tvOfficerNoPer.setVisibility(View.GONE);
                    officerRecyclerView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            pb.setVisibility(View.GONE);
            officerRecyclerView.setVisibility(View.GONE);

            tvOfficerNoPer.setText(Constants.UNAUTHORIZED_ACCESS);
            tvOfficerNoPer.setVisibility(View.VISIBLE);
        }
    }
}
