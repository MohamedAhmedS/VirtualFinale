package com.example.virtualmeetingapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.models.AppointmentModel;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.ToastHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AppointmentResponseAdapter extends RecyclerView.Adapter<AppointmentResponseAdapter.ViewHolder> {
    private Context mContext;
    private User currentUser;
    private List<AppointmentModel> appointmentModelList;

    FirebaseFirestore db;

    public AppointmentResponseAdapter(List<AppointmentModel> appointmentModelList) {
        this.appointmentModelList = appointmentModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        currentUser = (User) Global.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_response_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentModel appointment = appointmentModelList.get(position);

//        holder.username.setText("Your Request to talk with prisoner " + appointment.getPrisonerName() + " is still pending.");
//        String officerUID = currentUser.getUid();
//        String officerName = currentUser.getUserName();


        if (!currentUser.getUserType().equals(Constants.USER_TYPE_VISITOR)) {
            holder.btnApproveAppointment.setVisibility(View.GONE);
        } else {
            db.collection(Constants.COLLECTION_APPOINTMENTS).document(String.valueOf(appointment.getAppointmentId())).get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()){
                            ToastHelper.showToast(task.getException().getMessage());
                        } else {
                            if (appointment.getAppointmentStatus().equals("pending")){
                                holder.username.setText("Your Request to talk with prisoner " + appointment.getPrisonerName() + " is still pending at:");
                                holder.date.setText(appointment.getAppointmentDate());
                                holder.time.setText(appointment.getAppointmentTime());
                                holder.username.setTextColor(Color.RED);
                            } else if (appointment.getAppointmentStatus().equals("rejected")){
                                holder.username.setText("Your request to talk with prisoner " + appointment.getPrisonerName()
                                        + " is rejected by officer " + appointment.getOfficerName() + " at:");
                                holder.date.setText(appointment.getAppointmentDate());
                                holder.time.setText(appointment.getAppointmentTime());
                                holder.username.setTextColor(Color.RED);
                            } else {
                                holder.username.setText("Your request to talk with prisoner " + appointment.getPrisonerName()
                                        + " is approved by officer " + appointment.getOfficerName() + " at:");
                                holder.date.setText(appointment.getAppointmentDate());
                                holder.time.setText(appointment.getAppointmentTime());
                                holder.username.setTextColor(Color.GREEN);
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return appointmentModelList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ProgressBar pb;
        Button btnApproveAppointment;
        TextView username, time, date;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.tvDateRes);
            time = itemView.findViewById(R.id.tvTimeRes);
            btnApproveAppointment = itemView.findViewById(R.id.btnApproveAppointment);
            layout = itemView.findViewById(R.id.layout);
            pb = itemView.findViewById(R.id.pb);
        }
    }
}
