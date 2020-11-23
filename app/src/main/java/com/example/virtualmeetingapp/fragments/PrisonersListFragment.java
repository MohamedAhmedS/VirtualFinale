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
import com.example.virtualmeetingapp.adapter.PrisonersAdapter;
import com.example.virtualmeetingapp.models.Prisoner;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PrisonersListFragment extends BaseFragment {

    private TextView tvVisitorNoPer;
    private FirebaseFirestore fireStoreDB;
    private PrisonersAdapter prisonersAdapter;
    private RecyclerView prisonersRecyclerView;
    private ProgressBar pb;
    private List<Prisoner> prisonerList;

    @Override
    public void initXML(View view) {
        pb = view.findViewById(R.id.pb);
        prisonersRecyclerView = view.findViewById(R.id.prisonersList);
        tvVisitorNoPer = view.findViewById(R.id.tvPrisonerNoPer);
    }

    @Override
    public void initVariables() {
        prisonerList =new ArrayList<>();
        prisonersAdapter = new PrisonersAdapter(prisonerList);

        fireStoreDB = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View fragmentView = inflater.inflate(R.layout.prisoners_list_fragment, container, false);
        initXML(fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();

//        officersAdapter = new OfficersAdapter();
//        officerListAdapter.setAdapter(officersAdapter);

        prisonersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        prisonersRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        prisonersRecyclerView.setAdapter(prisonersAdapter);

//        fireStoreDB.collection(Constants.USERS_COLLECTION).whereEqualTo("userType", Constants.USER_TYPE_PRISONER).get()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()){
//                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    } else {
//                        List<Officer> officers = task.getResult().toObjects(Officer.class);
//                        prisonerList.addAll(officers);
//                        prisonersRecyclerView.setAdapter(officersAdapter);
//                    }
//                });

        Query prisonerListQuery = fireStoreDB.collection(Constants.COLLECTION_USER).whereEqualTo("userType", Constants.USER_TYPE_PRISONER);

        if (admin != null || officer != null || (visitor != null && visitor.isPriListApvByOfficer())) {
            prisonerListQuery.get().addOnCompleteListener(prisonerListTask -> {
                pb.setVisibility(View.GONE);
                if (!prisonerListTask.isSuccessful() && prisonerListTask.getException() != null) {
                    ToastHelper.showToast(prisonerListTask.getException().getMessage());
                } else if (prisonerListTask.getResult() == null) {
                    tvVisitorNoPer.setText(Constants.UNAUTHORIZED_ACCESS);
                    tvVisitorNoPer.setVisibility(View.VISIBLE);
                    prisonersRecyclerView.setVisibility(View.GONE);
                } else {
                    List<Prisoner> prisoners = prisonerListTask.getResult().toObjects(Prisoner.class);
                    prisonerList.addAll(prisoners);
                    prisonersAdapter.notifyDataSetChanged();

                    tvVisitorNoPer.setVisibility(View.GONE);
                    prisonersRecyclerView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            pb.setVisibility(View.GONE);
            prisonersRecyclerView.setVisibility(View.GONE);

            tvVisitorNoPer.setText(Constants.UNAUTHORIZED_ACCESS);
            tvVisitorNoPer.setVisibility(View.VISIBLE);
        }


    }

}
