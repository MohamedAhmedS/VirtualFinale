package com.example.virtualmeetingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.activites.AppointmentDetailsActivity;
import com.example.virtualmeetingapp.models.AppointmentModel;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private Context mContext;
    private User currentUser;
    private List<AppointmentModel> appointmentModelList;

    FirebaseFirestore db;

    public AppointmentAdapter(List<AppointmentModel> appointmentModelList) {
        this.appointmentModelList = appointmentModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        currentUser = (User) Global.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentModel appointment = appointmentModelList.get(position);

        holder.username.setText("Visitor " + appointment.getVisitorName() + " wants to talk with prisoner "
                + appointment.getPrisonerName() + " at: ");
        holder.date.setText(appointment.getAppointmentDate());
        holder.time.setText(appointment.getAppointmentTime());
        String officerUID = currentUser.getUid();
        String officerName = currentUser.getUserName();

        if (!currentUser.getUserType().equals(Constants.USER_TYPE_OFFICER)) {
            holder.btnReject.setVisibility(View.GONE);
            holder.btnApproveAppointment.setVisibility(View.GONE);
            holder.tvVisitorNoPer.setVisibility(View.VISIBLE);
        } else {
            if (!appointment.isChatApvByOfficer()) {
                holder.btnApproveAppointment.setText("Approve Appointment");
//                holder.btnApproveAppointment.setBackgroundColor(Color.GREEN);
            }

            if (appointment.getAppointmentStatus().equals("rejected")) {
                holder.btnApproveAppointment.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnReject.setEnabled(false);
                holder.btnReject.setText("Rejected");
            } else {
                holder.btnApproveAppointment.setEnabled(true);
                holder.btnReject.setEnabled(true);
                holder.btnApproveAppointment.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
            }

            if (appointment.getAppointmentStatus().equals("approved")) {
                holder.btnReject.setVisibility(View.GONE);
                holder.btnApproveAppointment.setEnabled(false);
                holder.btnApproveAppointment.setText("Approved");
            }

            holder.itemView.setOnClickListener(v -> {
                //commented for testing purpose  && appointment.getStartTimeInMillis() < System.currentTimeMillis()
                if (appointment.getAppointmentStatus().equals("approved")) {
                    Intent intent = new Intent(mContext, AppointmentDetailsActivity.class);
                    intent.putExtra("appointmentId", appointment.getAppointmentId());
                    mContext.startActivity(intent);
                } else {
                    ToastHelper.showToast("Appointment is rejected");
                }
            });

            holder.btnApproveAppointment.setOnClickListener(v -> {
                Map<String, Object> updateMap = new HashMap<>();
                appointment.setAppointmentStatus("approved");
                updateMap.put("appointmentStatus", "approved");
                appointment.setChatApvByOfficer(!appointment.isChatApvByOfficer());
                updateMap.put("chatApvByOfficer", appointment.isChatApvByOfficer());
                appointment.setOfficerId(officerUID);
                updateMap.put("officerId", officerUID);
                appointment.setOfficerName(officerName);
                updateMap.put("officerName", officerName);

                Map<String, String> notificationMap = new HashMap<>();
                notificationMap.put("type", "appointment");
                notificationMap.put("message", "You appointment on " + appointment.getAppointmentDate() + " with Prisoner " +
                        appointment.getPrisonerName() + " is approved by Officer " + appointment.getOfficerName());
                notificationMap.put("receiverId", appointment.getVisitorId());
                notificationMap.put("appointmentId", String.valueOf(appointment.getAppointmentId()));
                notificationMap.put("title", "Appointment Approved");
                FirebaseDatabase.getInstance().getReference(Constants.NOTIFICATIONS).child(appointment.getVisitorId()).push().setValue(notificationMap);


                holder.pb.setVisibility(View.VISIBLE);
                FirebaseFirestore.getInstance().collection(Constants.COLLECTION_APPOINTMENTS)
                        .document(String.valueOf(appointment.getAppointmentId()))
                        .update(updateMap)
                        .addOnCompleteListener(task -> {
                            holder.pb.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "Update Successful", Toast.LENGTH_SHORT).show();
                                if (!appointment.isChatApvByOfficer()) {
                                    holder.btnApproveAppointment.setText("Approve Appointment");
//                                    holder.btnApproveAppointment.setBackgroundColor(Color.GREEN);
                                } else {
                                    holder.btnReject.setVisibility(View.GONE);
                                    holder.btnApproveAppointment.setEnabled(false);
                                    holder.btnApproveAppointment.setText("Approved");
                                }
                            }
                        });
            });

            holder.btnReject.setOnClickListener(v -> {
                Map<String, Object> updateMap1 = new HashMap<>();
                appointment.setAppointmentStatus("rejected");
                updateMap1.put("appointmentStatus", "rejected");
                appointment.setOfficerId(officerUID);
                updateMap1.put("officerId", officerUID);
                appointment.setOfficerName(officerName);
                updateMap1.put("officerName", officerName);

                Map<String, String> notificationMap = new HashMap<>();
                notificationMap.put("type", "appointment");
                notificationMap.put("message", "You appointment on " + appointment.getAppointmentDate() + " with Prisoner " +
                        appointment.getPrisonerName() + " is rejected by Officer " + appointment.getOfficerName());
                notificationMap.put("receiverId", appointment.getVisitorId());
                notificationMap.put("appointmentId", String.valueOf(appointment.getAppointmentId()));
                notificationMap.put("title", "Appointment Rejected");
                FirebaseDatabase.getInstance().getReference(Constants.NOTIFICATIONS).child(appointment.getVisitorId()).push().setValue(notificationMap);

                holder.pb.setVisibility(View.VISIBLE);
                FirebaseFirestore.getInstance().collection(Constants.COLLECTION_APPOINTMENTS)
                        .document(String.valueOf(appointment.getAppointmentId()))
                        .update(updateMap1)
                        .addOnCompleteListener(task -> {
                            holder.pb.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "Update Successful", Toast.LENGTH_SHORT).show();
                                holder.btnReject.setText("Rejected");
                                holder.btnReject.setEnabled(false);
                                holder.btnReject.setBackgroundColor(Color.RED);
                                holder.btnApproveAppointment.setVisibility(View.GONE);
                            }
                        });
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return appointmentModelList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ProgressBar pb;
        Button btnApproveAppointment, btnReject;
        TextView username, tvVisitorNoPer, time, date;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.tvDate);
            time = itemView.findViewById(R.id.tvTime);
            btnApproveAppointment = itemView.findViewById(R.id.btnApproveAppointment);
            btnReject = itemView.findViewById(R.id.btnReject);
            layout = itemView.findViewById(R.id.layout);
            pb = itemView.findViewById(R.id.pb);
        }
    }
}
