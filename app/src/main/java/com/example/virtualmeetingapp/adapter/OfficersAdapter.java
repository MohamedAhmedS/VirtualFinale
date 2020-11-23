package com.example.virtualmeetingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.virtualmeetingapp.activites.ChatActivity;
import com.example.virtualmeetingapp.models.Officer;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfficersAdapter extends RecyclerView.Adapter<OfficersAdapter.ViewHolder> {

    private Context mContext;
    List<Officer> officerList;

    public OfficersAdapter(List<Officer> officerList) {
        this.officerList = officerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_officer_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Officer officer = officerList.get(position);
        holder.username.setText(officer.getUserName());
        holder.tvUserType.setText(officer.getUserType());
        holder.tvEmail.setText(officer.getUserEmail());

//        if (new SystemPrefs().getUserType().equals(Constants.USER_TYPE_VISITOR)){
//            holder.MainLayoutOfficerMsgBtn.setVisibility(View.VISIBLE);
//            holder.btnOfficerMsg.setVisibility(View.VISIBLE);
//            holder.btnOfficerMsg.setBackgroundColor(Color.GREEN);
//        }
//
//        if (!new SystemPrefs().getUserType().equals(Constants.USER_TYPE_ADMIN)) {
//            holder.btnApprove.setVisibility(View.GONE);
//            holder.btnApproveVisitors.setVisibility(View.GONE);
//        } else if (new SystemPrefs().getUserType().equals(Constants.USER_TYPE_VISITOR)){
//            holder.MainLayoutOfficerAprBtn.setVisibility(View.GONE);
////            holder.btnApprove.setVisibility(View.GONE);
//            holder.btnApproveVisitors.setVisibility(View.GONE);
//            holder.MainLayoutMsg.setVisibility(View.GONE);
//            holder.MainLayoutOfficerMsgBtn.setVisibility(View.VISIBLE);
//            holder.btnOfficerMsg.setVisibility(View.VISIBLE);
//        } else if (new SystemPrefs().getUserType().equals(Constants.USER_TYPE_OFFICER)){
//            holder.MainLayoutMsg.setVisibility(View.GONE);
//            holder.MainLayoutOfficerMsgBtn.setVisibility(View.GONE);
//            holder.btnOfficerMsg.setVisibility(View.GONE);
////            holder.btnMsg.setVisibility(View.GONE);
//        } else {
//            holder.btnApprove.setVisibility(View.GONE);
//            holder.btnApproveVisitors.setVisibility(View.VISIBLE);
////            if (officer.isOfficerListAcceptByAdmin()) {
////                holder.btnApprove.setText("Disapprove Officer List");
////                holder.btnApprove.setBackgroundColor(Color.RED);
////            } else {
////                holder.btnApprove.setText("Approve Officer List");
////                holder.btnApprove.setBackgroundColor(Color.GREEN);
////            }
//            if (officer.isVisitorListAcceptByAdmin()) {
//                holder.btnApproveVisitors.setText("Disapprove Visitor List");
//                holder.btnApproveVisitors.setBackgroundColor(Color.RED);
//            } else {
//                holder.btnApproveVisitors.setText("Approve Visitor List");
//                holder.btnApproveVisitors.setBackgroundColor(Color.GREEN);
//            }
//
//            holder.btnApprove.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Map<String, Object> updateMap1 = new HashMap<>();
//                    officer.setOfficerListAcceptByAdmin(!officer.isOfficerListAcceptByAdmin());
//                    updateMap1.put("officerListAcceptByAdmin", officer.isOfficerListAcceptByAdmin());
//
//                    holder.pb.setVisibility(View.VISIBLE);
//                    FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USER)
//                            .document(officer.getUserEmail())
//                            .update(updateMap1)
//                            .addOnCompleteListener(task -> {
//                                holder.pb.setVisibility(View.GONE);
//                                if (!task.isSuccessful()) {
//                                    Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(mContext, "Update Successful", Toast.LENGTH_SHORT).show();
//                                    if (officer.isOfficerListAcceptByAdmin()) {
//                                        holder.btnApprove.setText("Disapprove");
//                                        holder.btnApprove.setBackgroundColor(Color.RED);
//                                    } else {
//                                        holder.btnApprove.setText("Approve");
//                                        holder.btnApprove.setBackgroundColor(Color.GREEN);
//                                    }
//                                }
//                            });
//                }
//            });
//
//            holder.btnApproveVisitors.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Map<String, Object> updateMap2 = new HashMap<>();
//                    officer.setVisitorListAcceptByAdmin(!officer.isVisitorListAcceptByAdmin());
//                    updateMap2.put("visitorListAcceptByAdmin", officer.isVisitorListAcceptByAdmin());
//
//                    holder.pb.setVisibility(View.VISIBLE);
//                    FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USER)
//                            .document(officer.getUserEmail())
//                            .update(updateMap2)
//                            .addOnCompleteListener(task -> {
//                                holder.pb.setVisibility(View.GONE);
//                                if (!task.isSuccessful()) {
//                                    Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(mContext, "Update Successful", Toast.LENGTH_SHORT).show();
//                                    if (officer.isVisitorListAcceptByAdmin()) {
//                                        holder.btnApproveVisitors.setText("Disapprove Visitor List");
//                                        holder.btnApproveVisitors.setBackgroundColor(Color.RED);
//                                    } else {
//                                        holder.btnApproveVisitors.setText("Approve Visitor List");
//                                        holder.btnApproveVisitors.setBackgroundColor(Color.GREEN);
//                                    }
//                                }
//                            });
//                }
//            });

//        }


        if (new SystemPrefs().getUserType().equals(Constants.USER_TYPE_VISITOR)) {
            holder.MainLayoutOfficerMsgBtn.setVisibility(View.VISIBLE);
            holder.btnOfficerMsg.setVisibility(View.VISIBLE);
        }

        holder.btnOfficerMsg.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("isOfficerChat", true);
            bundle.putString(Constants.INTENT_CHAT_UID, officer.getUid());
            bundle.putString(Constants.INTENT_CHAT_PROFILE_THUMB, "");
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return officerList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView username, tvUserType, tvEmail;
        Button btnApprove, btnOfficerMsg, btnApproveVisitors;
        ProgressBar pb;
        LinearLayout MainLayoutMsg, MainLayoutOfficerAprBtn, MainLayoutOfficerMsgBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            tvUserType = itemView.findViewById(R.id.tvUserType);
            tvEmail = itemView.findViewById(R.id.tvEmail);

            MainLayoutOfficerAprBtn = itemView.findViewById(R.id.MainLayoutOfficerAprBtn);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnApproveVisitors = itemView.findViewById(R.id.btnApproveVisitors);

            btnOfficerMsg = itemView.findViewById(R.id.btnOfficerMsg);
            MainLayoutOfficerMsgBtn = itemView.findViewById(R.id.MainLayoutOfficerMsgBtn);

            pb = itemView.findViewById(R.id.pb);
        }
    }

}
