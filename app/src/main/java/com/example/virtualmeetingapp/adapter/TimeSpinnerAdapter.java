package com.example.virtualmeetingapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.models.AppointTimeItem;

import java.util.List;

public class TimeSpinnerAdapter extends ArrayAdapter<AppointTimeItem> {

    List<AppointTimeItem> appointmentTimeItems;

    public TimeSpinnerAdapter(Context context, List<AppointTimeItem> appointTimeItems) {
        super(context, 0, appointTimeItems);
        this.appointmentTimeItems = appointTimeItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        AppointTimeItem appointTimeItem = appointmentTimeItems.get(position);
        if (!appointTimeItem.isAvailable()) {
            return false;
        }
        return super.isEnabled(position);

    }

    private View initView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(
                R.layout.time_spinner_row, parent, false
        );
        TextView customTime, customAvail;
        customTime = convertView.findViewById(R.id.customTime);
        customAvail = convertView.findViewById(R.id.customAvail);

        AppointTimeItem appointTimeItem = appointmentTimeItems.get(position);

        if (appointTimeItem != null) {
            customTime.setText(appointTimeItem.getTvTime());
            if (!appointTimeItem.isAvailable()) {
                customAvail.setText("(Not Available)");
                customAvail.setTextColor(Color.RED);
            }
        }


        return convertView;
    }

    class AppointmentTimeViewHolder {
        TextView time, available;

        public AppointmentTimeViewHolder(View view) {
            time = view.findViewById(R.id.customTime);
            available = view.findViewById(R.id.customAvail);
        }
    }
}






