package com.example.virtualmeetingapp.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.virtualmeetingapp.activites.PrisonerProfileActivity;
import com.example.virtualmeetingapp.models.Prisoner;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PrisonersAdapter extends RecyclerView.Adapter<PrisonersAdapter.ViewHolder> {

    private Context mContext;
    //    List<Officer> officerList;
    User currentUser;
    List<Prisoner> prisonerList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PrisonersAdapter(List<Prisoner> prisonerList) {
        this.prisonerList = prisonerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        currentUser = (User) Global.getCurrentUser();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prisoner_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Prisoner prisoner = prisonerList.get(position);
        holder.username.setText(prisoner.getUserName());
        holder.tvUserType.setText(prisoner.getUserType());
        holder.tvEmail.setText(prisoner.getUserEmail());

        if (new SystemPrefs().getUserType().equals(Constants.USER_TYPE_ADMIN)) {
//            holder.MainLayoutMsg.setVisibility(View.GONE);
            holder.btnDetailPrisoner.setVisibility(View.GONE);
        } else if (new SystemPrefs().getUserType().equals(Constants.USER_TYPE_PRISONER)) {
//            holder.MainLayoutMsg.setVisibility(View.GONE);
            holder.btnDetailPrisoner.setVisibility(View.GONE);
        } else if (new SystemPrefs().getUserType().equals(Constants.USER_TYPE_OFFICER)) {
//            holder.MainLayoutMsg.setVisibility(View.GONE);
            holder.btnDetailPrisoner.setVisibility(View.GONE);
        } else {
//            holder.MainLayoutMsg.setVisibility(View.VISIBLE);
            holder.btnDetailPrisoner.setVisibility(View.VISIBLE);

//            holder.btnMsg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Map<String, Object> updateMap1 = new HashMap<>();
//                    Intent intent = new Intent(mContext, ChatActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString(Constants.INTENT_CHAT_UID, prisoner.getUid());
//                    bundle.putString(Constants.INTENT_CHAT_PROFILE_THUMB, "");
//                    intent.putExtras(bundle);
//                    mContext.startActivity(intent);
//                }
//            });

            holder.btnDetailPrisoner.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, PrisonerProfileActivity.class);
                intent.putExtra("prisonerUID", prisoner.getUid());
                mContext.startActivity(intent);
            });

        }

    }

    @Override
    public int getItemCount() {
        return prisonerList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView username, tvUserType, tvEmail;
        Button btnMsg, btnRequestConWithPri, btnDetailPrisoner;
        ProgressBar pb;
        LinearLayout MainLayoutMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            tvUserType = itemView.findViewById(R.id.tvUserType);
            tvEmail = itemView.findViewById(R.id.tvEmail);
//            MainLayoutMsg = itemView.findViewById(R.id.MainLayoutMsg);
//            btnMsg = itemView.findViewById(R.id.btnMsg);
//            btnRequestConWithPri = itemView.findViewById(R.id.btnRequestConWithPri);
            btnDetailPrisoner = itemView.findViewById(R.id.btnDetailPrisoner);
            pb = itemView.findViewById(R.id.pb);

        }

    }

}
