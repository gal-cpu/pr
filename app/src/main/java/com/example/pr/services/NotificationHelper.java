package com.example.pr.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.pr.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "order_channel";
    private static final String CHANNEL_NAME = "Order Updates";

    public static void sendOrderReadyNotification(Context context, String orderId) {

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // יצירת Channel (חובה מ-Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("ההזמנה אושרה! 🎉")
                .setContentText("הזמנה מספר " + orderId + " מוכנה לאיסוף.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(orderId.hashCode(), builder.build());
    }
}