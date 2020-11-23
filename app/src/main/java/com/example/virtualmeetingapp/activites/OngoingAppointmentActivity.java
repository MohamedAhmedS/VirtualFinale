package com.example.virtualmeetingapp.activites;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.adapter.OngoingAppointmentAdapter;
import com.example.virtualmeetingapp.interfaces.OngoingAdapterListener;
import com.example.virtualmeetingapp.models.AppointmentModel;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OngoingAppointmentActivity extends BaseActivity {

    @Override
    public void initXML() {
        rvOngoingAppointments = findViewById(R.id.rvOngoingAppointments);
        tvNoRecords = findViewById(R.id.tvNoRecords);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void initVariables() {

    }

    private TextView tvNoRecords;
    private ProgressBar progressBar;
    private RecyclerView rvOngoingAppointments;
    private Runnable btnMessageRunnable = new Runnable() {
        @Override
        public void run() {
            fetchOngoingAppointments();
            btnMessageHandler.postDelayed(this, 30000);
        }
    };
    private Handler btnMessageHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_appointments);

        initXML();
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnMessageHandler.post(btnMessageRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        btnMessageHandler.removeCallbacksAndMessages(null);
    }

    private void fetchOngoingAppointments() {
        progressBar.setVisibility(View.VISIBLE);
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
        FirebaseFirestore.getInstance().collection(Constants.COLLECTION_VIDEO_CALLS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            ToastHelper.showToast(task.getException().getMessage());

                            toggleEmptyState(true);
                        } else if (task.getResult() != null) {
                            List<AppointmentModel> appointmentModels = task.getResult().toObjects(AppointmentModel.class);
                            if (appointmentModels.size() > 0) {
                                rvOngoingAppointments.setAdapter(new OngoingAppointmentAdapter(appointmentModels, officer.getUserName(), new OngoingAdapterListener() {
                                    @Override
                                    public void refreshList() {
                                        fetchOngoingAppointments();
                                    }
                                }));

                                toggleEmptyState(false);
                            } else {
                                toggleEmptyState(true);
                            }
                        } else {
                            toggleEmptyState(true);
                        }
                    }
                });
    }

    private void toggleEmptyState(boolean showNoRecords) {
        if (showNoRecords) {
            tvNoRecords.setVisibility(View.VISIBLE);
            rvOngoingAppointments.setVisibility(View.GONE);
        } else {
            tvNoRecords.setVisibility(View.GONE);
            rvOngoingAppointments.setVisibility(View.VISIBLE);
        }
    }
}
