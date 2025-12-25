package com.example.inrapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;

import android.widget.Toast;

public class RingtoneService extends Service {
    MediaPlayer myPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //getting systems default ringtone
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        myPlayer = MediaPlayer.create(this,
                alarmUri);

//        Toast.makeText(this, "Ringtone playing", Toast.LENGTH_LONG).show();
        //setting loop play to true
        //this will make the ringtone continuously playing
        myPlayer.setLooping(false);
        //staring the player
        myPlayer.start();
        //we have some options for service
        //start sticky means service will be explicity started and stopped
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Alarm Stopped", Toast.LENGTH_LONG).show();
        myPlayer.stop();
    }
}