package com.cubbysulotions.proo.Calendar.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.cubbysulotions.proo.MainActivity.MainActivity;
import com.cubbysulotions.proo.SplashScreen;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "SAMPLE_CHANNEL";

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onReceive(Context context, Intent intent) {
        //Get id and message from intent

        int notificationID = intent.getIntExtra("notificationID", 0);
        int requestCode = intent.getIntExtra("requestCode", 0);
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("content");
        if(message.equals("")){
            message = "No content";
        }
        String del = intent.getStringExtra("delete");

        //When notification is tapped call main activity
        Intent main = new Intent(context, SplashScreen.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                requestCode,
                main,
                PendingIntent.FLAG_MUTABLE);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        //Prepare notification
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //for api 26 and above
            CharSequence channel_name = "My Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channel_name, importance);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_ID);
        }



        //notify

        if(del.equals("delete")){
            notificationManager.cancel(notificationID);
        } else {
            notificationManager.notify(notificationID, builder.build());
        }
    }
}
