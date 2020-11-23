package com.example.virtualmeetingapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.activites.VideoActivity;
import com.example.virtualmeetingapp.models.MessageModel;
import com.example.virtualmeetingapp.utils.Global;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private String imageUrl;
    private List<MessageModel> messagesList;

    public MessageAdapter(Context context, List<MessageModel> messagesList, String imageUrl) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layouts: row_chat_left.xml for receiver, row_Chat_right.xml for sender
        if (i == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, viewGroup, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, viewGroup, false);
            return new MessageViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, final int i) {

        MessageModel message = messagesList.get(i);

        //convert time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(message.getTimeStamp());
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        if (message.getType().equals("video")) {
            messageViewHolder.flMedia.setVisibility(View.VISIBLE);

            //text message
            messageViewHolder.messageTv.setVisibility(View.GONE);
            messageViewHolder.messageIv.setVisibility(View.VISIBLE);
            messageViewHolder.playIv.setVisibility(View.VISIBLE);

            messageViewHolder.messageTv.setText(message.getMessage());

            if (messageViewHolder.messageIv.getTag() == null) {
                Bitmap bitmap = null;
                try {
                    Global.retriveVideoFrameFromVideo(message.getMessage(), messageViewHolder.messageIv, messageViewHolder.progressBar);
//                    messageViewHolder.messageIv.setImageBitmap(bitmap);
                    messageViewHolder.messageIv.setTag("filled");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                messageViewHolder.playIv.setOnClickListener(v -> {
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("videoUrl", message.getMessage());
                    intent.putExtra("title", message.getTimeStamp());
                    context.startActivity(intent);
                });
            }
        } else if (message.getType().equals("image")) {
            messageViewHolder.flMedia.setVisibility(View.VISIBLE);
            //image message
            messageViewHolder.messageTv.setVisibility(View.GONE);
            messageViewHolder.messageIv.setVisibility(View.VISIBLE);
            messageViewHolder.playIv.setVisibility(View.GONE);

            Picasso.get().load(message.getMessage())
                    .placeholder(R.drawable.placeholder)
                    .into(messageViewHolder.messageIv);
        } else {
            messageViewHolder.flMedia.setVisibility(View.GONE);

            //text message
            messageViewHolder.messageTv.setVisibility(View.VISIBLE);
            messageViewHolder.messageIv.setVisibility(View.GONE);

            messageViewHolder.messageTv.setText(message.getMessage());
        }

        //set data
        messageViewHolder.messageTv.setText(message.getMessage());
        messageViewHolder.timeTv.setText(dateTime);
        try {
            Picasso.get().load(imageUrl).into(messageViewHolder.profileIv);
        } catch (Exception ignored) {

        }

        //click to show delete dialog
//        messageViewHolder.messageLAyout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //show delete message confirm dialog
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Delete");
//                builder.setMessage("Are you sure to delete this message?");
//                //delete button
//                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
////                        deleteMessage(i);
//                    }
//                });
//                //cancel delete button
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //dismiss dialog
//                        dialog.dismiss();
//                    }
//                });
//                //create and show dialog
//                builder.create().show();
//            }
//        });

        //set seen/delivered status of message
        if (i == messagesList.size() - 1) {
            if (messagesList.get(i).isSeen()) {
                messageViewHolder.isSeenTv.setText("Seen");
            } else {
                messageViewHolder.isSeenTv.setText("Delivered");
            }
        } else {
            messageViewHolder.isSeenTv.setVisibility(View.GONE);
        }
    }
//    private void setTimeTextVisibility(long ts1, long ts2, TextView timeview){
//
//        if(ts2==0){
//            timeview.setVisibility(View.VISIBLE);
//            timeview.setText(Utils.formatDayTimeHtml(ts1));
//        }else {
//            Calendar cal1 = Calendar.getInstance();
//            Calendar cal2 = Calendar.getInstance();
//            cal1.setTimeInMillis(ts1);
//            cal2.setTimeInMillis(ts2);
//
//            boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
//                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
//
//            if(sameMonth){
//                timeview.setVisibility(View.GONE);
//                timeview.setText("");
//            }else {
//                timeview.setVisibility(View.VISIBLE);
//                timeview.setText(Utils.formatDayTimeHtml(ts2));
//            }
//
//        }
//    }

//    private void deleteMessage(int position) {
//        final String myUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//
//        Long msgTimeStamp = messagesList.get(position).getTimeStamp();
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
//        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//
//                    if (Objects.equals(ds.child("sender").getValue(), myUID)) {
//
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("message", "This message was deleted...");
//                        ds.getRef().updateChildren(hashMap);
//
//                        Toast.makeText(context, "message deleted...", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(context, "You can delete only your messages...", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed in user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        assert fUser != null;
        if (messagesList.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    //view holder class
    static class MessageViewHolder extends RecyclerView.ViewHolder {

        //views
        ImageView messageIv;
        CardView playIv;
        ProgressBar progressBar;
        ImageView profileIv;
        FrameLayout flMedia;
        TextView messageTv, timeTv, isSeenTv, timeview;
        LinearLayout messageLAyout; //for click listener to show delete

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            profileIv = itemView.findViewById(R.id.profileIv);
            messageIv = itemView.findViewById(R.id.messageIv);
            progressBar = itemView.findViewById(R.id.progressBar);
            playIv = itemView.findViewById(R.id.playIv);
            flMedia = itemView.findViewById(R.id.flMedia);
            messageTv = itemView.findViewById(R.id.messageTv);
            messageLAyout = itemView.findViewById(R.id.messageLayout);
        }
    }
}
