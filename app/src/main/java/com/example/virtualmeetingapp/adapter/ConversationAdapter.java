package com.example.virtualmeetingapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.activites.ChatActivity;
import com.example.virtualmeetingapp.models.ConversationModel;
import com.example.virtualmeetingapp.utils.Constants;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private Context mContext;
    private HashMap<String, String> lastMessageMap;
    private List<ConversationModel> conversationsList; //get user info

    private FirebaseUser mUser;

    public ConversationAdapter(Context context, List<ConversationModel> conversationsList) {
        this.mContext = context;
        this.conversationsList = conversationsList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_conversation, viewGroup, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ConversationViewHolder viewHolder, final int i) {
        ConversationModel conversaion = conversationsList.get(i);

        viewHolder.nameTv.setText(conversaion.getUserName());
        viewHolder.lastMessageTv.setText(conversaion.getLastMessage());

        if (conversaion.getProfileThumbnail().equals("")) {
            Picasso.get().load(R.drawable.placeholder).into(viewHolder.profileIv);
        } else {
            Picasso.get().load(conversaion.getProfileThumbnail()).into(viewHolder.profileIv);
        }

        //set online status of other users in chatlist
//        if (userList.get(i).getOnlineStatus().equals("online")) {
//            //online
//            myHolder.onlineStatusIv.setImageResource(R.drawable.online);
//        } else {
//            //offline
//            myHolder.onlineStatusIv.setImageResource(R.drawable.offline);
//        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start chat activity with that user
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(Constants.INTENT_CHAT_ID, conversaion.getId());
                intent.putExtra(Constants.INTENT_CHAT_UID, conversaion.getWithUID());
                intent.putExtra(Constants.INTENT_CHAT_PROFILE_THUMB, conversaion.getProfileThumbnail());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public void setLastMessageMap(String userId, String lastMessage) {
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return conversationsList.size(); //size of the list
    }


    static class ConversationViewHolder extends RecyclerView.ViewHolder {
//        ImageView onlineStatusIv;
        CircleImageView profileIv;
        TextView nameTv, lastMessageTv;

        ConversationViewHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            nameTv = itemView.findViewById(R.id.nameTv);
            profileIv = itemView.findViewById(R.id.profileIv);
//            onlineStatusIv = itemView.findViewById(R.id.onlineStatusIv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
        }
    }

    public void filterList(List<ConversationModel> filteredList) {
        conversationsList = filteredList;
        notifyDataSetChanged();
    }
}