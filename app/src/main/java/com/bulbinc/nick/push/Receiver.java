package com.bulbinc.nick.push;

/**
 * Created by erfan on 8/25/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String token = intent.getStringExtra("token");
            String noti = intent.getStringExtra("noti");


        } catch (Exception e) {

        }
    }
}