package com.example.virtualmeetingapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.CallingActivity;
import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.activites.ChatActivity;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private List<User> mUser;

    private FirebaseUser firebaseUser;
    private boolean isFragment;
    private String userName = "", profileImage = "";
    private String calledBy = "";
//    private ProfileFragment profileFragment = new ProfileFragment();

    public UserAdapter(Context context, List<User> users, boolean isFragment) {
        mContext = context;
        mUsers = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ImageViewHolder holder, final int position) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userID = firebaseUser.getUid();

//        holder.userItem.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_anim));

        final Activity activity = (Activity) mContext;
        final User user = mUsers.get(position);

//        if (!user.getId().equals(userID) && userID != null) {
        if (!user.getUserEmail().equals(userID) && userID != null) {

//            holder.btn_follow.setVisibility(View.VISIBLE);
//            isFollowing(user.getId(), holder.btn_follow);

            holder.username.setText(user.getUserName());
            holder.userType.setText(user.getUserType());
//            holder.id.setText(user.getId());
            holder.id.setText(user.getUserEmail());

//            if (user.getImageUrl() != null) {
//                Glide.with(mContext).load(user.getImageUrl()).into(holder.image_profile);
//            } else {
//            }
            Picasso.get().load(R.drawable.placeholder).into(holder.image_profile);

            checkForReceivingCall();

            holder.btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
//                        intent.putExtra("hisUid", user.getId());
                    intent.putExtra("hisUid", user.getUserEmail());
                    intent.putExtra("myUid", firebaseUser.getUid());
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();

                }

            });

            holder.btnVideoChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, CallingActivity.class);
//                        intent.putExtra("hisUid", user.getId());
                    intent.putExtra("hisUid", user.getUserEmail());
                    intent.putExtra("myUid", firebaseUser.getUid());
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();

                }
            });
        } else {
            holder.userItem.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView username, userType, id;
        public CircleImageView image_profile;
        carbon.widget.TextView btnChat, btnVideoChat;
        ConstraintLayout userItem;

        ImageViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            userType = itemView.findViewById(R.id.tvUserType);
            id = itemView.findViewById(R.id.id);
            image_profile = itemView.findViewById(R.id.image_profile);
            btnChat = itemView.findViewById(R.id.btnChat);
            btnVideoChat = itemView.findViewById(R.id.btnVideoChat);
            userItem = itemView.findViewById(R.id.userItem);
        }
    }

    private void checkForReceivingCall() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child(Constants.COLLECTION_USER);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        usersRef.child(currentUserId)
                .child("Ringing")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("ringing")) {
                            calledBy = dataSnapshot.child("ringing").getValue().toString();

                            Intent callingIntent = new Intent(mContext, CallingActivity.class);
                            callingIntent.putExtra("hisUid", calledBy);
                            mContext.startActivity(callingIntent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void filterList(ArrayList<User> filteredList) {
        mUsers = filteredList;
        notifyDataSetChanged();
    }

}
