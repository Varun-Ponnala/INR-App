package com.example.inrapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

public  class NotificationRec extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        context.getApplicationContext().stopService(new Intent(context, RingtoneService.class));

    }
}