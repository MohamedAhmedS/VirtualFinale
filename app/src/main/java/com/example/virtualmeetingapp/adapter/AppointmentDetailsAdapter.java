package com.example.virtualmeetingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.models.AppointmentDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentDetailsAdapter extends RecyclerView.Adapter<AppointmentDetailsAdapter.AppointmentDetailsViewHolder> {

    List<AppointmentDetail> itemsList = new ArrayList();

    public AppointmentDetailsAdapter(List<AppointmentDetail> itemsList) {
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public AppointmentDetailsAdapter.AppointmentDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppointmentDetailsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentDetailsAdapter.AppointmentDetailsViewHolder holder, int position) {
        AppointmentDetail appointmentDetail = itemsList.get(position);

        holder.tvCallType.setText(appointmentDetail.getType());
        holder.tvPrisonerName.setText("Prisoner: " + appointmentDetail.getPrisonerName());
        holder.tvVisitorName.setText("Visitor: " + appointmentDetail.getVisitorName());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(appointmentDetail.getCallStartTimeInMillis());
        String startTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US).format(calendar.getTime());

        calendar.setTimeInMillis(appointmentDetail.getCallEndTimeInMillis());
        String endTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US).format(calendar.getTime());
        holder.tvStartTime.setText("Start Time: " + startTime);
        holder.tvEndTime.setText("End Time: " + endTime);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    static class AppointmentDetailsViewHolder extends RecyclerView.ViewHolder {

        TextView tvPrisonerName, tvVisitorName, tvStartTime, tvEndTime, tvCallType;

        public AppointmentDetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvVisitorName = itemView.findViewById(R.id.tvVisitor);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvPrisonerName = itemView.findViewById(R.id.tvPrisoner);
            tvCallType = itemView.findViewById(R.id.tvCallType);
        }
    }

}
