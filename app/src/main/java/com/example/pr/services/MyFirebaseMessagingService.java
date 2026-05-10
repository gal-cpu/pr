package com.example.pr.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;




import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;


import com.example.pr.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//         TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }



        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {

            super.onMessageReceived(remoteMessage);

            String title =
                    remoteMessage.getNotification().getTitle();

            String body =
                    remoteMessage.getNotification().getBody();

            NotificationManager manager =
                    (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);

            String channelId = "orders_channel";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel =
                        new NotificationChannel(
                                channelId,
                                "Orders",
                                NotificationManager.IMPORTANCE_HIGH
                        );

                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, channelId)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setAutoCancel(true);

            manager.notify(1, builder.build());
        }
    }
