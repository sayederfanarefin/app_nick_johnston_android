package com.bulbinc.nick.push;


import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NemesisMessagingService extends FirebaseMessagingService {
    private static final String TAG = "============FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());


        //send json


        Intent i = new Intent();
        i.setAction("com.neonsofts.www.nemesis");
        i.putExtra("noti", remoteMessage.getNotification().getBody());
        sendBroadcast(i);

    }
}