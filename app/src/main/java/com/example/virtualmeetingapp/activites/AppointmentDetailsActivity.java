package com.example.virtualmeetingapp.activites;

import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.adapter.AppointmentDetailsAdapter;
import com.example.virtualmeetingapp.models.AppointmentDetail;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import carbon.view.View;

public class AppointmentDetailsActivity extends BaseActivity {

    ProgressBar progressBar;
    RecyclerView rvAppointmentDetails;
    TextView tvNoRecords;

    AppointmentDetailsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        initXML();
        initVariables();

        if (getIntent() != null) {
            tvNoRecords.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            long appointmentId = getIntent().getLongExtra("appointmentId", -1);
            fetchAppointmentDetails(appointmentId);
        }else{
            tvNoRecords.setVisibility(View.VISIBLE);
        }
    }

    private void fetchAppointmentDetails(long appointmentId) {
        progressBar.setVisibility(View.GONE);
        FirebaseFirestore.getInstance().collection(Constants.COLLECTION_APPOINTMENT_DETAILS)
                .whereEqualTo("appointmentId", appointmentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<AppointmentDetail> appointmentDetailList = task.getResult().toObjects(AppointmentDetail.class);
                        if(appointmentDetailList.size() > 0) {
                            adapter = new AppointmentDetailsAdapter(appointmentDetailList);
                            rvAppointmentDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                            rvAppointmentDetails.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
                            rvAppointmentDetails.setAdapter(adapter);

                            rvAppointmentDetails.setVisibility(View.VISIBLE);
                            tvNoRecords.setVisibility(View.GONE);
                        }else{
                            tvNoRecords.setVisibility(View.VISIBLE);
                            rvAppointmentDetails.setVisibility(View.GONE);
                        }
                    } else {
                        ToastHelper.showToast(task.getException().getMessage());
                        tvNoRecords.setVisibility(View.VISIBLE);
                        rvAppointmentDetails.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void initXML() {
        progressBar = findViewById(R.id.progressBar);
        rvAppointmentDetails = findViewById(R.id.rvAppointmentDetail);
        tvNoRecords = findViewById(R.id.tvNoRecords);
    }

    @Override
    public void initVariables() {

    }
}
