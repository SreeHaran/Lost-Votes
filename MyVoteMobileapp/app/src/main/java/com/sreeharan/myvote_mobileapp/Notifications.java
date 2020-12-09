package com.sreeharan.myvote_mobileapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notifications extends Application {
    public static final String NOTIFY_CHANNEL = "Channel 1";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(NOTIFY_CHANNEL
            ,"Coercion", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Testing the voter for coercion");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }


    }
}
