package com.example.alexiv.dailyselfiewithtoolbar.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.alexiv.dailyselfiewithtoolbar.MainActivity;

/**
 * Created by Alex on 8/24/2015.
 */

/**
 * Recieves broadcasts from alarm
 */
public class FireNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Starts mainActivity
        PendingIntent notifIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        // Send notification
        Notification.Builder mNotificationBuilder = new Notification.Builder(context)
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setContentTitle("Daily Selfie")
                .setContentText("I want you to take selfie right now!")
                .setAutoCancel(true)
                .setContentIntent(notifIntent);
        ((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE)).notify(777,
                mNotificationBuilder.build());
    }
}
