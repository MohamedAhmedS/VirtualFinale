package com.example.virtualmeetingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.interfaces.OngoingAdapterListener;
import com.example.virtualmeetingapp.models.AppointmentModel;
import com.example.virtualmeetingapp.utils.App;
import com.example.virtualmeetingapp.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OngoingAppointmentAdapter extends RecyclerView.Adapter<OngoingAppointmentAdapter.OngoingAppointmentViewHolder> {

    private String officerName;
    private List<AppointmentModel> itemsList;
    private OngoingAdapterListener listener;

    public OngoingAppointmentAdapter(List<AppointmentModel> dataList, String officerName, OngoingAdapterListener listener) {
        itemsList = dataList;
        this.officerName = officerName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OngoingAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OngoingAppointmentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ongoing_appointment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OngoingAppointmentViewHolder holder, int position) {
        AppointmentModel appointmentModel = itemsList.get(position);

        holder.tvDate.setText("Date: " + appointmentModel.getAppointmentDate());
        holder.tvVisitorName.setText("Visitor Name: " + appointmentModel.getVisitorName());
        holder.tvPrisonerName.setText("Prisoner Name: " + appointmentModel.getPrisonerName());
        holder.tvAppointmentId.setText("Appointment#: " + String.valueOf(appointmentModel.getAppointmentId()));

        holder.btnEndAppointment.setOnClickListener(v -> {
            Map<String, Object> updateMap = new HashMap<>();

            updateMap.put("appointmentEndedByOfficer", true);
            updateMap.put("appointmentEndedByOfficerName", this.officerName);
            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_VIDEO_CALLS)
                    .document(String.valueOf(appointmentModel.getAppointmentId()))
                    .update(updateMap);

            listener.refreshList();
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    static class OngoingAppointmentViewHolder extends RecyclerView.ViewHolder {
        public Button btnEndAppointment;
        public TextView tvAppointmentId, tvVisitorName, tvPrisonerName, tvDate;

        public OngoingAppointmentViewHolder(View itemView) {
            super(itemView);
            btnEndAppointment = itemView.findViewById(R.id.btnEndAppointment);
            tvAppointmentId = itemView.findViewById(R.id.tvAppointmentID);
            tvVisitorName = itemView.findViewById(R.id.tvVisitor);
            tvPrisonerName = itemView.findViewById(R.id.tvPrisoner);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
