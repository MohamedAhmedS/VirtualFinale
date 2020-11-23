package com.example.virtualmeetingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.activites.VisitorProfileActivity;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.models.Visitor;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.ViewHolder> {
    private Context mContext;
    private User currentUser;
    private List<Visitor> visitorsList;

    public VisitorAdapter(List<Visitor> visitorsList) {
        this.visitorsList = visitorsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        currentUser = (User) Global.getCurrentUser();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visitor_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Visitor visitor = visitorsList.get(position);
        holder.username.setText(visitor.getUserName());
        holder.tvUserType.setText(visitor.getUserType());
        holder.tvEmail.setText(visitor.getUserEmail());

        //image code not required now
//        String imageView = visitor.getImage1Url();
//        Picasso.get().load(imageView).into(holder.profileImageVisitor);

        if (!currentUser.getUserType().equals(Constants.USER_TYPE_OFFICER)) {
            holder.btnApprove.setVisibility(View.GONE);
            holder.tvVisitorNoPer.setVisibility(View.VISIBLE);
            if (visitor.isApprovedByAdmin()) {
                holder.tvVisitorNoPer.setVisibility(View.GONE);
            } else {
                holder.tvVisitorNoPer.setTextColor(Color.RED);
                holder.tvVisitorNoPer.setText("This user is not approved by admin.");
            }
        } else {
            if (visitor.isApprovedByAdmin()) {
                holder.btnApprove.setText("Disapprove");
                holder.btnApprove.setBackgroundColor(Color.RED);
            } else {
                holder.btnApprove.setText("Approve");
                holder.btnApprove.setBackgroundColor(Color.GREEN);
            }

            holder.btnApprove.setOnClickListener(v -> {
                Map<String, Object> updateMap = new HashMap<>();
                visitor.setApprovedByAdmin(!visitor.isApprovedByAdmin());
                updateMap.put("approvedByAdmin", visitor.isApprovedByAdmin());

                holder.pb.setVisibility(View.VISIBLE);
                FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USER)
                        .document(visitor.getUserEmail())
                        .update(updateMap)
                        .addOnCompleteListener(task -> {
                            holder.pb.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "Update Successful", Toast.LENGTH_SHORT).show();
                                if (visitor.isApprovedByAdmin()) {
                                    holder.btnApprove.setText("Disapprove");
                                    holder.btnApprove.setBackgroundColor(Color.RED);
                                } else {
                                    holder.btnApprove.setText("Approve");
                                    holder.btnApprove.setBackgroundColor(Color.GREEN);
                                }
                            }
                        });
            });
        }

        /*Code Give permissions to visitor for lists Start */

        if (!currentUser.getUserType().equals(Constants.USER_TYPE_OFFICER)) {
            holder.btnAprOffList.setVisibility(View.GONE);
            holder.btnAprPriList.setVisibility(View.GONE);
            holder.layout.setVisibility(View.GONE);
            holder.tvVisitorNoPer.setVisibility(View.VISIBLE);
            if (visitor.isApprovedByAdmin()) {
                holder.tvVisitorNoPer.setVisibility(View.GONE);
            } else {
                holder.tvVisitorNoPer.setTextColor(Color.RED);
                holder.tvVisitorNoPer.setText("This user is not approved by admin.");
                holder.layout.setVisibility(View.GONE);
                holder.btnAprOffList.setVisibility(View.GONE);
                holder.btnAprPriList.setVisibility(View.GONE);
            }
        } else {
            if (visitor.isOffListApvByOfficer()) {
                holder.layout.setVisibility(View.VISIBLE);
//                holder.btnAprOffList.setVisibility(View.VISIBLE);
//                holder.btnAprOffList.setBackgroundColor(Color.RED);
//                holder.btnAprOffList.setText("Disapprove Officer List");
            } else {
                holder.layout.setVisibility(View.VISIBLE);
//                holder.btnAprOffList.setVisibility(View.VISIBLE);
//                holder.btnAprOffList.setBackgroundColor(Color.GREEN);
//                holder.btnAprOffList.setText("Approve Officer List");
            }

            if (visitor.isPriListApvByOfficer()) {
                holder.layout.setVisibility(View.VISIBLE);
                holder.btnAprPriList.setVisibility(View.VISIBLE);
                holder.btnAprPriList.setBackgroundColor(Color.RED);
                holder.btnAprPriList.setText("Disapprove Prisoner List");
            } else {
                holder.layout.setVisibility(View.VISIBLE);
                holder.btnAprPriList.setVisibility(View.VISIBLE);
                holder.btnAprPriList.setBackgroundColor(Color.GREEN);
                holder.btnAprPriList.setText("Approve Prisoner List");
            }

//          officers list not needed now
//            holder.btnAprOffList.setOnClickListener(v -> {
//                Map<String, Object> updateMap3 = new HashMap<>();
//                visitor.setOffListApvByOfficer(!visitor.isOffListApvByOfficer());
//                updateMap3.put("offListApvByOfficer", visitor.isOffListApvByOfficer());
//
//                holder.pb.setVisibility(View.VISIBLE);
//                FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USER)
//                        .document(visitor.getUserEmail())
//                        .update(updateMap3)
//                        .addOnCompleteListener(task -> {
//                            holder.pb.setVisibility(View.GONE);
//                            if (!task.isSuccessful()) {
//                                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(mContext, "Update Successful", Toast.LENGTH_SHORT).show();
//                                if (visitor.isOffListApvByOfficer()) {
//                                    holder.btnAprOffList.setText("Disapprove Officer List");
//                                    holder.btnAprOffList.setBackgroundColor(Color.RED);
//                                } else {
//                                    holder.btnAprOffList.setText("Approve Officer List");
//                                    holder.btnAprOffList.setBackgroundColor(Color.GREEN);
//                                }
//                            }
//                        });
//            });

            holder.btnAprPriList.setOnClickListener(v -> {
                Map<String, Object> updateMap4 = new HashMap<>();
                visitor.setPriListApvByOfficer(!visitor.isPriListApvByOfficer());
                updateMap4.put("priListApvByOfficer", visitor.isPriListApvByOfficer());

                holder.pb.setVisibility(View.VISIBLE);
                FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USER)
                        .document(visitor.getUserEmail())
                        .update(updateMap4)
                        .addOnCompleteListener(task -> {
                            holder.pb.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "Update Successful", Toast.LENGTH_SHORT).show();
                                if (visitor.isPriListApvByOfficer()) {
                                    holder.btnAprPriList.setText("Disapprove Prisoner List");
                                    holder.btnAprPriList.setBackgroundColor(Color.RED);
                                } else {
                                    holder.btnAprPriList.setText("Approve Prisoner List");
                                    holder.btnAprPriList.setBackgroundColor(Color.GREEN);
                                }
                            }
                        });
            });

            holder.itemView.setOnClickListener(v1 -> {
                if (currentUser.getUserType().equals(Constants.USER_TYPE_OFFICER) ||
                        currentUser.getUserType().equals(Constants.USER_TYPE_VISITOR)) {
                    Intent intent = new Intent(mContext, VisitorProfileActivity.class);
                    intent.putExtra("visitorName", visitor.getUserName());
                    intent.putExtra("visitorEmail", visitor.getUserEmail());
                    intent.putExtra("visitorDescription", visitor.getDescriptionVisitor());
                    intent.putExtra("visitorImage1Url", visitor.getImage1Url());
                    intent.putExtra("visitorImage2Url", visitor.getImage2Url());
                    mContext.startActivity(intent);
                }
            });

        }

        /*Code Give permissions to visitor for lists end */

    }

    @Override
    public int getItemCount() {
        return visitorsList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ProgressBar pb;
        Button btnApprove, btnAprOffList, btnAprPriList;
        TextView username, tvUserType, tvEmail, tvVisitorNoPer;
        ImageView profileImageVisitor;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            tvUserType = itemView.findViewById(R.id.tvUserType);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvVisitorNoPer = itemView.findViewById(R.id.tvVisitorNoPer);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnAprOffList = itemView.findViewById(R.id.btnAprOffList);
            btnAprPriList = itemView.findViewById(R.id.btnAprPriList);
            profileImageVisitor = itemView.findViewById(R.id.profileImageVisitor);
            layout = itemView.findViewById(R.id.layout);
            pb = itemView.findViewById(R.id.pb);
        }
    }
}
