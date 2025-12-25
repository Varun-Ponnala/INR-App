package com.example.inrapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

public class AlarmReceiver extends BroadcastReceiver {
    BroadcastReceiver broadcastReceiver;

    @Override
    public void onReceive(Context context, Intent k2) {
        // TODO Auto-generated method stub

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(4000);

            createNotificationChannel(context);


            Intent intent1 = new Intent(context, NotificationRec.class); //same
            intent1.setAction("Stop Alarm");
            intent1.putExtra("RES",true);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "My Channel")
                    .setSmallIcon(R.drawable.inr_logo)
                    .setContentTitle("Alert")
                    .setContentText("Remainder for Warafine Dose !!!")
                    .setContentIntent(pendingIntent1)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setDefaults(DEFAULT_SOUND)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(R.drawable.ic_arrow_back_24dp, "Stop Alarm",
                            pendingIntent1);


            // Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(2, builder.build());
        try {
            Intent intentService = new Intent(context, RingtoneService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intentService );
            } else {
                context.startService(intentService );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        context.getApplicationContext().startService(new Intent(context, RingtoneService.class));
//            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//            if (alarmUri == null) {
//                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            }
//
//            // setting default ringtone
//            Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//
//            // play ringtone

//            ringtone.play();

    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Remainder for Warafine Dose";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel( "My Channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager =  context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
