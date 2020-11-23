package com.example.virtualmeetingapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.virtualmeetingapp.CallingActivity;

public class CallBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() != null && intent.getAction().equals("calling")){
            String callerID = intent.getStringExtra("callerId");
            String receiverID = intent.getStringExtra("receiverId");
            String appointmentId = intent.getStringExtra("appointmentId");
            String videoCall = intent.getStringExtra("videoCall");

            Intent newIntent = new Intent(context, CallingActivity.class);
            newIntent.putExtra("callerID", callerID);
            newIntent.putExtra("receiverID", receiverID);
            newIntent.putExtra("ringing", true);
            newIntent.putExtra("videoCall", videoCall);
            newIntent.putExtra("appointmentId", appointmentId);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(newIntent);
        }
    }
}
